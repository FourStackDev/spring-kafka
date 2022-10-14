package org.fourstack.springkafkaproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.partition-count}")
    private int partitionCount;

    @Value("${spring.kafka.replica-count}")
    private int replicaCount;


    /**
     * Bean method to create Topic.
     *
     * @return Topic Object.
     */
    @Bean
    public NewTopic flinkDataTopic() {
        return TopicBuilder.name("flink-data-topic")
                .partitions(partitionCount)
                .replicas(replicaCount)
                .build();
    }
}
