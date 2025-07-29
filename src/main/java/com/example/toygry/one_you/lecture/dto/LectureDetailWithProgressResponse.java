package com.example.toygry.one_you.lecture.dto;

import java.util.UUID;

public record LectureDetailWithProgressResponse(
        UUID chapterId,
        String chapterTitle,
        UUID detailId,
        String detailTitle,
        String type,
        Boolean isCompleted
) {}