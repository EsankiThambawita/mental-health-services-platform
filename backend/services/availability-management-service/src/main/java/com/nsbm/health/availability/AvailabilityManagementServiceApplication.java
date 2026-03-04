package com.nsbm.health.availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class AvailabilityManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvailabilityManagementServiceApplication.class, args);
	}
}