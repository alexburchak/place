package org.alexburchak.place.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class FrontendApplication {
	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}
}
