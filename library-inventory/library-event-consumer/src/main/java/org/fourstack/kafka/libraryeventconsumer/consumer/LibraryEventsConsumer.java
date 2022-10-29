package org.fourstack.kafka.libraryeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.service.LibraryEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsConsumer {

    @Autowired
    private LibraryEventService libraryEventService;


    @KafkaListener(
            topics = {"${spring.kafka.topic.library-events}"},
            groupId = "${spring.kafka.consumer.group.library-events-consumer-grp}"
    )
    public void consumeLibraryEvents(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumer-Record: {}", consumerRecord);
        libraryEventService.processLibraryEvent(consumerRecord);
    }
}
