package com.example.toygry.one_you.passReview.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.PASS_REVIEW;
import static com.example.toygry.one_you.jooq.generated.Tables.UNIVERSITY;

public record PassReviewResponse(
        UUID id,
        UUID userId,
        String title,
        String academySelectionReason,     // 학원선택이유
        String satisfyingContentReason,   // 만족스러웠던 콘텐츠와 이유
        String studyMethod,               // 학습노하우
        String adviceForStudents,         // 다른 수강생에게 조언
        UniversityInfo university,        // 대학 정보
        String major,                     // 합격 학과
        Integer passYear,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PassReviewResponse fromRecord(Record record) {
        UniversityInfo universityInfo = new UniversityInfo(
                record.get(UNIVERSITY.ID),
                record.get(UNIVERSITY.NAME),
                record.get(UNIVERSITY.LOGO_IMAGE),
                record.get(UNIVERSITY.UNIVERSITY_TYPE)
        );

        return new PassReviewResponse(
                record.get(PASS_REVIEW.ID),
                record.get(PASS_REVIEW.USER_ID),
                record.get(PASS_REVIEW.TITLE),
                record.get(PASS_REVIEW.ACADEMY_SELECTION_REASON),
                record.get(PASS_REVIEW.SATISFYING_CONTENT_REASON),
                record.get(PASS_REVIEW.STUDY_METHOD),
                record.get(PASS_REVIEW.ADVICE_FOR_STUDENTS),
                universityInfo,
                record.get(PASS_REVIEW.MAJOR),
                record.get(PASS_REVIEW.PASS_YEAR),
                record.get(PASS_REVIEW.CREATED_AT),
                record.get(PASS_REVIEW.UPDATED_AT)
        );
    }

    public record UniversityInfo(
            UUID id,
            String name,
            String logoImage,
            String universityType
    ) {}
}
