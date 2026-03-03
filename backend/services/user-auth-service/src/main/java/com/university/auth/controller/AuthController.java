package com.university.auth.controller;

import com.university.auth.model.User;
import com.university.auth.repository.UserRepository;
import com.university.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository; // <-- add this

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository; // <-- initialize it
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody SignUpRequest request) {
        return authService.signUp(request.username, request.email, request.password, request.role);
    }

    @PostMapping("/signin")
    public User signIn(@RequestBody SignInRequest request) {
        return authService.signIn(request.email, request.password);
    }

    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable String id) {
        User user = userRepository.findById(id) // <-- use the instance, not the interface
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user.getId(), user.getUsername(), user.getRole());
    }

    // DTOs
    public static class SignUpRequest {
        public String username;
        public String email;
        public String password;
        public String role;
    }

    public static class SignInRequest {
        public String email;
        public String password;
    }

    public static class UserDTO {
        public String id;
        public String username;
        public String role;

        public UserDTO(String id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
    }
}
