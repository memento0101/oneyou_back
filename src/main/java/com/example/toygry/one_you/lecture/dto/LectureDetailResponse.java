package com.example.toygry.one_you.lecture.dto;

import java.util.List;
import java.util.UUID;

public record LectureDetailResponse(
        UUID lectureId,
        String lectureTitle,
        int expireDaysLeft,
        double progressRate,
        List<Chapter> chapters
) {
    public record Chapter(UUID chapterId, String chapterTitle, List<Detail> details) {}
    public record Detail(UUID detailId, String detailTitle, String type, boolean isCompleted) {}
}