package org.fourstack.springkafkaproducer.dao;

import org.fourstack.springkafkaproducer.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
}
