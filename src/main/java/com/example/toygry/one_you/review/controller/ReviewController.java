package com.example.toygry.one_you.review.controller;

import com.example.toygry.one_you.review.dto.ReviewResponse;
import com.example.toygry.one_you.review.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{lectureId}")
    public List<ReviewResponse> getLectureReviews(@PathVariable UUID lectureId) {
        return reviewService.getLectureReviews(lectureId);
    }

    // 게시글 작성, 삭제, 수정 필요
}
