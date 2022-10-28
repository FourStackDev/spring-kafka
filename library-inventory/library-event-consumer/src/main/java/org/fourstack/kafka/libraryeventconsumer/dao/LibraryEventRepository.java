package org.fourstack.kafka.libraryeventconsumer.dao;

import org.fourstack.kafka.libraryeventconsumer.model.LibraryEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LibraryEventRepository extends MongoRepository<LibraryEvent, Integer> {
}
