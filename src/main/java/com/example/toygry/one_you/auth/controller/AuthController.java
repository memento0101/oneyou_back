package com.example.toygry.one_you.auth.controller;

import com.example.toygry.one_you.auth.dto.LoginRequest;
import com.example.toygry.one_you.auth.dto.TokenResponse;
import com.example.toygry.one_you.auth.service.JwtTokenService;
import com.example.toygry.one_you.auth.service.TokenRedisService;
import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.users.dto.TeacherInsertRequest;
import com.example.toygry.one_you.users.dto.UserInsertRequest;
import com.example.toygry.one_you.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TokenRedisService tokenRedisService;
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

    /**
     * 로그아웃 - Redis에서 토큰 삭제
     * 
     * @param authentication 인증된 사용자 정보
     * @return 로그아웃 성공 메시지
     */
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리 (Redis에서 토큰 즉시 무효화)")
    @PostMapping("/logout")
    public ApiResponse<String> logout(Authentication authentication) {
        UserTokenPrincipal principal = (UserTokenPrincipal) authentication.getPrincipal();
        
        // Redis에서 토큰 삭제
        tokenRedisService.deleteToken(principal.getUuid());
        
        return ApiResponse.success("로그아웃되었습니다.");
    }

    /**
     * 토큰 갱신 - 새로운 JWT 토큰 발급
     * 
     * @param authentication 현재 인증된 사용자 정보
     * @return 새로운 JWT 토큰
     */
    @Operation(summary = "토큰 갱신", description = "기존 토큰으로 새로운 토큰 발급 (TTL 연장)")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(Authentication authentication) {
        UserTokenPrincipal principal = (UserTokenPrincipal) authentication.getPrincipal();
        
        // 새로운 토큰 생성 (사용자 정보로 재로그인 없이)
        LoginRequest loginRequest = new LoginRequest(principal.getUsername(), null);
        TokenResponse newToken = jwtTokenService.generateTokenForRefresh(principal);
        
        return ApiResponse.success(newToken);
    }

    // 회원가입 (학생 등록) => 선생님 등록은 별도로 추가 필요
    @PostMapping("/register")
    public ApiResponse<String> insertStudent(@RequestBody UserInsertRequest request) {
        usersService.insertStudent(request);
        return ApiResponse.success("회원가입이 완료되었습니다.");
    }

    @PostMapping("/register/teacher")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "선생님 계정 생성", description = "ADMIN 또는 TEACHER 권한이 있는 사용자만 선생님 계정을 생성할 수 있습니다.")
    public ApiResponse<String> insertTeacher(@RequestBody TeacherInsertRequest request) {
        usersService.insertTeacher(request);
        return ApiResponse.success("선생님 계정 생성이 완료되었습니다");
    }
}
