package org.fourstack.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class LibraryEventsProducer {

    private static int id = 0;

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    public ListenableFuture<SendResult<Integer, String>> publishLibraryEvent(LibraryEvent event) {
        try {
            if (event.getLibraryEventType() == LibraryEventType.NEW )
                event.setLibraryEventId(++id);

            Integer key = event.getLibraryEventId();
            String value = objectMapper.writeValueAsString(event);

            ListenableFuture<SendResult<Integer, String>> resultFuture = kafkaTemplate.sendDefault(key, value);
            resultFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    handleFailure(key, value, ex);
                }

                @Override
                public void onSuccess(SendResult<Integer, String> result) {
                    handleSuccess(key, value, result);
                }
            });

            return resultFuture;
        } catch (Exception e) {
            log.error("Exception occurred while publishing the LibraryEvent with eventId - {}",
                    event.getLibraryEventId(), e);
            throw new RuntimeException(e);
        }
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error occurred while sending message - {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in OnFailure: {}", e.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message published Successfully. Details are : key >> {}, value >> {}, partition >> {}",
                key, value, result.getRecordMetadata().partition());
    }

    public ListenableFuture<SendResult<Integer, String>> publishLibraryEvent_Approach2(String topic, LibraryEvent event) {
        try {
            if (event.getLibraryEventType() == LibraryEventType.NEW )
                event.setLibraryEventId(++id);

            Integer key = event.getLibraryEventId();
            String value = objectMapper.writeValueAsString(event);

            ProducerRecord<Integer, String> producerRecord = buildProducerRecord(topic, key, value);

            // publish the libraryEvent as ProducerRecord
            ListenableFuture<SendResult<Integer, String>> resultFuture = kafkaTemplate.send(producerRecord);
            resultFuture.addCallback(
                    success -> {
                        log.info("Successfully published the message to the Topic >> {}, key >> {}, value >> {}, partition",
                        topic, key, value, success.getRecordMetadata().partition());
                    },
                    failure -> {
                        log.error("Exception occurred while publishing the message - {}", failure.getMessage());
                        throw new RuntimeException(failure.getMessage());
                    }
            );

            return resultFuture;
        } catch (Exception e) {
            log.error("Exception caught : {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private ProducerRecord<Integer, String> buildProducerRecord(String topic, Integer key, String value) {
        List<Header> recordHeaders = List.of(
                new RecordHeader("record-event", "scanner".getBytes(StandardCharsets.UTF_8)),
                new RecordHeader("client-id", "library-event-producer".getBytes(StandardCharsets.UTF_8))
        );
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }
}
