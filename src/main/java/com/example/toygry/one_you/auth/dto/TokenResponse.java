package com.example.toygry.one_you.auth.dto;

/**
 * DTO for token response
 */
public class TokenResponse {
    private String token;

    // Default constructor for JSON serialization
    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}