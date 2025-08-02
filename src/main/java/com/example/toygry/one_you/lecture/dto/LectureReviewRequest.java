package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "수강후기 작성 요청")
public record LectureReviewRequest(
        @NotNull(message = "강의 ID는 필수입니다")
        @Schema(description = "강의 ID", example = "33333333-3333-3333-3333-333333333333")
        String lectureId,

        @NotNull(message = "평점은 필수입니다")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다")
        @Schema(description = "평점 (1-5)", example = "5")
        Integer rating,

        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다")
        @Schema(description = "후기 제목", example = "정말 유익한 강의였습니다!")
        String title,

        @NotBlank(message = "내용은 필수입니다")
        @Size(max = 2000, message = "내용은 2000자 이하여야 합니다")
        @Schema(description = "후기 내용", example = "강의 내용이 체계적이고 이해하기 쉬웠습니다.")
        String content,

        @Schema(description = "익명 여부", example = "false")
        Boolean isAnonymous
) {
}