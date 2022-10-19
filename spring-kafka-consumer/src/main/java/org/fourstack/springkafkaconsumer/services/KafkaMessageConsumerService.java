package org.fourstack.springkafkaconsumer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaconsumer.dao.EmployeeRepository;
import org.fourstack.springkafkaconsumer.dao.StudentRepository;
import org.fourstack.springkafkaconsumer.model.Employee;
import org.fourstack.springkafkaconsumer.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumerService {

    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private StudentRepository stdRepository;

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

    @KafkaListener(
            topics = "${kafka.topics.student-list-topic}",
            groupId = "${kafka.groups.student-data}",
            containerFactory = "studentListenerFactory"
    )
    public void consumeStudentData(Student student) {
        System.out.println(student);

        try {
            stdRepository.save(student);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
