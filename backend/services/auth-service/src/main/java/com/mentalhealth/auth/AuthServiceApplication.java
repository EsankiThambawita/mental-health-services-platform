package com.mentalhealth.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("\nAuth Service is running on http://localhost:8084");
        System.out.println("Endpoints:");
        System.out.println("   POST /api/auth/signup - User registration");
        System.out.println("   POST /api/auth/login - User login");
        System.out.println("   POST /api/auth/validate - Token validation (inter-service)\n");
    }
}