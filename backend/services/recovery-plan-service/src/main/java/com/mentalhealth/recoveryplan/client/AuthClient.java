package com.mentalhealth.recoveryplan.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * AuthClient - Calls Auth Service to validate JWT tokens
 * 
 * This is the INTER-SERVICE COMMUNICATION component
 */
@Component
public class AuthClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ValidatedUser validateToken(String token) {
        try {
            String url = authServiceUrl + "/api/auth/validate";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<AuthResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AuthResponse.class);

            AuthResponse authResponse = response.getBody();

            if (authResponse != null && authResponse.valid) {
                return new ValidatedUser(
                        authResponse.userId,
                        authResponse.email,
                        authResponse.role);
            } else {
                throw new RuntimeException("Token validation failed");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to validate token: " + e.getMessage());
        }
    }

    /**
     * Response from Auth Service
     */
    public static class AuthResponse {
        public boolean valid;
        public String userId;
        public String email;
        public String role;
        public String message;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ValidatedUser {
        public String userId;
        public String email;
        public String role;

        public ValidatedUser(String userId, String email, String role) {
            this.userId = userId;
            this.email = email;
            this.role = role;
        }
    }
}