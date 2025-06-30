package com.example.toygry.one_you.passReview.dto;

public record PassReviewRequest(
        String title,
        String contents,
        String targetUniversity,
        Integer passYear
) {}

