package org.fourstack.kafka.libraryeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class LibraryEventConsumerWithAck implements AcknowledgingMessageListener<Integer, String> {

    @Override
    @KafkaListener(
            topics = {"${spring.kafka.topic.library-events}"},
            groupId = "${spring.kafka.consumer.group.library-events-consumer-grp}"
    )
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumer-Record: {}", consumerRecord);
        acknowledgment.acknowledge();
    }
}
