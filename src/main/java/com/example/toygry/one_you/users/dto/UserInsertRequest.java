package com.example.toygry.one_you.users.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record UserInsertRequest(
        String userId,
        String password,
        String name,
        String studentContact,
        String parentContact,
        String address,
        List<Map<String, String>> goalUniversities,
        Integer studyYears,
        String majorType,
        Map<String, Object> ejuScores,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}