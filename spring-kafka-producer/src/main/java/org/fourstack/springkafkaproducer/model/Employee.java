package org.fourstack.springkafkaproducer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fourstack.springkafkaproducer.codetype.Department;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = -5980879186261647199L;

    private String id;
    private String name;
    private double salary;
    private Department department;

    public Employee(String name, double salary, Department department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }
}
