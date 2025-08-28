package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;
import java.util.UUID;


public record LectureResponse(
        UUID id,
        String title,
        String description,
        String instructorName,
        String durationMinutes,
        UUID categoryId,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
