package com.example.toygry.one_you.lecture.dto;

import java.util.UUID;

public record LectureDetailRequest(
        UUID lectureDetailId,
        String type
) {
}
