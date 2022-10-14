package org.fourstack.springkafkaproducer.services;

import org.fourstack.springkafkaproducer.dao.EmployeeRepository;
import org.fourstack.springkafkaproducer.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicPublisherService {


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmployeeRepository empRepository;

    public String publishMessageToTopic(String topic, String key, String data) {
        kafkaTemplate.send(topic, key, data);
        return "Message Published";
    }

    public Employee saveEmployee(Employee employee) {
        return empRepository.save(employee);
    }

}
