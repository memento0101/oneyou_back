package com.example.toygry.one_you.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String userId,
        String name,
        String studentContact,
        String parentContact,
        String address,
        List<GoalUniversity> goalUniversities,
        Integer studyYears,
        String majorType,
        EjuScores ejuScores,
        String note,
        LocalDateTime createdAt
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
