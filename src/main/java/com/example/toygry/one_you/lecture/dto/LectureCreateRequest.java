package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "강의 생성 요청")
public record LectureCreateRequest(
        @Schema(description = "강의 제목", example = "EJU 물리학 기초")
        String title,

        @Schema(description = "강의 부제목", example = "기초부터 심화까지 완전정복")
        String subtitle,

        @Schema(description = "강의 카테고리", example = "물리")
        String category,

        @Schema(description = "강사 ID", example = "22222222-2222-2222-2222-222222222222")
        UUID teacherId,

        @Schema(description = "강의 설명", example = "EJU 물리학 시험 준비를 위한 기초 강의입니다.")
        String description,

        @Schema(description = "강의 가격", example = "120000")
        Integer price,

        @Schema(description = "강의 기간 (일)", example = "90")
        Integer period,

        @Schema(description = "수강 대상", example = "EJU 물리학 시험 준비생")
        String target,

        @Schema(description = "강의 URL")
        String url
) {
}