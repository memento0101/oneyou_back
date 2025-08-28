package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "강의 질문 작성 요청")
public record LectureQuestionRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다")
    @Schema(description = "질문 제목", example = "1강 가속도 운동 관련 질문")
    String title,
    
    @NotBlank(message = "내용은 필수입니다")
    @Schema(description = "질문 내용", example = "가속도 공식에서 시간이 제곱으로 들어가는 이유가 무엇인가요?")
    String content
) {}