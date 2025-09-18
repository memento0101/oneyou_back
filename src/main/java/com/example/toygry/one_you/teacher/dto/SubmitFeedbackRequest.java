package com.example.toygry.one_you.teacher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 선생님 피드백 제출 요청 DTO
 */
public record SubmitFeedbackRequest(
        @NotBlank(message = "피드백 내용은 필수입니다")
        String feedback,
        
        @NotNull(message = "완료 여부는 필수입니다")
        Boolean isCompleted
) {
}