package org.fourstack.kafka.libraryeventconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.fourstack.kafka.libraryeventconsumer.constants.EventsConsumerConstants;
import org.fourstack.kafka.libraryeventconsumer.service.FailureRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fourstack.kafka.libraryeventconsumer.constants.EventsConsumerConstants.DEAD;
import static org.fourstack.kafka.libraryeventconsumer.constants.EventsConsumerConstants.RETRY;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${spring.kafka.topic.retry}")
    private String retryTopic;

    @Value("${spring.kafka.topic.dlt}")
    private String deadLetterTopic;

    @Autowired
    private FailureRecordService failureRecordService;

    /*
     * KafkaTemplate Bean is created for the purpose of Recovery logic,
     * where failed Records will be published to the new retry topics using the KafkaTemplate
     */
    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<Integer, String> factory = new DefaultKafkaProducerFactory<>(producerProps);

        KafkaTemplate<Integer, String> template = new KafkaTemplate<>(factory);
        return template;
    }

    public DeadLetterPublishingRecoverer publishingRecoverer(KafkaTemplate<Integer, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, exception) -> {
                    if (exception.getCause() instanceof RecoverableDataAccessException) {
                        return new TopicPartition(retryTopic, consumerRecord.partition());
                    } else {
                        return new TopicPartition(deadLetterTopic, consumerRecord.partition());
                    }
                });
        return recoverer;
    }

    ConsumerRecordRecoverer consumerRecordRecoverer = (consumerRecord, exception) -> {
        var record  =(ConsumerRecord<Integer, String>) consumerRecord;
      if (exception.getCause() instanceof RecoverableDataAccessException) {
          failureRecordService.saveFailedRecord(record, exception, RETRY);
      } else {
          failureRecordService.saveFailedRecord(record, exception, DEAD);
      }
    };

    /**
     * ErrorHandler to handle the errors of Kafka Consumer.
     *
     * @return
     */
    public DefaultErrorHandler errorHandler() {
        //create a BackOff with 1 second interval and 5 attempts
        var backOff = new FixedBackOff(1000L, 5);
        var errorHandler = new DefaultErrorHandler(publishingRecoverer(kafkaTemplate()), backOff);

        var exceptionsIgnoreList = List.of(
                IllegalArgumentException.class,
                ArithmeticException.class
        );
        exceptionsIgnoreList.forEach(errorHandler::addNotRetryableExceptions);

        // Set Retry Listeners to ErrorHandler.
        errorHandler.setRetryListeners(
                (record, ex, deliveryAttempt) -> {
                    log.info("Failed record in Retry attempt, Exception - {}, deliveryAttempt - {}",
                            ex.getMessage(), deliveryAttempt);
                }
        );
        return errorHandler;
    }

    public Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return props;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    listenerFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }
}
