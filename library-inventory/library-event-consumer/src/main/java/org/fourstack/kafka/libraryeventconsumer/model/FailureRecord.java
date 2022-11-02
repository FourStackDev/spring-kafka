package org.fourstack.kafka.libraryeventconsumer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Document(collection = "failure_records")
public class FailureRecord {

    @Id
    private Integer id;
    private String topic;
    private Integer key;
    private String errorRecord;
    private Integer partition;
    private long offset_value;
    private String exception;
    private String status;


}
