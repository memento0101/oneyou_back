package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.service.LectureQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "강의 질문", description = "강의 질문 관련 API")
public class LectureQuestionController {

    private final LectureQuestionService questionService;

    @PostMapping("/lectures/{lectureId}/questions")
    @Operation(summary = "강의 질문 작성", description = "학생이 강의에 대한 질문을 작성합니다")
    public ResponseEntity<ApiResponse<UUID>> createQuestion(
            @Parameter(description = "강의 ID") @PathVariable UUID lectureId,
            @Valid @RequestBody LectureQuestionRequest request,
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        UUID questionId = questionService.createQuestion(lectureId, principal.getUuid(), request);
        return ResponseEntity.ok(ApiResponse.success(questionId));
    }

    @GetMapping("/students/me/questions")
    @Operation(summary = "내 질문 목록 조회", description = "학생이 자신이 작성한 질문 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<LectureQuestionListResponse>>> getMyQuestions(
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        List<LectureQuestionListResponse> questions = questionService.getQuestionsByStudent(principal.getUuid());
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @GetMapping("/students/me/questions/{questionId}")
    @Operation(summary = "내 질문 상세 조회", description = "학생이 자신이 작성한 질문의 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<LectureQuestionResponse>> getMyQuestionDetail(
            @Parameter(description = "질문 ID") @PathVariable UUID questionId,
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        LectureQuestionResponse question = questionService.getQuestionDetail(questionId, principal.getUuid());
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    @GetMapping("/teachers/me/questions")
    @Operation(summary = "담당 강의 질문 목록 조회", description = "강사가 자신이 담당하는 강의의 질문 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<LectureQuestionListResponse>>> getTeacherQuestions(
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        List<LectureQuestionListResponse> questions = questionService.getQuestionsByTeacher(principal.getUuid());
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @GetMapping("/teachers/me/questions/{questionId}")
    @Operation(summary = "질문 상세 조회", description = "강사가 담당 강의의 질문 상세 정보를 조회합니다")
    public ResponseEntity<ApiResponse<LectureQuestionResponse>> getTeacherQuestionDetail(
            @Parameter(description = "질문 ID") @PathVariable UUID questionId,
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        LectureQuestionResponse question = questionService.getQuestionDetail(questionId, principal.getUuid());
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    @PostMapping("/teachers/me/questions/{questionId}/answer")
    @Operation(summary = "질문에 답변 작성", description = "강사가 학생의 질문에 답변을 작성합니다")
    public ResponseEntity<ApiResponse<Void>> createAnswer(
            @Parameter(description = "질문 ID") @PathVariable UUID questionId,
            @Valid @RequestBody LectureQuestionAnswerRequest request,
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        questionService.createOrUpdateAnswer(questionId, principal.getUuid(), request);
        return ResponseEntity.ok(ApiResponse.success("답변 생성 완료"));
    }

    @PutMapping("/teachers/me/questions/{questionId}/answer")
    @Operation(summary = "질문 답변 수정", description = "강사가 작성한 답변을 수정합니다")
    public ResponseEntity<ApiResponse<Void>> updateAnswer(
            @Parameter(description = "질문 ID") @PathVariable UUID questionId,
            @Valid @RequestBody LectureQuestionAnswerRequest request,
            @AuthenticationPrincipal UserTokenPrincipal principal
    ) {
        questionService.createOrUpdateAnswer(questionId, principal.getUuid(), request);
        return ResponseEntity.ok(ApiResponse.success("답변 수정 완료"));
    }
}