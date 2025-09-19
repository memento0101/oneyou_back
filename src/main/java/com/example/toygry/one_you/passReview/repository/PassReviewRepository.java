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
import static com.example.toygry.one_you.jooq.generated.Tables.UNIVERSITY;

@Repository
@RequiredArgsConstructor
public class PassReviewRepository {

    private final DSLContext dsl;

    public List<PassReviewResponse> findAll() {
        return dsl.select()
                .from(PASS_REVIEW)
                .join(UNIVERSITY).on(PASS_REVIEW.UNIVERSITY_ID.eq(UNIVERSITY.ID))
                .orderBy(PASS_REVIEW.CREATED_AT.desc())
                .fetch()
                .map(PassReviewResponse::fromRecord);
    }

    public List<PassReviewResponse> findByUniversityType(String universityType) {
        return dsl.select()
                .from(PASS_REVIEW)
                .join(UNIVERSITY).on(PASS_REVIEW.UNIVERSITY_ID.eq(UNIVERSITY.ID))
                .where(UNIVERSITY.UNIVERSITY_TYPE.eq(universityType))
                .orderBy(PASS_REVIEW.CREATED_AT.desc())
                .fetch()
                .map(PassReviewResponse::fromRecord);
    }

    public List<PassReviewResponse> findByUniversityId(UUID universityId) {
        return dsl.select()
                .from(PASS_REVIEW)
                .join(UNIVERSITY).on(PASS_REVIEW.UNIVERSITY_ID.eq(UNIVERSITY.ID))
                .where(PASS_REVIEW.UNIVERSITY_ID.eq(universityId))
                .orderBy(PASS_REVIEW.CREATED_AT.desc())
                .fetch()
                .map(PassReviewResponse::fromRecord);
    }

    public List<PassReviewResponse> findByPassYear(Integer passYear) {
        return dsl.select()
                .from(PASS_REVIEW)
                .join(UNIVERSITY).on(PASS_REVIEW.UNIVERSITY_ID.eq(UNIVERSITY.ID))
                .where(PASS_REVIEW.PASS_YEAR.eq(passYear))
                .orderBy(PASS_REVIEW.CREATED_AT.desc())
                .fetch()
                .map(PassReviewResponse::fromRecord);
    }

    public void save(UUID userId, PassReviewRequest request) {
        dsl.insertInto(PASS_REVIEW)
                .set(PASS_REVIEW.ID, UUID.randomUUID())
                .set(PASS_REVIEW.USER_ID, userId)
                .set(PASS_REVIEW.TITLE, request.title())
                .set(PASS_REVIEW.ACADEMY_SELECTION_REASON, request.academySelectionReason())
                .set(PASS_REVIEW.SATISFYING_CONTENT_REASON, request.satisfyingContentReason())
                .set(PASS_REVIEW.STUDY_METHOD, request.studyMethod())
                .set(PASS_REVIEW.ADVICE_FOR_STUDENTS, request.adviceForStudents())
                .set(PASS_REVIEW.UNIVERSITY_ID, request.universityId())
                .set(PASS_REVIEW.MAJOR, request.major())
                .set(PASS_REVIEW.PASS_YEAR, request.passYear())
                .set(PASS_REVIEW.CREATED_AT, LocalDateTime.now())
                .set(PASS_REVIEW.UPDATED_AT, LocalDateTime.now())
                .execute();
    }
}
