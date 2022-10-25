package org.fourstack.kafka.libraryeventproducer.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.Book;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.fourstack.kafka.libraryeventproducer.producer.LibraryEventsProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(LibraryEventController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LibraryEventsProducer eventsProducer;

    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void destroy() {
        objectMapper = null;
    }

    @Test
    public void testPostLibraryEvent() throws Exception {
        // Create the pojo objects.
        Book book = getBook();
        LibraryEvent event = getLibraryEvent(book, null);

        String endPoint = "/api/v1/library-event";

        // Mock the void method.
        Mockito.doNothing()
                .when(eventsProducer)
                .publishLibraryEvent(Mockito.any(LibraryEvent.class));

        String content = objectMapper.writeValueAsString(event);
        mockMvc.perform(
                MockMvcRequestBuilders.post(endPoint)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
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
