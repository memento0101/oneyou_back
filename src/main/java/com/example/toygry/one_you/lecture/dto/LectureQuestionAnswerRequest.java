package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "강의 질문 답변 요청")
public record LectureQuestionAnswerRequest(
    @NotBlank(message = "답변 내용은 필수입니다")
    @Schema(description = "답변 내용", example = "가속도 공식 v = v0 + at에서 거리를 구하려면 s = v0*t + (1/2)*a*t²이 됩니다. 시간의 제곱이 들어가는 이유는...")
    String content
) {}