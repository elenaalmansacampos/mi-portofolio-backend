package com.elena.portafolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PortafolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortafolioApplication.class, args);
	}

}
