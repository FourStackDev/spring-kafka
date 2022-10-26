package org.fourstack.kafka.libraryeventproducer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {


    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    private Map<String, Object> producerConfigProps() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "200");

        return properties;
    }

    /**
     * ProducerFactory Bean needed for KafkaTemplate Creation.
     * Factory Object will be created by using producer config properties.
     *
     * @return
     */
    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigProps());
    }

    /**
     * Create KafkaTemplate Bean with key.serializer as Integer and value.serializer as
     * String because ObjectMapper will be used and object will be converted to String.
     *
     * @param producerFactory
     * @return
     */
    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate(ProducerFactory<Integer, String> producerFactory) {
        KafkaTemplate<Integer, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // setting the default topic, Can be configured using properties also.
        kafkaTemplate.setDefaultTopic("library-events");
        return kafkaTemplate;
    }
}
