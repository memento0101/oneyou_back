package com.example.toygry.one_you.passReview.dto;

public record PassReviewUpdateRequest(
        int passYear,
        String targetUniversity,
        String title,
        String content
) {
}
