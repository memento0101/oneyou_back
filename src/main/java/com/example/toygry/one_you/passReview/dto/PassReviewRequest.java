package com.example.toygry.one_you.passReview.dto;

import java.util.UUID;

public record PassReviewRequest(
        String title,
        String academySelectionReason,     // 학원선택이유
        String satisfyingContentReason,   // 만족스러웠던 콘텐츠와 이유
        String studyMethod,               // 학습노하우
        String adviceForStudents,         // 다른 수강생에게 조언
        UUID universityId,                // 대학 ID (외래키)
        String major,                     // 합격 학과
        Integer passYear
) {}

