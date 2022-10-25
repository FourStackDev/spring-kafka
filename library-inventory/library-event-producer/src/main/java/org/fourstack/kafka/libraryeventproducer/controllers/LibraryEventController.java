package org.fourstack.kafka.libraryeventproducer.controllers;

import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.fourstack.kafka.libraryeventproducer.producer.LibraryEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class LibraryEventController {

    @Autowired
    private LibraryEventsProducer eventsProducer;

    @PostMapping("/library-event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Validated LibraryEvent event) {
        event.setLibraryEventType(LibraryEventType.NEW);
        eventsProducer.publishLibraryEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    @PostMapping("/library-event/approach2")
    public ResponseEntity<LibraryEvent> postLibraryEventApproach2(@RequestBody @Validated LibraryEvent event) {
        event.setLibraryEventType(LibraryEventType.NEW);
        eventsProducer.publishLibraryEvent_Approach2("library-events", event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }
}
