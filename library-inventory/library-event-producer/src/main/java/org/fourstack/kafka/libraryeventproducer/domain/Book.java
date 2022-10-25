package org.fourstack.kafka.libraryeventproducer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {

    private Integer bookId;

    // Validation constraint (name should not be null)
    @NotNull
    private String name;

    // Validation Constraint (author should not be null or empty)
    @NotBlank
    private String author;
}
