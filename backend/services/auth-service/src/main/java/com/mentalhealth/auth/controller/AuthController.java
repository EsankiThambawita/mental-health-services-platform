package com.mentalhealth.auth.controller;

import com.mentalhealth.auth.dto.AuthResponse;
import com.mentalhealth.auth.dto.LoginRequest;
import com.mentalhealth.auth.dto.SignupRequest;
import com.mentalhealth.auth.dto.ValidateTokenResponse;
import com.mentalhealth.auth.service.AuthService;
import jakarta.validation.Valid;
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
}