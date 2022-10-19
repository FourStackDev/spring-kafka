package org.fourstack.springkafkaproducer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fourstack.springkafkaproducer.codetype.StdDepartment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = -5293198999604414903L;

    @Id
    private String id;
    private String name;
    private StdDepartment department;
    private String collegeName;

    public Student(String name, StdDepartment department, String collegeName) {
        this.name = name;
        this.department = department;
        this.collegeName = collegeName;
    }


}
