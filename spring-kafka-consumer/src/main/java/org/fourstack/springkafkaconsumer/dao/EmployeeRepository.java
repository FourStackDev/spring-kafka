package org.fourstack.springkafkaconsumer.dao;

import org.fourstack.springkafkaconsumer.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
}
