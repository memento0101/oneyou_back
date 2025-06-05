package com.example.toygry.one_you.review.service;

import com.example.toygry.one_you.review.dto.ReviewResponse;
import com.example.toygry.one_you.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllReviews();
    }

    public List<ReviewResponse> getLectureReviews(UUID lectureId) {
        return reviewRepository.findByLectureId(lectureId);
    }
}
