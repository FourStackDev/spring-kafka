package org.fourstack.springkafkaproducer.data.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaproducer.codetype.Department;
import org.fourstack.springkafkaproducer.model.Employee;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class EmployeeDataGenerator {

    private static String[] names = {
            "Manjunath", "Manoj", "Vinay", "Raju", "Kiran", "Sunil", "Sachin", "Likitha",
            "Deepika", "Deepa", "Pallavi", "Sushma", "Kruthika", "Keerthan", "Kushal",
            "Kishan", "Anusha", "Rashmi", "Poornima"
    };

    private static Department[] departments = {
            Department.FINANCE, Department.MARKETING,
            Department.HUMAN_RESOURCE, Department.OPERATIONS,
            Department.PRODUCT_DEVELOPMENT, Department.SALES
    };

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        HttpClient httpClient = HttpClient.newHttpClient();

        String url = "http://localhost:8080/kafka-producer-app/api/v1/employee";
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            Employee employee = new Employee(
                    names[random.nextInt(names.length)],
                    random.nextDouble() * 100000,
                    departments[random.nextInt(departments.length)]
            );

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(employee)))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println(response.statusCode());

                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
