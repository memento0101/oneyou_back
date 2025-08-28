package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "강의 질문 응답")
public record LectureQuestionResponse(
    @Schema(description = "질문 ID")
    UUID id,
    
    @Schema(description = "강의 ID")
    UUID lectureId,
    
    @Schema(description = "강의 제목")
    String lectureTitle,
    
    @Schema(description = "학생 ID")
    UUID studentId,
    
    @Schema(description = "학생 이름")
    String studentName,
    
    @Schema(description = "질문 제목")
    String title,
    
    @Schema(description = "질문 내용")
    String content,
    
    @Schema(description = "답변 완료 여부")
    boolean isAnswered,
    
    @Schema(description = "질문 작성일")
    LocalDateTime createdAt,
    
    @Schema(description = "질문 수정일")
    LocalDateTime updatedAt,
    
    @Schema(description = "답변 정보")
    LectureQuestionAnswerResponse answer
) {}