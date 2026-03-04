package com.mentalhealth.auth.controller;

import com.mentalhealth.auth.dto.AuthResponse;
import com.mentalhealth.auth.dto.LoginRequest;
import com.mentalhealth.auth.dto.SignupRequest;
import com.mentalhealth.auth.dto.ValidateTokenResponse;
import com.mentalhealth.auth.model.User;
import com.mentalhealth.auth.service.AuthService;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Base URL: http://localhost:8084/api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        try {
            AuthResponse response = authService.signup(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // POST /api/auth/validate
    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        ValidateTokenResponse response = authService.validateToken(authHeader);

        if (response.isValid()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // GET /api/auth/health
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Auth Service is running");
    }

    @GetMapping("/userIdByEmail")
    public ResponseEntity<?> getUserIdByEmail(
            @RequestParam String email,
            @RequestHeader("Authorization") String token) {

        try {
            // Validate requester's token first
            ValidateTokenResponse tokenValidation = authService.validateToken(token);
            if (!tokenValidation.isValid()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Look up user by email
            User user = authService.getUserByEmail(email);

            Map<String, String> response = new java.util.HashMap<>();
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole().toString());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found with email: " + email));
        }
    }
}