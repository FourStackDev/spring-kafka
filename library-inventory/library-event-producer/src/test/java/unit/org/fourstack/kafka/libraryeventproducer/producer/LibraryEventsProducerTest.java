package org.fourstack.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.Book;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class LibraryEventsProducerTest {

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LibraryEventsProducer eventsProducer;

    @Test
    public void test_publishLibraryEvent_Approach2_onFailure() {
        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, LibraryEventType.NEW);

        SettableListenableFuture future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception while calling Kafka"));

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(future);

        Assertions.assertThrows(Exception.class,
                () -> eventsProducer.publishLibraryEvent_Approach2("library-events", event).get());
    }

    @Test
    public void test_publishLibraryEvent_Approach2_onSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {
        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, LibraryEventType.NEW);

        //Build ProducerRecord, RecordMetaData and SendResult
        String value = objectMapper.writeValueAsString(event);
        String topic = "library-event";
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(
                topic,
                event.getLibraryEventId(),
                value
        );
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition(topic, 3),
                1, 1, System.currentTimeMillis(), 234, 123
        );
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);

        // Create a ListenableFuture Instance.
        SettableListenableFuture future = new SettableListenableFuture<>();
        future.set(sendResult);

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(future);

        ListenableFuture<SendResult<Integer, String>> future1 = eventsProducer.publishLibraryEvent_Approach2(topic, event);
        SendResult<Integer, String> sendResult1 = future1.get();

        Assertions.assertEquals(3, sendResult1.getRecordMetadata().partition());

    }

    private LibraryEvent getLibraryEvent(Book book, LibraryEventType eventType) {
        return LibraryEvent.builder()
                .libraryEventId(null)
                .libraryEventType(eventType)
                .book(book)
                .build();
    }

    private Book getBook() {
        return Book.builder()
                .bookId(2341)
                .author("Manjunath")
                .name("Spring Boot with Kafka")
                .build();
    }
}
