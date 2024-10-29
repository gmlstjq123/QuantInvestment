package com.example.QuantInvestigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class QuantInvestigationApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantInvestigationApplication.class, args);
	}

}
