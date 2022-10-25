package org.fourstack.kafka.libraryeventproducer.controllers;

import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.Book;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventControllerIntgTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testPostLibraryEvent() {

        String url = "/api/v1/library-event";

        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, LibraryEventType.NEW);

        // Create an HttpEntity with headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(event, headers);

        // post the created entity using TestRestTemplate
        ResponseEntity<LibraryEvent> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                LibraryEvent.class
        );

        assert response != null;
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testPostLibraryEventApproach2() {
        String url = "/api/v1/library-event/approach2";
        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, LibraryEventType.NEW);

        // Create HttpEntity with headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(event, headers);

        // Post the entity using TestRestTemplate
        ResponseEntity<LibraryEvent> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                LibraryEvent.class
        );

        assert response != null;
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
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
