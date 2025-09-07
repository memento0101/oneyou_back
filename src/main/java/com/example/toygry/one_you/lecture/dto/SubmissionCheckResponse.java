package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;

public record SubmissionCheckResponse(
        boolean hasSubmission,
        String reviewUrl,
        LocalDateTime submittedAt
) {
}