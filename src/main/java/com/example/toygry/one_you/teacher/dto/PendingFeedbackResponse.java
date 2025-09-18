package com.example.toygry.one_you.teacher.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 피드백 대기 과제 응답 DTO
 */
public record PendingFeedbackResponse(
        List<LectureGroup> lectureGroups,
        int totalPendingCount
) {
    
    /**
     * 강의별 그룹 정보
     */
    public record LectureGroup(
            UUID lectureId,
            String lectureTitle,
            String lectureCategory,
            int pendingCount,
            List<PendingAssignment> assignments
    ) {}
    
    /**
     * 피드백 대기 과제 정보
     */
    public record PendingAssignment(
            UUID submissionId,
            UUID lectureDetailId,
            String chapterTitle,
            String detailTitle,
            String detailType,        // VIDEO, LIVE, QUIZ
            UUID studentId,
            String studentName,
            String reviewUrl,         // 유튜브 링크
            LocalDateTime submittedAt
    ) {}
}