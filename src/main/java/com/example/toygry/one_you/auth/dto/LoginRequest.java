package com.example.toygry.one_you.auth.dto;

/**
 * DTO for login request
 */
public class LoginRequest {
    private String username;
    private String password;

    // Default constructor for JSON deserialization
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}