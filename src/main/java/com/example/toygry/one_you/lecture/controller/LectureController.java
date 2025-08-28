package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.service.LectureService;
import com.example.toygry.one_you.lecture.service.StudentLectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
@Tag(name = "강의 관리", description = "강의 목록, 상세 정보, 콘텐츠 조회 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class LectureController {

    private final StudentLectureService studentLectureService;
    private final LectureService lectureService;

    @Operation(summary = "전체 강의 목록 조회", description = "사용자가 수강 가능한 전체 강의 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list")
    public ApiResponse<List<TeacherLectureGroupResponse>> getLectureList(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal
    ) {
        return ApiResponse.success(studentLectureService.getActiveLecturesByUser(userPrincipal.getUuid()));
    }

    @Operation(summary = "강의 상세 정보 조회", description = "강의 목차, 진행도, 남은 수강 일수를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수강 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "강의를 찾을 수 없음")
    })
    @GetMapping("/{lectureId}")
    public ApiResponse<LectureDetailResponse> getLectureDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @Parameter(description = "강의 ID", required = true) @PathVariable UUID lectureId
    ) {
        return ApiResponse.success(lectureService.getLectureDetail(userTokenPrincipal.getUuid(), lectureId));
    }

    @Operation(summary = "강의 콘텐츠 상세 조회", description = "강의 목차 클릭 시 해당 강의의 상세 콘텐츠를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "강의 콘텐츠를 찾을 수 없음")
    })
    @PostMapping("/detail")
    public ApiResponse<LectureContentsResponse> getLectureContentsDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "강의 상세 요청") @RequestBody LectureDetailRequest lectureDetailRequest
    ) {
        return ApiResponse.success(lectureService.getLectureContentsDetail(userTokenPrincipal.getUuid(), lectureDetailRequest));
    }

    @Operation(summary = "학생 과제 링크 제출", description = "학생이 과제 링크를 제출합니다.", tags = {"과제 관리"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "강의를 찾을 수 없음")
    })
    @PostMapping("/submit-link")
    public ApiResponse<String> submitStudentLink(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "과제 링크 제출 요청") @RequestBody StudentLinkSubmissionRequest request
    ) {
        lectureService.submitStudentLink(userTokenPrincipal.getUuid(), request);
        return ApiResponse.success("링크가 성공적으로 제출되었습니다.");
    }

    @Operation(summary = "퀴즈 채점", description = "학생이 제출한 퀴즈 답안을 채점하고 결과를 반환합니다.", tags = {"퀴즈 관리"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 답안 정보"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "퀴즈를 찾을 수 없음")
    })
    @PostMapping("/grade-quiz")
    public ApiResponse<QuizGradingResponse> gradeQuiz(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "퀴즈 제출 요청") @RequestBody QuizSubmissionRequest request
    ) {
        return ApiResponse.success(lectureService.gradeQuiz(userTokenPrincipal.getUuid(), request));
    }

    @Operation(summary = "강의 진도 업데이트", description = "학생의 강의 완료 상태를 업데이트합니다.", tags = {"진도 관리"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "강의를 찾을 수 없음")
    })
    @PostMapping("/progress")
    public ApiResponse<String> updateLectureProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "진도 업데이트 요청") @RequestBody LectureProgressRequest request
    ) {
        return ApiResponse.success(lectureService.updateLectureProgress(userTokenPrincipal.getUuid(), request));
    }
}
