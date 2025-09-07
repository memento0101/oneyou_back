package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.jooq.generated.tables.records.StudentReviewSubmissionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.STUDENT_REVIEW_SUBMISSION;

@Repository
@RequiredArgsConstructor
public class StudentReviewSubmissionRepository {

    private final DSLContext dsl;

    public StudentReviewSubmissionRecord findSubmissionByUserAndLectureDetail(UUID userId, UUID lectureDetailId) {
        return dsl.selectFrom(STUDENT_REVIEW_SUBMISSION)
                .where(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(userId)
                        .and(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                .fetchOneInto(StudentReviewSubmissionRecord.class);
    }

    public void insertOrUpdateSubmission(UUID userId, UUID lectureDetailId, String reviewUrl) {
        StudentReviewSubmissionRecord existing = findSubmissionByUserAndLectureDetail(userId, lectureDetailId);
        LocalDateTime now = LocalDateTime.now();

        if (existing != null) {
            dsl.update(STUDENT_REVIEW_SUBMISSION)
                    .set(STUDENT_REVIEW_SUBMISSION.REVIEW_URL, reviewUrl)
                    .set(STUDENT_REVIEW_SUBMISSION.UPDATED_AT, now)
                    .where(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(userId)
                            .and(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                    .execute();
        } else {
            dsl.insertInto(STUDENT_REVIEW_SUBMISSION)
                    .set(STUDENT_REVIEW_SUBMISSION.ID, UUID.randomUUID())
                    .set(STUDENT_REVIEW_SUBMISSION.USER_ID, userId)
                    .set(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID, lectureDetailId)
                    .set(STUDENT_REVIEW_SUBMISSION.REVIEW_URL, reviewUrl)
                    .set(STUDENT_REVIEW_SUBMISSION.CREATED_AT, now)
                    .set(STUDENT_REVIEW_SUBMISSION.UPDATED_AT, now)
                    .execute();
        }
    }
}