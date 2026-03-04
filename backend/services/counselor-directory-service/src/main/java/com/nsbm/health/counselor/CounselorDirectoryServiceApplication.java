package com.nsbm.health.counselor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point of the Counselor Directory Service
 */
@SpringBootApplication
@EnableFeignClients  // Add this annotation to enable Feign clients
public class CounselorDirectoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CounselorDirectoryServiceApplication.class, args);
    }
}