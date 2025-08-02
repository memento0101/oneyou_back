package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "수강후기 수정 요청")
public record LectureReviewUpdateRequest(
        @NotNull(message = "평점은 필수입니다")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다")
        @Schema(description = "평점 (1-5)", example = "4")
        Integer rating,

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다")
        @Schema(description = "후기 제목", example = "수정된 후기 제목")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        @Size(max = 2000, message = "내용은 2000자 이하여야 합니다")
        @Schema(description = "후기 내용", example = "수정된 후기 내용입니다.")
        String content,

        @Schema(description = "익명 여부", example = "true")
        Boolean isAnonymous
) {
}