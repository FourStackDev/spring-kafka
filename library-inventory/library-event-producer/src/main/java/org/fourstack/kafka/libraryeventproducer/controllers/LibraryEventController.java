package org.fourstack.kafka.libraryeventproducer.controllers;

import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;
import org.fourstack.kafka.libraryeventproducer.domain.LibraryEvent;
import org.fourstack.kafka.libraryeventproducer.producer.LibraryEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class LibraryEventController {

    @Autowired
    private LibraryEventsProducer eventsProducer;

    @PostMapping("/library-event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent event) {
        event.setLibraryEventType(LibraryEventType.NEW);
        eventsProducer.publishLibraryEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    @PostMapping("/library-event/approach2")
    public ResponseEntity<LibraryEvent> postLibraryEventApproach2(@RequestBody @Valid LibraryEvent event) {
        event.setLibraryEventType(LibraryEventType.NEW);
        eventsProducer.publishLibraryEvent_Approach2("library-events", event);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(event);
    }

    @PutMapping("/library-event")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent event) {

        if (event.getLibraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("LibraryEventId is Required.");
        }
        event.setLibraryEventType(LibraryEventType.UPDATE);
        eventsProducer.publishLibraryEvent_Approach2("library-events", event);

        return ResponseEntity.status(HttpStatus.OK)
                .body(event);
    }
}
