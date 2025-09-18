package com.example.toygry.one_you.teacher.service;

import com.example.toygry.one_you.teacher.dto.PendingFeedbackResponse;
import com.example.toygry.one_you.teacher.dto.SubmitFeedbackRequest;
import com.example.toygry.one_you.teacher.repository.TeacherAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 선생님용 과제 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository teacherAssignmentRepository;

    /**
     * 특정 선생님의 피드백 대기 과제를 강의별로 그룹핑하여 조회
     * 
     * @param teacherId 선생님 ID
     * @return 강의별로 그룹핑된 피드백 대기 과제 목록
     */
    public PendingFeedbackResponse getPendingFeedbackByTeacher(UUID teacherId) {
        // Repository에서 피드백 대기 과제 목록 조회
        List<PendingFeedbackResponse.PendingAssignment> allPendingAssignments = 
            teacherAssignmentRepository.findPendingFeedbackByTeacher(teacherId);

        // 강의별로 그룹핑
        Map<UUID, List<PendingFeedbackResponse.PendingAssignment>> groupedByLecture = 
            allPendingAssignments.stream()
                .collect(Collectors.groupingBy(assignment -> {
                    // lectureId는 Repository에서 조회해올 예정 (강의 정보가 필요)
                    return teacherAssignmentRepository.getLectureIdByDetailId(assignment.lectureDetailId());
                }));

        // 강의 그룹 생성
        List<PendingFeedbackResponse.LectureGroup> lectureGroups = 
            groupedByLecture.entrySet().stream()
                .map(entry -> {
                    UUID lectureId = entry.getKey();
                    List<PendingFeedbackResponse.PendingAssignment> assignments = entry.getValue();
                    
                    // 강의 정보 조회
                    var lectureInfo = teacherAssignmentRepository.getLectureInfo(lectureId);
                    
                    return new PendingFeedbackResponse.LectureGroup(
                        lectureId,
                        lectureInfo.title(),
                        lectureInfo.category(),
                        assignments.size(),
                        assignments
                    );
                })
                .sorted((a, b) -> a.lectureTitle().compareTo(b.lectureTitle())) // 강의명으로 정렬
                .toList();

        // 전체 대기 건수 계산
        int totalPendingCount = allPendingAssignments.size();

        return new PendingFeedbackResponse(lectureGroups, totalPendingCount);
    }

    /**
     * 학생 과제에 피드백 제출 및 진도 상태 업데이트
     * 
     * @param submissionId 제출물 ID
     * @param request 피드백 요청 정보
     * @param teacherId 선생님 ID
     */
    @Transactional
    public void submitFeedback(UUID submissionId, SubmitFeedbackRequest request, UUID teacherId) {
        // 1. 제출물 정보 조회 및 권한 확인
        var submissionInfo = teacherAssignmentRepository.getSubmissionInfo(submissionId);
        teacherAssignmentRepository.validateTeacherAccess(submissionId, teacherId);
        
        // 2. 피드백 업데이트
        teacherAssignmentRepository.updateFeedback(submissionId, request.feedback());
        
        // 3. 학생 진도 상태 업데이트
        teacherAssignmentRepository.updateStudentProgress(
            submissionInfo.userId(), 
            submissionInfo.lectureDetailId(), 
            request.isCompleted()
        );
    }
}