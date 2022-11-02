package org.fourstack.kafka.libraryeventconsumer.dao;

import org.fourstack.kafka.libraryeventconsumer.model.FailureRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FailureRecordRepository extends MongoRepository<FailureRecord, Integer> {
    List<FailureRecord> findAllByStatus(String status);
}
