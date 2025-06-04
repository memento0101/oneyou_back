package com.example.toygry.one_you.review.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID lectureId,
        String lectureTitle,
        String userName,
        String contents,
        int score,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
