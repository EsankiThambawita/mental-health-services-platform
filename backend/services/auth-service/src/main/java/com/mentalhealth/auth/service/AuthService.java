package com.mentalhealth.auth.service;

import com.mentalhealth.auth.dto.AuthResponse;
import com.mentalhealth.auth.dto.LoginRequest;
import com.mentalhealth.auth.dto.SignupRequest;
import com.mentalhealth.auth.dto.ValidateTokenResponse;
import com.mentalhealth.auth.model.User;
import com.mentalhealth.auth.repository.UserRepository;
import com.mentalhealth.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // Hash password
                request.getName(),
                request.getRole());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().toString());

        return new AuthResponse(
                token,
                savedUser.getId(), // This is counselorId or userId
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString());

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole());
    }

    public ValidateTokenResponse validateToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtil.isTokenExpired(token)) {
                return new ValidateTokenResponse(
                        false,
                        null,
                        null,
                        null,
                        "Token expired");
            }

            String userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            String roleStr = jwtUtil.getRoleFromToken(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new ValidateTokenResponse(
                    true,
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    "Token valid");

        } catch (Exception e) {
            return new ValidateTokenResponse(
                    false,
                    null,
                    null,
                    null,
                    "Invalid token: " + e.getMessage());
        }
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}