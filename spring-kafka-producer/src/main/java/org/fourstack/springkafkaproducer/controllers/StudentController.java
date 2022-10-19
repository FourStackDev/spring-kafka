package org.fourstack.springkafkaproducer.controllers;

import org.fourstack.springkafkaproducer.model.Student;
import org.fourstack.springkafkaproducer.services.KafkaTopicPublisherService;
import org.fourstack.springkafkaproducer.util.RandomIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    @Autowired
    private KafkaTopicPublisherService topicPublisherService;

    @PostMapping("/student")
    public Student createStudent(@RequestBody Student student) {

        // Generate Id
        if (Objects.nonNull(student) && Objects.isNull(student.getId())) {
            student.setId(RandomIdGenerator.generateRandomId());
        }

        topicPublisherService.publishMessageToTopic(
                "student-list-topic",
                student.getDepartment().toString(),
                student
        );

        System.out.println(student);
        topicPublisherService.saveStudent(student);
        return student;
    }
}
