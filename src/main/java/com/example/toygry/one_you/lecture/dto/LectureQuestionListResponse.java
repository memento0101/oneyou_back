package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "강의 질문 목록 응답")
public record LectureQuestionListResponse(
    @Schema(description = "질문 ID")
    UUID id,
    
    @Schema(description = "강의 ID")
    UUID lectureId,
    
    @Schema(description = "강의 제목")
    String lectureTitle,
    
    @Schema(description = "학생 이름")
    String studentName,
    
    @Schema(description = "질문 제목")
    String title,
    
    @Schema(description = "답변 완료 여부")
    boolean isAnswered,
    
    @Schema(description = "질문 작성일")
    LocalDateTime createdAt
) {}