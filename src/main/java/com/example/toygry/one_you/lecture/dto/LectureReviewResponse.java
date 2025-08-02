package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "수강후기 응답")
public record LectureReviewResponse(
        @Schema(description = "후기 ID", example = "11111111-1111-1111-1111-111111111111")
        String reviewId,

        @Schema(description = "강의 ID", example = "33333333-3333-3333-3333-333333333333")
        String lectureId,

        @Schema(description = "작성자 ID", example = "5d726309-0785-47e2-8f81-257d74401543")
        String userId,

        @Schema(description = "작성자명 (익명일 경우 '익명')", example = "김철수")
        String userName,

        @Schema(description = "평점 (1-5)", example = "5")
        Integer rating,

        @Schema(description = "후기 제목", example = "정말 유익한 강의였습니다!")
        String title,

        @Schema(description = "후기 내용", example = "강의 내용이 체계적이고 이해하기 쉬웠습니다.")
        String content,

        @Schema(description = "익명 여부", example = "false")
        Boolean isAnonymous,

        @Schema(description = "작성일시", example = "2025-07-29T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일시", example = "2025-07-29T10:30:00")
        LocalDateTime updatedAt
) {
}