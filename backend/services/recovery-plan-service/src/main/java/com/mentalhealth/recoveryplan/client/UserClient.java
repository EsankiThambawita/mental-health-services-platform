package com.mentalhealth.recoveryplan.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUserById(String userId) {
        String url = "http://localhost:8082/users/" + userId;
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public static class UserDTO {
        public String id;
        public String username;
        public String role;
    }
}
