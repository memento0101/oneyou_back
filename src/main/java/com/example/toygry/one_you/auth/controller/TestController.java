package com.example.toygry.one_you.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing authentication
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * Test endpoint that requires authentication
     * Returns the authenticated user's details
     *
     * @param authentication The authenticated user
     * @return User details
     */
    @GetMapping("/user")
    public Map<String, Object> getUserDetails(Authentication authentication) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", authentication.getName());
        userDetails.put("authorities", authentication.getAuthorities());
        userDetails.put("details", authentication.getDetails());
        userDetails.put("authenticated", authentication.isAuthenticated());
        return userDetails;
    }
}