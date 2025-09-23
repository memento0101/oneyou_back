package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "챕터 응답")
public record ChapterResponse(
        @Schema(description = "챕터 ID")
        UUID chapterId,

        @Schema(description = "강의 ID")
        UUID lectureId,

        @Schema(description = "챕터 제목")
        String title,

        @Schema(description = "챕터 순서")
        Integer chapterOrder,

        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
}