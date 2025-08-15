package com.example.toygry.one_you.users.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.users.dto.UserResponse;
import com.example.toygry.one_you.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @Operation(summary = "전체 유저 조회", description = "전체 사용자를 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success(usersService.getAllUsers());
    }

    // 수강생 목표 3 지망 까지 출력
    @Operation(summary = "유저 목표 대학 출력", description = "수강생의 목표 대학 최대 3개를 출력합니다.")
    @GetMapping("/goal")
    public ApiResponse<List<UserResponse.GoalUniversity>> getUserGoal(
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal
    ) {
        return ApiResponse.success(usersService.getUserGoal(userTokenPrincipal.getUuid()));
    }


    // 유저 정보 한개 가져오기
    @Operation(summary = "유저 정보 출력", description = "로그인한 사용자의 정보 출력합니다")
    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo(@AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal) {
        return ApiResponse.success(usersService.getUserById(userTokenPrincipal.getUuid()));
    }
}
