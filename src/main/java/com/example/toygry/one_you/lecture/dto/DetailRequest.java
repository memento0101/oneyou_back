package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강의 상세 생성/수정 요청")
public record DetailRequest(
        @Schema(description = "상세 제목", example = "1-1. 물리학의 기본 원리")
        String title,

        @Schema(description = "콘텐츠 타입", example = "VIDEO", allowableValues = {"VIDEO", "QUIZ", "ASSIGNMENT", "TEXT"})
        String type,

        @Schema(description = "상세 순서", example = "1")
        Integer detailOrder
) {
}