package org.fourstack.kafka.libraryeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.service.LibraryEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsRetryConsumer {

    @Autowired
    private LibraryEventService service;


    @KafkaListener(
            topics = {"${spring.kafka.topic.retry}"},
            groupId = "library-events-consumer-retry-grp"
    )
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer Record in Retry: {}", consumerRecord);
        service.processLibraryEvent(consumerRecord);
    }
}
