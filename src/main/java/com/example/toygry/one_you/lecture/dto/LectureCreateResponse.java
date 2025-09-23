package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "강의 생성 응답")
public record LectureCreateResponse(
        @Schema(description = "강의 ID")
        UUID lectureId,

        @Schema(description = "강의 제목")
        String title,

        @Schema(description = "강의 부제목")
        String subtitle,

        @Schema(description = "강의 카테고리")
        String category,

        @Schema(description = "강사명")
        String teacherName,

        @Schema(description = "강의 설명")
        String description,

        @Schema(description = "강의 가격")
        Integer price,

        @Schema(description = "강의 기간 (일)")
        Integer period,

        @Schema(description = "수강 대상")
        String target,

        @Schema(description = "강의 URL")
        String url,

        @Schema(description = "업로드된 이미지 목록")
        List<LectureImageInfo> images,

        @Schema(description = "강의 생성 시간")
        LocalDateTime createdAt
) {
    @Schema(description = "강의 이미지 정보")
    public record LectureImageInfo(
            @Schema(description = "이미지 ID")
            UUID imageId,

            @Schema(description = "원본 파일명")
            String originalFileName,

            @Schema(description = "파일 경로")
            String filePath,

            @Schema(description = "대표 이미지 여부")
            Boolean isPrimary,

            @Schema(description = "이미지 순서")
            Integer displayOrder,

            @Schema(description = "대체 텍스트")
            String altText,

            @Schema(description = "이미지 설명")
            String description
    ) {}
}