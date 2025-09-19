package com.example.toygry.one_you.passReview.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import com.example.toygry.one_you.passReview.service.PassReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pass")
@RequiredArgsConstructor
public class PassReviewController {

    private final PassReviewService passReviewService;

    @Operation(summary = "전체 합격생 후기 조회", description = "전체 합격생 후기를 조회합니다.")
    @GetMapping
    public ApiResponse<List<PassReviewResponse>> getAllPassReviews() {
        return ApiResponse.success(passReviewService.getAllPassReviews());
    }

    @Operation(summary = "대학 타입별 합격생 후기 조회", description = "대학 타입(NATIONAL/PRIVATE/MEDICAL)별로 합격생 후기를 조회합니다.")
    @GetMapping("/type/{universityType}")
    public ApiResponse<List<PassReviewResponse>> getPassReviewsByUniversityType(
            @Parameter(description = "대학 타입 (NATIONAL, PRIVATE, MEDICAL)", required = true)
            @PathVariable String universityType
    ) {
        return ApiResponse.success(passReviewService.getPassReviewsByUniversityType(universityType));
    }

    @Operation(summary = "특정 대학 합격생 후기 조회", description = "특정 대학의 합격생 후기를 조회합니다.")
    @GetMapping("/university/{universityId}")
    public ApiResponse<List<PassReviewResponse>> getPassReviewsByUniversityId(
            @Parameter(description = "대학 ID", required = true)
            @PathVariable UUID universityId
    ) {
        return ApiResponse.success(passReviewService.getPassReviewsByUniversityId(universityId));
    }

    @Operation(summary = "합격 연도별 후기 조회", description = "특정 연도에 합격한 학생들의 후기를 조회합니다.")
    @GetMapping("/year/{passYear}")
    public ApiResponse<List<PassReviewResponse>> getPassReviewsByPassYear(
            @Parameter(description = "합격 연도", required = true)
            @PathVariable Integer passYear
    ) {
        return ApiResponse.success(passReviewService.getPassReviewsByPassYear(passYear));
    }

    @Operation(
            summary = "합격생 후기 작성",
            description = "새로운 합격생 후기를 작성합니다. 4개의 질문에 대한 답변과 대학 정보, 합격 학과를 포함해야 합니다."
    )
    @PostMapping
    public ApiResponse<String> createPassReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "합격 후기 작성 요청")
            @RequestBody PassReviewRequest passReviewRequest
    ) {
        passReviewService.createPassReview(userPrincipal, passReviewRequest);
        return ApiResponse.success("합격 후기가 성공적으로 생성되었습니다.");
    }
}
