package org.fourstack.springkafkaproducer.data.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.springkafkaproducer.codetype.StdDepartment;
import org.fourstack.springkafkaproducer.model.Student;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class StudentDataGenerator {

    private static String[] names = {
            "Manjunath", "Manoj", "Vinay", "Raju", "Kiran", "Sunil", "Sachin", "Likitha",
            "Deepika", "Deepa", "Pallavi", "Sushma", "Kruthika", "Keerthan", "Kushal",
            "Kishan", "Anusha", "Rashmi", "Poornima", "Sudheer", "Mahesh", "Ramesh", "Archana",
            "Monisha", "Venkatesh"
    };

    private static StdDepartment[] departments = {
            StdDepartment.ELECTRONICS_AND_COMMUNICATION, StdDepartment.COMPUTER_SCIENCE,
            StdDepartment.CIVIL_ENGINEERING, StdDepartment.MECHANICAL_ENGINEERING,
            StdDepartment.ELECTRICAL_AND_ELECTRONICS, StdDepartment.TELECOMMUNICATION,
            StdDepartment.INDUSTRIAL_MANAGEMENT, StdDepartment.INFORMATION_SCIENCE
    };

    private static String[] collegeNames = {
            "Dr. AIT", "UVCE", "MVIT", "MSRIT", "SJCIT", "BMSIT", "BIT"
    };

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        HttpClient httpClient = HttpClient.newHttpClient();
        String url = "http://localhost:8080/kafka-producer-app/api/v1/student";
        Random random = new Random();

        for (int i = 0; i < 500; i++ ) {
            Student student = new Student(
                    names[random.nextInt(names.length)],
                    departments[random.nextInt(departments.length)],
                    collegeNames[random.nextInt(collegeNames.length)]
            );

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(student)))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println(response.statusCode());

                Thread.sleep(500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
