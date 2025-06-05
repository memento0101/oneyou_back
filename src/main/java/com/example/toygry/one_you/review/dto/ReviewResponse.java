package com.example.toygry.one_you.review.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Review.REVIEW;

public record ReviewResponse(
        UUID id,
        UUID lectureId,
        String lectureTitle,
        String userName,
        String contents,
        int score,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse fromRecord(Record record) {
        return new ReviewResponse(
                record.get(REVIEW.ID),
                record.get(REVIEW.LECTURE_ID),
                record.get("lectureTitle", String.class),
                record.get("userName", String.class),
                record.get(REVIEW.CONTENTS),
                record.get(REVIEW.SCORE),
                record.get(REVIEW.CREATED_AT),
                record.get(REVIEW.UPDATED_AT)
        );
    }
}
