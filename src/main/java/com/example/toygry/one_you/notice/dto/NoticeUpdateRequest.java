package com.example.toygry.one_you.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "공지사항 수정 요청")
public record NoticeUpdateRequest(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다")
        @Schema(description = "공지사항 제목", example = "수정된 공지사항 제목")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        @Schema(description = "공지사항 내용", example = "수정된 공지사항 내용입니다.")
        String content,

        @Schema(description = "중요 공지 여부", example = "true")
        Boolean isImportant
) {
}