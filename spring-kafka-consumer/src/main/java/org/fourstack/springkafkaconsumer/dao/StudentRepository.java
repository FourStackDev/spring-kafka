package org.fourstack.springkafkaconsumer.dao;

import org.fourstack.springkafkaconsumer.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
}
