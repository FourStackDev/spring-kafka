package org.fourstack.springkafkaproducer.dao;

import org.fourstack.springkafkaproducer.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
