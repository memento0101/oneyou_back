package com.example.toygry.one_you.review.repository;

import com.example.toygry.one_you.jooq.generated.tables.Review;
import com.example.toygry.one_you.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Review.REVIEW;
import static com.example.toygry.one_you.jooq.generated.tables.Lecture.LECTURE;
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
                .map(record -> new ReviewResponse(
                        record.get(REVIEW.ID),
                        record.get(REVIEW.LECTURE_ID),
                        record.get("lectureTitle",String.class),
                        record.get("userName",String.class),
                        record.get(REVIEW.CONTENTS),
                        record.get(REVIEW.SCORE),
                        record.get(REVIEW.CREATED_AT),
                        record.get(REVIEW.UPDATED_AT)
                ));

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
                .map(record -> new ReviewResponse(
                        record.get(REVIEW.ID),
                        record.get(REVIEW.LECTURE_ID),
                        record.get("lectureTitle", String.class),
                        record.get("userName", String.class),
                        record.get(REVIEW.CONTENTS),
                        record.get(REVIEW.SCORE),
                        record.get(REVIEW.CREATED_AT),
                        record.get(REVIEW.UPDATED_AT)
                ));
    }

}
