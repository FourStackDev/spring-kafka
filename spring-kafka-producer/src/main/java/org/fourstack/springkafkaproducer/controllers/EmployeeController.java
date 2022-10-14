package org.fourstack.springkafkaproducer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaproducer.model.Employee;
import org.fourstack.springkafkaproducer.services.KafkaTopicPublisherService;
import org.fourstack.springkafkaproducer.util.RandomIdGenerator;

import static org.springframework.http.MediaType.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    @Autowired
    private KafkaTopicPublisherService topicPublisherService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(path = "/employee", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Employee createEmployee(@RequestBody Employee employee) {

        // Generate the Employee ID
        if (Objects.nonNull(employee) && Objects.isNull(employee.getId())) {
            employee.setId(RandomIdGenerator.generateRandomId());
        }

        try {
            // publish the message to Kafka
            topicPublisherService.publishMessageToTopic(
                    "emp-list-topic",
                    employee.getId(),
                    objectMapper.writeValueAsString(employee)
            );

            topicPublisherService.saveEmployee(employee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employee;
    }
}
