package org.fourstack.kafka.libraryeventproducer.controllers;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.Book;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
public class LibraryEventControllerIntgTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    public void setUp() {
        String consumerGroup = "events-consumer-test-group";
        String autoCommit = "true";
        Map<String, Object> consumerProps = KafkaTestUtils
                .consumerProps(consumerGroup, autoCommit, embeddedKafkaBroker);
        consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new IntegerDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }

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

        // consume the record from the topic using KafkaTestUtils.
        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");

        assert consumerRecord != null;
        assertNotNull(consumerRecord.value());
        assertNotNull(consumerRecord.key());
    }

    @Test
    @Timeout(5)
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

        // Consume the Record using KafkaTestUtils.
        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
        assert consumerRecord != null;
        assertNotNull(consumerRecord.key());
        assertNotNull(consumerRecord.value());
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
