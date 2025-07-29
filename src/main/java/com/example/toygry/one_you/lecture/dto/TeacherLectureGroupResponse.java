package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TeacherLectureGroupResponse(
        UUID teacherId,
        String teacherName,
        List<LectureItem> lectures
) {
    public record LectureItem(
            UUID lectureId,
            String lectureTitle,
            String thumbnailUrl,
            LocalDateTime expireDate
    ) {}
}

