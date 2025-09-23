package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.annotation.CommonApiResponses;
import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.service.LectureService;
import com.example.toygry.one_you.lecture.service.StudentLectureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    @CommonApiResponses
    @GetMapping("/list")
    public ApiResponse<List<TeacherLectureGroupResponse>> getLectureList(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal
    ) {
        return ApiResponse.success(studentLectureService.getActiveLecturesByUser(userPrincipal.getUuid()));
    }

    @Operation(summary = "강의 상세 정보 조회", description = "강의 목차, 진행도, 남은 수강 일수를 조회합니다.")
    @CommonApiResponses
    @GetMapping("/{lectureId}")
    public ApiResponse<LectureDetailResponse> getLectureDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @Parameter(description = "강의 ID", required = true) @PathVariable UUID lectureId
    ) {
        return ApiResponse.success(lectureService.getLectureDetail(userTokenPrincipal.getUuid(), lectureId));
    }

    @Operation(summary = "강의 콘텐츠 상세 조회", description = "강의 목차 클릭 시 해당 강의의 상세 콘텐츠를 조회합니다.")
    @CommonApiResponses
    @PostMapping("/detail")
    public ApiResponse<LectureContentsResponse> getLectureContentsDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "강의 상세 요청") @RequestBody LectureDetailRequest lectureDetailRequest
    ) {
        return ApiResponse.success(lectureService.getLectureContentsDetail(userTokenPrincipal.getUuid(), lectureDetailRequest));
    }

    @Operation(summary = "학생 과제 링크 제출", description = "학생이 과제 링크를 제출합니다.", tags = {"과제 관리"})
    @CommonApiResponses
    @PostMapping("/submit-link")
    public ApiResponse<String> submitStudentLink(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "과제 링크 제출 요청") @RequestBody StudentLinkSubmissionRequest request
    ) {
        lectureService.submitStudentLink(userTokenPrincipal.getUuid(), request);
        return ApiResponse.success("링크가 성공적으로 제출되었습니다.");
    }

    @Operation(summary = "퀴즈 채점", description = "학생이 제출한 퀴즈 답안을 채점하고 결과를 반환합니다.", tags = {"퀴즈 관리"})
    @CommonApiResponses
    @PostMapping("/grade-quiz")
    public ApiResponse<QuizGradingResponse> gradeQuiz(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "퀴즈 제출 요청") @RequestBody QuizSubmissionRequest request
    ) {
        return ApiResponse.success(lectureService.gradeQuiz(userTokenPrincipal.getUuid(), request));
    }

    @Operation(summary = "강의 진도 업데이트", description = "학생의 강의 완료 상태를 업데이트합니다.", tags = {"진도 관리"})
    @CommonApiResponses
    @PostMapping("/progress")
    public ApiResponse<String> updateLectureProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "진도 업데이트 요청") @RequestBody LectureProgressRequest request
    ) {
        return ApiResponse.success(lectureService.updateLectureProgress(userTokenPrincipal.getUuid(), request));
    }

    @Operation(summary = "과제 제출 확인", description = "학생이 해당 강의에 대해 과제를 제출했는지 확인합니다.", tags = {"과제 관리"})
    @CommonApiResponses
    @GetMapping("/submission")
    public ApiResponse<SubmissionCheckResponse> checkSubmission(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @Parameter(description = "강의 상세 ID", required = true) @RequestParam UUID lectureDetailId
    ) {
        return ApiResponse.success(studentLectureService.checkSubmission(userTokenPrincipal.getUuid(), lectureDetailId));
    }

    @Operation(summary = "강의 생성", description = "새로운 강의를 생성합니다. 여러 이미지 파일 업로드가 가능하며, 첫 번째 이미지가 대표 이미지로 설정됩니다.", tags = {"강의 관리"})
    @CommonApiResponses
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<LectureCreateResponse> createLecture(
            @ModelAttribute LectureCreateFormRequest formRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal
    ) {
        UUID createdBy = userTokenPrincipal.getUuid();

        // LectureCreateRequest로 변환
        LectureCreateRequest request = formRequest.toLectureCreateRequest();

        LectureCreateResponse response = lectureService.createLecture(request, formRequest.images(), createdBy);
        return ApiResponse.success(response);
    }

    // =========================== Chapter CRUD API ===========================

    @Operation(summary = "챕터 생성", description = "강의에 새로운 챕터를 추가합니다.", tags = {"챕터 관리"})
    @CommonApiResponses
    @PostMapping("/{lectureId}/chapters")
    public ApiResponse<ChapterResponse> createChapter(
            @Parameter(description = "강의 ID", required = true) @PathVariable UUID lectureId,
            @RequestBody ChapterRequest request
    ) {
        return ApiResponse.success(lectureService.createChapter(lectureId, request));
    }

    @Operation(summary = "챕터 수정", description = "기존 챕터 정보를 수정합니다.", tags = {"챕터 관리"})
    @CommonApiResponses
    @PutMapping("/chapters/{chapterId}")
    public ApiResponse<ChapterResponse> updateChapter(
            @Parameter(description = "챕터 ID", required = true) @PathVariable UUID chapterId,
            @RequestBody ChapterRequest request
    ) {
        return ApiResponse.success(lectureService.updateChapter(chapterId, request));
    }

    @Operation(summary = "챕터 삭제", description = "기존 챕터를 삭제합니다.", tags = {"챕터 관리"})
    @CommonApiResponses
    @DeleteMapping("/chapters/{chapterId}")
    public ApiResponse<String> deleteChapter(
            @Parameter(description = "챕터 ID", required = true) @PathVariable UUID chapterId
    ) {
        lectureService.deleteChapter(chapterId);
        return ApiResponse.success("챕터가 성공적으로 삭제되었습니다.");
    }

    // =========================== Detail CRUD API ===========================

    @Operation(summary = "강의 상세 생성", description = "챕터에 새로운 강의 상세를 추가합니다.", tags = {"강의 상세 관리"})
    @CommonApiResponses
    @PostMapping("/chapters/{chapterId}/details")
    public ApiResponse<DetailResponse> createDetail(
            @Parameter(description = "챕터 ID", required = true) @PathVariable UUID chapterId,
            @RequestBody DetailRequest request
    ) {
        return ApiResponse.success(lectureService.createDetail(chapterId, request));
    }

    @Operation(summary = "강의 상세 수정", description = "기존 강의 상세 정보를 수정합니다.", tags = {"강의 상세 관리"})
    @CommonApiResponses
    @PutMapping("/details/{detailId}")
    public ApiResponse<DetailResponse> updateDetail(
            @Parameter(description = "강의 상세 ID", required = true) @PathVariable UUID detailId,
            @RequestBody DetailRequest request
    ) {
        return ApiResponse.success(lectureService.updateDetail(detailId, request));
    }

    @Operation(summary = "강의 상세 삭제", description = "기존 강의 상세를 삭제합니다.", tags = {"강의 상세 관리"})
    @CommonApiResponses
    @DeleteMapping("/details/{detailId}")
    public ApiResponse<String> deleteDetail(
            @Parameter(description = "강의 상세 ID", required = true) @PathVariable UUID detailId
    ) {
        lectureService.deleteDetail(detailId);
        return ApiResponse.success("강의 상세가 성공적으로 삭제되었습니다.");
    }

    // =========================== 순서 변경 API ===========================

    @Operation(summary = "목차 순서 변경", description = "드래그 앤 드롭으로 변경된 챕터와 강의 상세의 순서를 일괄 업데이트합니다.", tags = {"목차 관리"})
    @CommonApiResponses
    @PutMapping("/{lectureId}/reorder")
    public ApiResponse<ReorderResponse> reorderLectureContents(
            @Parameter(description = "강의 ID", required = true) @PathVariable UUID lectureId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "순서 변경 요청") @RequestBody ReorderRequest request
    ) {
        return ApiResponse.success(lectureService.reorderLectureContents(lectureId, request));
    }
}
