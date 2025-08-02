package com.example.toygry.one_you.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "공지사항 응답")
public record NoticeResponse(
        @Schema(description = "공지사항 ID", example = "11111111-1111-1111-1111-111111111111")
        String id,

        @Schema(description = "공지사항 제목", example = "중요한 공지사항입니다")
        String title,

        @Schema(description = "공지사항 내용", example = "공지사항 내용입니다.")
        String content,

        @Schema(description = "중요 공지 여부", example = "false")
        Boolean isImportant,

        @Schema(description = "조회수", example = "10")
        Integer viewCount,

        @Schema(description = "작성자 ID", example = "22222222-2222-2222-2222-222222222222")
        String authorId,

        @Schema(description = "작성자명", example = "관리자")
        String authorName,

        @Schema(description = "작성일시", example = "2025-07-29T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "수정일시", example = "2025-07-29T10:30:00")
        LocalDateTime updatedAt
) {
}