package org.fourstack.kafka.libraryeventconsumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.dao.LibraryEventRepository;
import org.fourstack.kafka.libraryeventconsumer.model.Book;
import org.fourstack.kafka.libraryeventconsumer.model.LibraryEvent;
import org.fourstack.kafka.libraryeventconsumer.service.LibraryEventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(
        properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"
        }
)
public class LibraryEventsConsumerTest {

    // Create an Embedded Kafka Broker
    @Autowired
    EmbeddedKafkaBroker kafkaBroker;

    // Autowire the KafkaTemplate using test properties
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    ObjectMapper mapper;

    @SpyBean
    LibraryEventsConsumer libraryEventsConsumerSpy;

    @SpyBean
    LibraryEventService libraryEventServiceSpy;

    @Autowired
    LibraryEventRepository repository;

    @BeforeEach
    public void setUp() {

        mapper = new ObjectMapper();

        for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, kafkaBroker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void publishNewLibraryEvents() throws JsonProcessingException, ExecutionException, InterruptedException {
        LibraryEvent event = getLibraryEvent("NEW");
        String value = mapper.writeValueAsString(event);

        // publish to kafkaTemplate and block the asynchronous call.
        // sendDefault() will send data to default topic configured
        kafkaTemplate.sendDefault(value).get();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);


        Mockito.verify(libraryEventsConsumerSpy, Mockito.times(1))
                .consumeLibraryEvents(Mockito.isA(ConsumerRecord.class));
        Mockito.verify(libraryEventServiceSpy, Mockito.times(1))
                .processLibraryEvent(Mockito.isA(ConsumerRecord.class));

        List<LibraryEvent> events = repository.findAll();
        assert events.size() == 1;
        Assertions.assertEquals("Manjunath", events.get(0).getBook().getAuthor());
    }

    @Test
    public void updateLibraryEvents() throws JsonProcessingException, ExecutionException, InterruptedException {
        // save the record to Database
        LibraryEvent event = getLibraryEvent("NEW");
        repository.save(event);

        // Update Book name
        event.setLibraryEventType("UPDATE");
        event.getBook().setName("Spring Kafka 2.0");
        String value = mapper.writeValueAsString(event);

        System.out.println("Publishing the record");
        // publish the record to Kafka
        kafkaTemplate.sendDefault(value).get();

        System.out.println("CountDown Latch");
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        // Verifying using Mockito
        System.out.println("Verifying using Mockito");
        Mockito.verify(libraryEventsConsumerSpy, Mockito.times(1))
                .consumeLibraryEvents(Mockito.any(ConsumerRecord.class));
        Mockito.verify(libraryEventServiceSpy, Mockito.times(1))
                .processLibraryEvent(Mockito.any(ConsumerRecord.class));

        System.out.println("Fetch the persisted Event");
        LibraryEvent persistedEvent = repository.findById(event.getLibraryEventId()).get();
        Assertions.assertEquals("Spring Kafka 2.0", persistedEvent.getBook().getName());

    }

    private LibraryEvent getLibraryEvent(String eventType) {
        Random random = new Random();
        return LibraryEvent.builder()
                .libraryEventId(random.nextInt())
                .libraryEventType(eventType)
                .book(getBook(random))
                .build();
    }

    private Book getBook(Random random) {
        return Book.builder()
                .bookId(random.nextInt())
                .name("Spring Boot Kafka")
                .author("Manjunath")
                .build();
    }
}
