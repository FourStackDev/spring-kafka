package org.fourstack.kafka.libraryeventproducer.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
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

@ExtendWith(MockitoExtension.class)
public class LibraryEventsProducerTest {

    @Mock
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LibraryEventsProducer eventsProducer;

    @Test
    public void test_publishLibraryEvent_Approach2() {
        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, LibraryEventType.NEW);

        SettableListenableFuture future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Exception while calling Kafka"));

        Mockito.when(kafkaTemplate.send(Mockito.any(ProducerRecord.class)))
                .thenReturn(future);

        Assertions.assertThrows(Exception.class,
                () -> eventsProducer.publishLibraryEvent_Approach2("library-events", event).get());
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
