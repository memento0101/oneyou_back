package com.example.toygry.one_you.auth.dto;

/**
 * DTO for login request
 */
public record LoginRequest(
    String username,
    String password
) {}