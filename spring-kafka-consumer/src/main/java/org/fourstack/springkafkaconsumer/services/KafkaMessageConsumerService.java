package org.fourstack.springkafkaconsumer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaconsumer.dao.EmployeeRepository;
import org.fourstack.springkafkaconsumer.model.Employee;
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
            groupId = "${kafka.groups.employee-data}",
            containerFactory = "empListenerFactory"
    )
    public void consumeEmployeeData(Employee employee) {
        System.out.println(employee);

        try {
            //Employee employee = objectMapper.readValue(empData, Employee.class);
            empRepository.save(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
