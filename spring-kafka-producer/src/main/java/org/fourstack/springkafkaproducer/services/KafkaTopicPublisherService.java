package org.fourstack.springkafkaproducer.services;

import org.fourstack.springkafkaproducer.dao.EmployeeRepository;
import org.fourstack.springkafkaproducer.dao.StudentRepository;
import org.fourstack.springkafkaproducer.model.Employee;
import org.fourstack.springkafkaproducer.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

@Service
public class KafkaTopicPublisherService {


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private StudentRepository studentRepository;

    public String publishMessageToTopic(String topic, String key, Object data) {
        kafkaTemplate.send(topic, key, data);
        return "Message Published";
    }

    public Employee saveEmployee(Employee employee) {
        return empRepository.save(employee);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}
