package org.fourstack.kafka.libraryeventconsumer.dao;

import org.fourstack.kafka.libraryeventconsumer.model.FailureRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FailureRecordRepository extends MongoRepository<FailureRecord, Integer> {
}
