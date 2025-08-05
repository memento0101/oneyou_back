package com.example.toygry.one_you.passReview.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.PASS_REVIEW;

public record PassReviewResponse(
        UUID id,
        UUID userId,
        String title,
        String contents,
        String targetUniversity,
        Integer passYear,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PassReviewResponse fromRecord(Record record) {
        return new PassReviewResponse(
                record.get(PASS_REVIEW.ID),
                record.get(PASS_REVIEW.USER_ID),
                record.get(PASS_REVIEW.TITLE),
                record.get(PASS_REVIEW.CONTENTS),
                record.get(PASS_REVIEW.TARGET_UNIVERSITY),
                record.get(PASS_REVIEW.PASS_YEAR),
                record.get(PASS_REVIEW.CREATED_AT),
                record.get(PASS_REVIEW.UPDATED_AT)
        );
    }
}
