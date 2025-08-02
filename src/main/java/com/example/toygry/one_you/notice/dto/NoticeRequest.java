package com.example.toygry.one_you.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "공지사항 작성 요청")
public record NoticeRequest(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다")
        @Schema(description = "공지사항 제목", example = "중요한 공지사항입니다")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        @Schema(description = "공지사항 내용", example = "공지사항 내용입니다.")
        String content,

        @Schema(description = "중요 공지 여부", example = "false")
        Boolean isImportant
) {
}