package com.chhatkh.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RenthubApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenthubApiApplication.class, args);
	}

}
