package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserLectureResponse(
        UUID lectureId,
        String title,
        String instructorName,
        int progress,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isActive,
        String categoryName
) {
}
