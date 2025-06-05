package com.example.toygry.one_you.review.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.review.dto.ReviewRequest;
import com.example.toygry.one_you.review.dto.ReviewResponse;
import com.example.toygry.one_you.review.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //
    @GetMapping
    public ApiResponse<List<ReviewResponse>> getAllReviews() {
        return ApiResponse.success(reviewService.getAllReviews());
    }

    // 강의 별 수강평 목록 조회
    @GetMapping("/{lectureId}")
    public ApiResponse<List<ReviewResponse>> getLectureReviews(@PathVariable UUID lectureId) {
        return ApiResponse.success(reviewService.getLectureReviews(lectureId));
    }

    // 자기가 작성한 수강평 목록 조회
//    @GetMapping("users")

    // 수강평 작성
    @PostMapping
    public ApiResponse<String> saveReview(
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @RequestBody ReviewRequest request) {

        reviewService.saveReview(userTokenPrincipal,request);
        return ApiResponse.success("성공적으로 등록되었습니다");
    }

    // 수강평 수정
    @PutMapping("/{reviewId}")
    public ApiResponse<String> updateReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @RequestBody ReviewRequest request) {
        reviewService.updateReview(reviewId, userTokenPrincipal, request);
        return ApiResponse.success("수정이 완료 되었습니다.");
    }

    // 수강평 삭제
    @DeleteMapping("/{reviewId}")
    public ApiResponse<String> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal) {
        reviewService.deleteReview(reviewId, userTokenPrincipal);
        return ApiResponse.success("삭제가 완료 되었습니다.");
    }


    //    @GetMapping("/user")
//    public ResponseEntity<List<UserLectureResponse>> getUserLectures(@AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal) {
//        return ResponseEntity.ok(lectureService.getUserLectures(userTokenPrincipal.getUuid()));
//    }
}
