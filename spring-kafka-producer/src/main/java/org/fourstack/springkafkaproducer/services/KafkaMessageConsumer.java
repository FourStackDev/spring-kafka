package org.fourstack.springkafkaproducer.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// @Service
public class KafkaMessageConsumer {


    @KafkaListener(
            topics = "flink-data-topic",
            groupId = "flink-data-group"
    )
    public void consumeDataFromTopic(String data) {
        System.out.println("Message Recieved :" +data +" !!!!!!!!!!!!!!!!!!!!!!!");
    }
}
