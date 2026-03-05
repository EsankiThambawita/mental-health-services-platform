package com.mentalhealth.auth.dto;

import com.mentalhealth.auth.model.UserRole;

public class ValidateTokenResponse {

    private boolean valid;
    private String userId;
    private String email;
    private UserRole role;
    private String message;

    public ValidateTokenResponse() {
    }

    public ValidateTokenResponse(boolean valid, String userId, String email, UserRole role, String message) {
        this.valid = valid;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.message = message;
    }

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}