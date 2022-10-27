package org.fourstack.kafka.libraryeventconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class LibraryEventConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventConsumerApplication.class, args);
	}

}
