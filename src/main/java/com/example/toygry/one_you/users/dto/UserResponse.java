package com.example.toygry.one_you.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String userId,
        String name,
        // 학생 관련 필드
        String studentContact,
        String parentContact,
        String address,
        List<GoalUniversity> goalUniversities,
        Integer studyYears,
        String majorType,
        EjuScores ejuScores,
        String note,
        // 선생님 관련 필드
        String image,
        List<String> teachingSubjects,
        String bankName,
        String accountNumber,
        String accountHolder,
        String businessNumber,
        // 공통 필드
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String role
) {
    public record GoalUniversity(String school, String major) {}
    public record EjuScores(
            Integer listening,
            Integer reading,
            @JsonProperty("total_listen_read")
            Integer totalListenRead,
            Integer science,
            String subject1,
            Integer score1,
            String subject2,
            Integer score2
    ) {}
}
