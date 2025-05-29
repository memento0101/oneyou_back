package com.example.toygry.one_you.auth.controller;

import com.example.toygry.one_you.auth.dto.LoginRequest;
import com.example.toygry.one_you.auth.dto.TokenResponse;
import com.example.toygry.one_you.auth.service.JwtTokenService;
import com.example.toygry.one_you.users.dto.UserInsertRequest;
import com.example.toygry.one_you.users.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling authentication requests
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final UsersService usersService;


    @Autowired
    public AuthController(
            JwtTokenService jwtTokenService,
            UsersService usersService
    ) {
        this.jwtTokenService = jwtTokenService;
        this.usersService = usersService;
    }

    /**
     * Authenticate user and generate JWT token
     *
     * @param loginRequest Login credentials
     * @return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        // Return token response
        return ResponseEntity.ok(jwtTokenService.generateToken(loginRequest));
    }

    /**
     * Check if the current token is valid
     * This endpoint is protected, so it will return 401 if the token is invalid
     *
     * @param authentication The authenticated user
     * @return Token validity status
     */
    @GetMapping("/token/validate")
    public Map<String, Object> validateToken(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("username", authentication.getName());
        return response;
    }

    // 회원가입 (학생 등록) => 선생님 등록은 별도로 추가 필요
    @PostMapping("/register")
    public ResponseEntity<Void> insertStudent(@RequestBody UserInsertRequest request) {
        usersService.insertUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
