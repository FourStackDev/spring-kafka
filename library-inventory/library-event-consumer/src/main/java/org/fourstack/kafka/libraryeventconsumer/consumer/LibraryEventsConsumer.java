package org.fourstack.kafka.libraryeventconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsConsumer {


    @KafkaListener(
            topics = {"${spring.kafka.topic.library-events}"},
            groupId = "${spring.kafka.consumer.group.library-events-consumer-grp}"
    )
    public void consumeLibraryEvents(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Consumed message : {}", consumerRecord.value());
        log.info("Consumer-Record: {}", consumerRecord);
    }
}
