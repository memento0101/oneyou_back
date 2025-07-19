package com.example.toygry.one_you.passReview.repository;

import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.PASS_REVIEW;

@Repository
@RequiredArgsConstructor
public class PassReviewRepository {

    private final DSLContext dsl;

    public List<PassReviewResponse> findAll() {
        return dsl.selectFrom(PASS_REVIEW)
                .orderBy(PASS_REVIEW.CREATED_AT.desc())
                .fetch()
                .map(PassReviewResponse::fromRecord);
    }

    public void save(UUID userId, PassReviewRequest request) {
        dsl.insertInto(PASS_REVIEW)
                .set(PASS_REVIEW.ID, UUID.randomUUID())
                .set(PASS_REVIEW.USER_ID, userId)
                .set(PASS_REVIEW.TITLE, request.title())
                .set(PASS_REVIEW.CONTENTS, request.contents())
                .set(PASS_REVIEW.TARGET_UNIVERSITY, request.targetUniversity())
                .set(PASS_REVIEW.PASS_YEAR, request.passYear())
                .set(PASS_REVIEW.CREATED_AT, LocalDateTime.now())
                .set(PASS_REVIEW.UPDATED_AT, LocalDateTime.now())
                .execute();
    }



}
