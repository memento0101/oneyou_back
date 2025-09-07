package com.example.toygry.one_you.lecture.dto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LectureUserResponse(
        LectureResponse lecture,
        List<UserLectureInfo> users
) {
    public record UserLectureInfo(
            UUID userId,
            String username,
            String email,
            int progress,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean isActive
    ) {}

}
