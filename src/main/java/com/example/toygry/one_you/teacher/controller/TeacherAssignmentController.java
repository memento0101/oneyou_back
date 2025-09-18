package com.example.toygry.one_you.teacher.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.teacher.dto.PendingFeedbackResponse;
import com.example.toygry.one_you.teacher.dto.SubmitFeedbackRequest;
import com.example.toygry.one_you.teacher.service.TeacherAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 선생님용 과제 관리 컨트롤러
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherAssignmentController {

    private final TeacherAssignmentService teacherAssignmentService;

    /**
     * 피드백 대기 중인 과제 목록 조회 (강의별 그룹핑)
     * 
     * @param authentication 인증된 선생님 정보
     * @return 강의별로 그룹핑된 피드백 대기 과제 목록
     */
    @Operation(summary = "피드백 대기 과제 목록", description = "선생님의 강의별 피드백이 필요한 과제 목록을 조회합니다.")
    @GetMapping("/assignments/pending-feedback")
    public ApiResponse<PendingFeedbackResponse> getPendingFeedbackAssignments(Authentication authentication) {
        UserTokenPrincipal principal = (UserTokenPrincipal) authentication.getPrincipal();
        
        PendingFeedbackResponse response = teacherAssignmentService.getPendingFeedbackByTeacher(principal.getUuid());
        
        return ApiResponse.success(response);
    }

    /**
     * 학생 과제에 피드백 제출
     * 
     * @param submissionId 제출물 ID
     * @param request 피드백 내용 및 완료 여부
     * @param authentication 인증된 선생님 정보
     * @return 피드백 제출 결과
     */
    @Operation(summary = "피드백 제출", description = "학생의 과제 제출물에 피드백을 제공하고 완료 상태를 업데이트합니다.")
    @PostMapping("/submissions/{submissionId}/feedback")
    public ApiResponse<String> submitFeedback(
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmitFeedbackRequest request,
            Authentication authentication) {
        
        UserTokenPrincipal principal = (UserTokenPrincipal) authentication.getPrincipal();
        
        teacherAssignmentService.submitFeedback(submissionId, request, principal.getUuid());
        
        return ApiResponse.success("피드백이 성공적으로 제출되었습니다.");
    }
}