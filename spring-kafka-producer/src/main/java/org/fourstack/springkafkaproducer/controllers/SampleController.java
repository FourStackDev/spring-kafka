package org.fourstack.springkafkaproducer.controllers;

import org.fourstack.springkafkaproducer.services.KafkaTopicPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SampleController {

    @Autowired
    private KafkaTopicPublisherService topicPublisherService;

    @PostMapping("/publish-data")
    public String sendMessageToKafka(@RequestBody String message) {
        return topicPublisherService.publishMessageToTopic("flink-data-topic", message, message);
    }

    @GetMapping("/data")
    public String getData() {
        return "App started";
    }
}
