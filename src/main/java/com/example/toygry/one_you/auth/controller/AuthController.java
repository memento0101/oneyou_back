//package com.example.toygry.one_you.auth.controller;
//
//import com.example.toygry.one_you.auth.dto.LoginRequest;
//import com.example.toygry.one_you.auth.dto.TokenResponse;
//import com.example.toygry.one_you.auth.service.JwtTokenService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Controller for handling authentication requests
// */
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    private final JwtTokenService jwtTokenService;
//
//
//    @Autowired
//    public AuthController(
//            JwtTokenService jwtTokenService
//    ) {
//        this.jwtTokenService = jwtTokenService;
//    }
//
//    /**
//     * Authenticate user and generate JWT token
//     *
//     * @param loginRequest Login credentials
//     * @return JWT token
//     */
//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
//        // Return token response
//        return ResponseEntity.ok(jwtTokenService.generateToken(loginRequest));
//    }
//
//    /**
//     * Check if the current token is valid
//     * This endpoint is protected, so it will return 401 if the token is invalid
//     *
//     * @param authentication The authenticated user
//     * @return Token validity status
//     */
//    @GetMapping("/token/validate")
//    public Map<String, Object> validateToken(Authentication authentication) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("valid", true);
//        response.put("username", authentication.getName());
//        return response;
//    }
//}
