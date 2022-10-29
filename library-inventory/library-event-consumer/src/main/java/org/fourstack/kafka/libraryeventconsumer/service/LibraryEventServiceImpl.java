package org.fourstack.kafka.libraryeventconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.dao.LibraryEventRepository;
import org.fourstack.kafka.libraryeventconsumer.model.LibraryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventServiceImpl implements LibraryEventService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LibraryEventRepository eventRepository;

    @Override
    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            String value = consumerRecord.value();
            LibraryEvent libraryEvent = objectMapper.readValue(value, LibraryEvent.class);
            eventRepository.save(libraryEvent);
        } catch (Exception e) {
            log.error("Exception occurred while processing the LibraryEvent : {}", e.getMessage());
        }
    }
}
