package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.jooq.generated.tables.records.LectureReviewRecord;
import com.example.toygry.one_you.jooq.generated.tables.records.UsersRecord;
import com.example.toygry.one_you.lecture.dto.LectureReviewListResponse.RatingStatistics;
import com.example.toygry.one_you.lecture.dto.LectureReviewResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class LectureReviewRepository {

    private final DSLContext dsl;

    public LectureReviewRecord createReview(UUID lectureId, UUID userId, Integer rating, String title, String content, Boolean isAnonymous) {
        LocalDateTime now = LocalDateTime.now();

        return dsl.insertInto(LECTURE_REVIEW)
                .set(LECTURE_REVIEW.ID, UUID.randomUUID())
                .set(LECTURE_REVIEW.LECTURE_ID, lectureId)
                .set(LECTURE_REVIEW.USER_ID, userId)
                .set(LECTURE_REVIEW.RATING, rating)
                .set(LECTURE_REVIEW.TITLE, title)
                .set(LECTURE_REVIEW.CONTENT, content)
                .set(LECTURE_REVIEW.IS_ANONYMOUS, isAnonymous != null ? isAnonymous : false)
                .set(LECTURE_REVIEW.CREATED_AT, now)
                .set(LECTURE_REVIEW.UPDATED_AT, now)
                .returning()
                .fetchOne();
    }

    public Optional<LectureReviewRecord> findByUserIdAndLectureId(UUID userId, UUID lectureId) {
        return Optional.ofNullable(
                dsl.selectFrom(LECTURE_REVIEW)
                        .where(LECTURE_REVIEW.USER_ID.eq(userId)
                                .and(LECTURE_REVIEW.LECTURE_ID.eq(lectureId)))
                        .fetchOne()
        );
    }

    public Optional<LectureReviewRecord> findByIdAndUserId(UUID reviewId, UUID userId) {
        return Optional.ofNullable(
                dsl.selectFrom(LECTURE_REVIEW)
                        .where(LECTURE_REVIEW.ID.eq(reviewId)
                                .and(LECTURE_REVIEW.USER_ID.eq(userId)))
                        .fetchOne()
        );
    }

    public List<LectureReviewResponse> findReviewsByLectureId(UUID lectureId, int offset, int limit) {
        return dsl.select(
                        LECTURE_REVIEW.ID,
                        LECTURE_REVIEW.LECTURE_ID,
                        LECTURE_REVIEW.USER_ID,
                        DSL.when(LECTURE_REVIEW.IS_ANONYMOUS.eq(true), DSL.val("익명"))
                                .otherwise(USERS.NAME).as("userName"),
                        LECTURE_REVIEW.RATING,
                        LECTURE_REVIEW.TITLE,
                        LECTURE_REVIEW.CONTENT,
                        LECTURE_REVIEW.IS_ANONYMOUS,
                        LECTURE_REVIEW.CREATED_AT,
                        LECTURE_REVIEW.UPDATED_AT
                )
                .from(LECTURE_REVIEW)
                .leftJoin(USERS).on(LECTURE_REVIEW.USER_ID.eq(USERS.ID))
                .where(LECTURE_REVIEW.LECTURE_ID.eq(lectureId))
                .orderBy(LECTURE_REVIEW.CREATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(LectureReviewResponse.class);
    }

    public long countReviewsByLectureId(UUID lectureId) {
        return dsl.selectCount()
                .from(LECTURE_REVIEW)
                .where(LECTURE_REVIEW.LECTURE_ID.eq(lectureId))
                .fetchOne(0, Long.class);
    }

    public Double getAverageRatingByLectureId(UUID lectureId) {
        Record1<BigDecimal> result = dsl.select(DSL.avg(LECTURE_REVIEW.RATING))
                .from(LECTURE_REVIEW)
                .where(LECTURE_REVIEW.LECTURE_ID.eq(lectureId))
                .fetchOne();

        return result != null && result.value1() != null ? result.value1().doubleValue() : 0.0;
    }

    public RatingStatistics getRatingStatisticsByLectureId(UUID lectureId) {
        var result = dsl.select(
                        DSL.count(DSL.when(LECTURE_REVIEW.RATING.eq(5), 1)).as("rating5Count"),
                        DSL.count(DSL.when(LECTURE_REVIEW.RATING.eq(4), 1)).as("rating4Count"),
                        DSL.count(DSL.when(LECTURE_REVIEW.RATING.eq(3), 1)).as("rating3Count"),
                        DSL.count(DSL.when(LECTURE_REVIEW.RATING.eq(2), 1)).as("rating2Count"),
                        DSL.count(DSL.when(LECTURE_REVIEW.RATING.eq(1), 1)).as("rating1Count")
                )
                .from(LECTURE_REVIEW)
                .where(LECTURE_REVIEW.LECTURE_ID.eq(lectureId))
                .fetchOne();

        return new RatingStatistics(
                result != null ? result.get("rating5Count", Long.class) : 0L,
                result != null ? result.get("rating4Count", Long.class) : 0L,
                result != null ? result.get("rating3Count", Long.class) : 0L,
                result != null ? result.get("rating2Count", Long.class) : 0L,
                result != null ? result.get("rating1Count", Long.class) : 0L
        );
    }

    public LectureReviewRecord updateReview(UUID reviewId, Integer rating, String title, String content, Boolean isAnonymous) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(LECTURE_REVIEW)
                .set(LECTURE_REVIEW.RATING, rating)
                .set(LECTURE_REVIEW.TITLE, title)
                .set(LECTURE_REVIEW.CONTENT, content)
                .set(LECTURE_REVIEW.IS_ANONYMOUS, isAnonymous != null ? isAnonymous : false)
                .set(LECTURE_REVIEW.UPDATED_AT, now)
                .where(LECTURE_REVIEW.ID.eq(reviewId))
                .execute();

        return dsl.selectFrom(LECTURE_REVIEW)
                .where(LECTURE_REVIEW.ID.eq(reviewId))
                .fetchOne();
    }

    public boolean deleteReview(UUID reviewId, UUID userId) {
        int affectedRows = dsl.deleteFrom(LECTURE_REVIEW)
                .where(LECTURE_REVIEW.ID.eq(reviewId)
                        .and(LECTURE_REVIEW.USER_ID.eq(userId)))
                .execute();

        return affectedRows > 0;
    }

    public boolean hasUserCompletedLecture(UUID userId, UUID lectureId) {
        // 학생이 해당 강의를 수강하고 있는지 확인
        boolean isEnrolled = dsl.selectCount()
                .from(STUDENT_LECTURE)
                .where(STUDENT_LECTURE.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE.LECTURE_ID.eq(lectureId)))
                .fetchOne(0, Integer.class) > 0;

        if (!isEnrolled) {
            return false;
        }

        // 전체 강의 소단원 수 조회
        int totalLectureDetails = dsl.selectCount()
                .from(LECTURE_DETAIL)
                .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .where(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId))
                .fetchOne(0, Integer.class);

        // 완료한 강의 소단원 수 조회
        int completedLectureDetails = dsl.selectCount()
                .from(STUDENT_LECTURE_PROGRESS)
                .join(LECTURE_DETAIL).on(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(LECTURE_DETAIL.ID))
                .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE_PROGRESS.IS_COMPLETED.eq(true))
                        .and(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId)))
                .fetchOne(0, Integer.class);

        // 50% 이상 완료했으면 후기 작성 가능
        return totalLectureDetails > 0 && (completedLectureDetails * 100 / totalLectureDetails) >= 50;
    }
}