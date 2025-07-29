package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record StudentLectureResponse(
        UUID teacherId,
        String teacherName,
        UUID lectureId,
        String lectureTitle,
        String thumbnailUrl,
        LocalDateTime expireDate
) {}


