package org.fourstack.kafka.libraryeventconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fourstack.kafka.libraryeventconsumer.model.Book;
import org.fourstack.kafka.libraryeventconsumer.model.LibraryEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class LibraryEventsGenerator {

    private static String[] names = {
            "Manjunath", "Manoj", "Raju", "Vinay", "Sunil",
            "Deepika", "Kiran", "Pooja", "Likitha", "Sachin",
            "Suhas"
    };

    private static String[] bookNames = {
            "Spring FrameWork", "FrontEnd - Angular", "FrontEnd - React",
            "My Experiments on Life", "Daily Circus", "Rajadhani Express"
    };

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        try (ServerSocket server = new ServerSocket(8090)) {
            Socket socket = server.accept();
            System.out.println("Got new Connection : " + socket.toString());

            try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                Random random = new Random();

                HttpClient httpClient = HttpClient.newHttpClient();
                String url = "http://localhost:8080/library-event-producer/api/v1/library-event/approach2";

                while (true) {
                    Book book = Book.builder()
                            .bookId(random.nextInt())
                            .name(bookNames[random.nextInt(bookNames.length)])
                            .author(names[random.nextInt(names.length)])
                            .build();
                    LibraryEvent event = LibraryEvent.builder()
                            .book(book)
                            .build();

                    String eventValue = mapper.writeValueAsString(event);

                    writer.println(event);
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .POST(HttpRequest.BodyPublishers.ofString(eventValue))
                            .header("Content-Type", "application/json")
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.statusCode());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
