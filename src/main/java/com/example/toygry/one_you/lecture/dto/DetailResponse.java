package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "강의 상세 응답")
public record DetailResponse(
        @Schema(description = "상세 ID")
        UUID detailId,

        @Schema(description = "챕터 ID")
        UUID chapterId,

        @Schema(description = "상세 제목")
        String title,

        @Schema(description = "콘텐츠 타입")
        String type,

        @Schema(description = "상세 순서")
        Integer detailOrder,

        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
}