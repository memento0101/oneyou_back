package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.jooq.generated.tables.records.*;
import com.example.toygry.one_you.lecture.dto.LectureDetailWithProgressResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;


@Repository
@RequiredArgsConstructor
public class LectureRepository {

    private final DSLContext dsl;

    // 전체 강의 개수 세기
    public int countTotalLectureDetails(UUID lectureId) {
        return dsl.selectCount()
                .from(LECTURE_DETAIL)
                .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .where(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId))
                .fetchOne(0, int.class);
    }

    // 학생이 완료한 강의 개수 조회 (진도 조회용)
    public int countCompletedLectureDetails(UUID userId, UUID lectureId) {
        return dsl.selectCount()
                .from(STUDENT_LECTURE_PROGRESS)
                .join(LECTURE_DETAIL).on(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(LECTURE_DETAIL.ID))
                .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE_PROGRESS.IS_COMPLETED.eq(true))
                        .and(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId)))
                .fetchOne(0, int.class);
    }

    // 목차 조회 및 수강 완료 여부 표현
    public List<LectureDetailWithProgressResponse> findLectureChaptersWithProgress(UUID userId, UUID lectureId) {
        return dsl.select(
                        LECTURE_CHAPTER.ID.as("chapterId"),
                        LECTURE_CHAPTER.TITLE.as("chapterTitle"),
                        LECTURE_DETAIL.ID.as("detailId"),
                        LECTURE_DETAIL.TITLE.as("detailTitle"),
                        LECTURE_DETAIL.TYPE,
                        STUDENT_LECTURE_PROGRESS.IS_COMPLETED
                )
                .from(LECTURE_CHAPTER)
                .join(LECTURE_DETAIL).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .leftJoin(STUDENT_LECTURE_PROGRESS).on(
                        STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(LECTURE_DETAIL.ID)
                                .and(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId))
                )
                .where(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId))
                .orderBy(LECTURE_CHAPTER.CHAPTER_ORDER.asc(), LECTURE_DETAIL.DETAIL_ORDER.asc())
                .fetchInto(LectureDetailWithProgressResponse.class);
    }

    public LectureDetailRecord findLectureDetail(UUID lectureDetailId) {
        return dsl.selectFrom(LECTURE_DETAIL)
                .where(LECTURE_DETAIL.ID.eq(lectureDetailId))
                .fetchOneInto(LectureDetailRecord.class);
    }

    public LectureContentRecord fetchLectureContent(UUID lectureDetailId) {
        return dsl.selectFrom(LECTURE_CONTENT)
                .where(LECTURE_CONTENT.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .fetchOneInto(LectureContentRecord.class);
    }

    public StudentReviewSubmissionRecord fetchStudentReviewSubmission(UUID lectureDetailId, UUID userId) {
        return dsl.selectFrom(STUDENT_REVIEW_SUBMISSION)
                .where(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(lectureDetailId)
                        .and(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(userId)))
                .fetchOneInto(StudentReviewSubmissionRecord.class);
    }

    public List<LectureQuizRecord> fetchLectureQuizzes(UUID lectureDetailId) {
        return dsl.selectFrom(LECTURE_QUIZ)
                .where(LECTURE_QUIZ.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .fetchInto(LectureQuizRecord.class);
    }

    public List<LectureQuizOptionRecord> fetchQuizOptions(UUID quizId) {
        return dsl.selectFrom(LECTURE_QUIZ_OPTION)
                .where(LECTURE_QUIZ_OPTION.LECTURE_QUIZ_ID.eq(quizId))
                .fetchInto(LectureQuizOptionRecord.class);
    }


}
