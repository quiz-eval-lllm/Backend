package com.ta.llmbackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ta.llmbackend.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootApplication
public class LlmbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmbackendApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.addAdmin();
		};
	}

}
