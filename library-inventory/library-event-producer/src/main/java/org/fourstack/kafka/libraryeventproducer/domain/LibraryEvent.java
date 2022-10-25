package org.fourstack.kafka.libraryeventproducer.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fourstack.kafka.libraryeventproducer.codetype.LibraryEventType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibraryEvent {

    private Integer libraryEventId;
    private LibraryEventType libraryEventType;

    @NotNull
    @Valid
    private Book book;
}
