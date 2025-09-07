package com.example.toygry.one_you.auth.controller;

import com.example.toygry.one_you.auth.dto.LoginRequest;
import com.example.toygry.one_you.auth.dto.TokenResponse;
import com.example.toygry.one_you.auth.service.JwtTokenService;
import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.users.dto.TeacherInsertRequest;
import com.example.toygry.one_you.users.dto.UserInsertRequest;
import com.example.toygry.one_you.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling authentication requests
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final UsersService usersService;

    /**
     * Authenticate user and generate JWT token
     *
     * @param loginRequest Login credentials
     * @return JWT token
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        // Return token response
        return ApiResponse.success(jwtTokenService.generateToken(loginRequest));
    }

    /**
     * Check if the current token is valid
     * This endpoint is protected, so it will return 401 if the token is invalid
     *
     * @param authentication The authenticated user
     * @return Token validity status
     */
    @GetMapping("/token/validate")
    public ApiResponse<Map<String, Object>> validateToken(Authentication authentication) {
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("username", authentication.getName());
        return ApiResponse.success(data);
    }

    // 회원가입 (학생 등록) => 선생님 등록은 별도로 추가 필요
    @PostMapping("/register")
    public ApiResponse<String> insertStudent(@RequestBody UserInsertRequest request) {
        usersService.insertStudent(request);
        return ApiResponse.success("회원가입이 완료되었습니다.");
    }

    @PostMapping("/register/teacher")
    public ApiResponse<String> insertTeacher(@RequestBody TeacherInsertRequest request) {
        usersService.insertTeacher(request);
        return ApiResponse.success("선생님 계정 생성이 완료되었습니다");
    }
}
