package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "강의 질문 답변 응답")
public record LectureQuestionAnswerResponse(
    @Schema(description = "답변 ID")
    UUID id,
    
    @Schema(description = "질문 ID")
    UUID questionId,
    
    @Schema(description = "강사 ID")
    UUID teacherId,
    
    @Schema(description = "강사 이름")
    String teacherName,
    
    @Schema(description = "답변 내용")
    String content,
    
    @Schema(description = "답변 작성일")
    LocalDateTime createdAt,
    
    @Schema(description = "답변 수정일")
    LocalDateTime updatedAt
) {}