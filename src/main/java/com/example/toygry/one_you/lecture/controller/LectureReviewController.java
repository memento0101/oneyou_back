package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.dto.LectureReviewListResponse;
import com.example.toygry.one_you.lecture.dto.LectureReviewRequest;
import com.example.toygry.one_you.lecture.dto.LectureReviewResponse;
import com.example.toygry.one_you.lecture.dto.LectureReviewUpdateRequest;
import com.example.toygry.one_you.lecture.service.LectureReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
@Tag(name = "수강후기 관리", description = "수강후기 작성, 조회, 수정, 삭제 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class LectureReviewController {

    private final LectureReviewService lectureReviewService;

    @Operation(summary = "수강후기 작성", description = "강의에 대한 수강후기를 작성합니다. 강의를 50% 이상 수강한 경우에만 작성 가능하며, 한 강의당 하나의 후기만 작성할 수 있습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "후기 작성 권한 없음 (50% 미만 수강)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 해당 강의에 후기 작성됨")
    })
    @PostMapping("/reviews")
    public ApiResponse<LectureReviewResponse> createReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Valid @RequestBody LectureReviewRequest request
    ) {
        LectureReviewResponse response = lectureReviewService.createReview(userPrincipal.getUuid(), request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "내가 작성한 후기 조회", description = "특정 강의에 대해 내가 작성한 후기를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "후기를 찾을 수 없음")
    })
    @GetMapping("/{lectureId}/reviews/my")
    public ApiResponse<LectureReviewResponse> getMyReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Parameter(description = "강의 ID") @PathVariable String lectureId
    ) {
        LectureReviewResponse response = lectureReviewService.getMyReview(userPrincipal.getUuid(), lectureId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "강의 수강후기 목록 조회", description = "특정 강의의 모든 수강후기를 페이징하여 조회합니다. 평점 통계와 평균 평점도 함께 제공됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/{lectureId}/reviews")
    public ApiResponse<LectureReviewListResponse> getReviewsByLecture(
            @Parameter(description = "강의 ID") @PathVariable String lectureId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        LectureReviewListResponse response = lectureReviewService.getReviewsByLecture(lectureId, page, size);
        return ApiResponse.success(response);
    }

    @Operation(summary = "수강후기 수정", description = "내가 작성한 수강후기를 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "후기를 찾을 수 없음")
    })
    @PutMapping("/reviews/{reviewId}")
    public ApiResponse<LectureReviewResponse> updateReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Parameter(description = "후기 ID") @PathVariable String reviewId,
            @Valid @RequestBody LectureReviewUpdateRequest request
    ) {
        LectureReviewResponse response = lectureReviewService.updateReview(userPrincipal.getUuid(), reviewId, request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "수강후기 삭제", description = "내가 작성한 수강후기를 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "후기 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "후기를 찾을 수 없음")
    })
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Parameter(description = "후기 ID") @PathVariable String reviewId
    ) {
        lectureReviewService.deleteReview(userPrincipal.getUuid(), reviewId);
        return ApiResponse.success(null);
    }
}