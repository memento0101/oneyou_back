package com.example.toygry.one_you.review.repository;

import com.example.toygry.one_you.jooq.generated.tables.Review;
import com.example.toygry.one_you.jooq.generated.tables.records.ReviewRecord;
import com.example.toygry.one_you.review.dto.ReviewRequest;
import com.example.toygry.one_you.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Lecture.LECTURE;
import static com.example.toygry.one_you.jooq.generated.tables.Review.REVIEW;
import static com.example.toygry.one_you.jooq.generated.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

    private final DSLContext dsl;

    public List<ReviewResponse> findAllReviews() {
        return dsl.select(
                REVIEW.ID,
                REVIEW.LECTURE_ID,
                REVIEW.CONTENTS,
                REVIEW.SCORE,
                REVIEW.CREATED_AT,
                REVIEW.UPDATED_AT,
                LECTURE.TITLE.as("lectureTitle"),
                USERS.NAME.as("userName")
        )
                .from(REVIEW)
                .join(LECTURE).on(REVIEW.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(REVIEW.USER_ID.eq(USERS.ID))
                .fetch()
                .map(ReviewResponse::fromRecord);

    }
    public List<ReviewResponse> findByLectureId(UUID lectureId) {
        return dsl.select(
                        REVIEW.ID,
                        REVIEW.LECTURE_ID,
                        REVIEW.CONTENTS,
                        REVIEW.SCORE,
                        REVIEW.CREATED_AT,
                        REVIEW.UPDATED_AT,
                        LECTURE.TITLE.as("lectureTitle"),
                        USERS.NAME.as("userName")
                )
                .from(REVIEW)
                .join(LECTURE).on(REVIEW.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(REVIEW.USER_ID.eq(USERS.ID))
                .where(REVIEW.LECTURE_ID.eq(lectureId))
                .fetch()
                .map(ReviewResponse::fromRecord);
    }

    public void saveReview(UUID userId, ReviewRequest request) {
        dsl.insertInto(REVIEW)
                .set(REVIEW.ID, UUID.randomUUID())
                .set(REVIEW.LECTURE_ID, request.lectureId())
                .set(REVIEW.USER_ID, userId)
                .set(REVIEW.CONTENTS, request.contents())
                .set(REVIEW.SCORE, request.score())
                .execute();
    }

    public Optional<ReviewRecord> findById(UUID reviewId) {
        return Optional.ofNullable(dsl.selectFrom(REVIEW)
                .where(REVIEW.ID.eq(reviewId))
                .fetchOne());
    }

    public void updateReview(ReviewRecord review) {
        dsl.update(REVIEW)
                .set(REVIEW.CONTENTS, review.getContents())
                .set(REVIEW.SCORE, review.getScore())
                .where(REVIEW.ID.eq(review.getId()))
                .execute();
    }

    public void deleteReviewById(UUID reviewId) {
        dsl.deleteFrom(REVIEW)
                .where(REVIEW.ID.eq(reviewId))
                .execute();
    }
}
