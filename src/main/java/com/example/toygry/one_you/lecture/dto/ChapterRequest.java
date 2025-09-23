package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챕터 생성/수정 요청")
public record ChapterRequest(
        @Schema(description = "챕터 제목", example = "1장. 기초 개념")
        String title,

        @Schema(description = "챕터 순서", example = "1")
        Integer chapterOrder
) {
}