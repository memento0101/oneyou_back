package com.example.toygry.one_you.lecture.dto;

import java.util.UUID;

public record LectureProgressRequest(
        UUID lectureDetailId,
        boolean isCompleted
) {
}