package com.example.toygry.one_you.passReview.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import com.example.toygry.one_you.passReview.service.PassReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pass")
@RequiredArgsConstructor
public class PassReviewController {

    private final PassReviewService passReviewService;

    @Operation(summary = "전체 합격생 리뷰 조회", description = "전체 합격생 리뷰를 조회합니다.")
    @GetMapping
    public ApiResponse<List<PassReviewResponse>> getAllPassReviews() {
        return ApiResponse.success(passReviewService.getAllPassReviews());
    }

    @Operation(summary = "합격생 리뷰 작성", description = "합격생 리뷰 작성")
    @PostMapping
    public ApiResponse<String> createPassReview(
            @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @RequestBody PassReviewRequest passReviewRequest
    ) {
        passReviewService.createPassReview(userPrincipal, passReviewRequest);
        return ApiResponse.success("성공적으로 생성했습니다.");
    }
}
