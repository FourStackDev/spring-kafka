package org.fourstack.springkafkaconsumer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaconsumer.Employee;
import org.fourstack.springkafkaconsumer.dao.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumerService {

    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.flink-data-topic}",
            groupId = "${kafka.groups.flink-data-group}"
    )
    public void consumeDataFromTopic(String data) {
        System.out.println("Received Data: "+data);
    }

    @KafkaListener(
            topics = "${kafka.topics.emp-list-topic}",
            groupId = "${kafka.groups.employee-data}"
    )
    public void consumeEmployeeData(String empData) {
        System.out.println(empData);

        try {
            Employee employee = objectMapper.readValue(empData, Employee.class);
            empRepository.save(employee);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
