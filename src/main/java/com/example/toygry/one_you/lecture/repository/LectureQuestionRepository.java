package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.lecture.dto.LectureQuestionListResponse;
import com.example.toygry.one_you.lecture.dto.LectureQuestionResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class LectureQuestionRepository {

    private final DSLContext dsl;

    public UUID createQuestion(UUID lectureId, UUID studentId, String title, String content) {
        return dsl.insertInto(LECTURE_QUESTION)
                .set(LECTURE_QUESTION.LECTURE_ID, lectureId)
                .set(LECTURE_QUESTION.STUDENT_ID, studentId)
                .set(LECTURE_QUESTION.TITLE, title)
                .set(LECTURE_QUESTION.CONTENT, content)
                .set(LECTURE_QUESTION.IS_ANSWERED, false)
                .returningResult(LECTURE_QUESTION.ID)
                .fetchOne()
                .get(LECTURE_QUESTION.ID);
    }

    public List<LectureQuestionListResponse> findQuestionsByStudentId(UUID studentId) {
        return dsl.select(
                        LECTURE_QUESTION.ID,
                        LECTURE_QUESTION.LECTURE_ID,
                        LECTURE.TITLE.as("lectureTitle"),
                        USERS.NAME.as("studentName"),
                        LECTURE_QUESTION.TITLE,
                        LECTURE_QUESTION.IS_ANSWERED,
                        LECTURE_QUESTION.CREATED_AT
                )
                .from(LECTURE_QUESTION)
                .join(LECTURE).on(LECTURE_QUESTION.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(LECTURE_QUESTION.STUDENT_ID.eq(USERS.ID))
                .where(LECTURE_QUESTION.STUDENT_ID.eq(studentId))
                .orderBy(LECTURE_QUESTION.CREATED_AT.desc())
                .fetchInto(LectureQuestionListResponse.class);
    }

    public List<LectureQuestionListResponse> findQuestionsByTeacherId(UUID teacherId) {
        return dsl.select(
                        LECTURE_QUESTION.ID,
                        LECTURE_QUESTION.LECTURE_ID,
                        LECTURE.TITLE.as("lectureTitle"),
                        USERS.NAME.as("studentName"),
                        LECTURE_QUESTION.TITLE,
                        LECTURE_QUESTION.IS_ANSWERED,
                        LECTURE_QUESTION.CREATED_AT
                )
                .from(LECTURE_QUESTION)
                .join(LECTURE).on(LECTURE_QUESTION.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(LECTURE_QUESTION.STUDENT_ID.eq(USERS.ID))
                .where(LECTURE.TEACHER_ID.eq(teacherId))
                .orderBy(LECTURE_QUESTION.CREATED_AT.desc())
                .fetchInto(LectureQuestionListResponse.class);
    }

    public Optional<LectureQuestionResponse> findQuestionDetailById(UUID questionId) {
        var question = dsl.select(
                        LECTURE_QUESTION.ID,
                        LECTURE_QUESTION.LECTURE_ID,
                        LECTURE.TITLE.as("lectureTitle"),
                        LECTURE_QUESTION.STUDENT_ID,
                        USERS.NAME.as("studentName"),
                        LECTURE_QUESTION.TITLE,
                        LECTURE_QUESTION.CONTENT,
                        LECTURE_QUESTION.IS_ANSWERED,
                        LECTURE_QUESTION.CREATED_AT,
                        LECTURE_QUESTION.UPDATED_AT
                )
                .from(LECTURE_QUESTION)
                .join(LECTURE).on(LECTURE_QUESTION.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(LECTURE_QUESTION.STUDENT_ID.eq(USERS.ID))
                .where(LECTURE_QUESTION.ID.eq(questionId))
                .fetchOptionalInto(LectureQuestionResponse.class);

        return question;
    }

    public boolean isStudentQuestionOwner(UUID questionId, UUID studentId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(LECTURE_QUESTION)
                        .where(LECTURE_QUESTION.ID.eq(questionId)
                                .and(LECTURE_QUESTION.STUDENT_ID.eq(studentId)))
        );
    }

    public boolean isTeacherQuestionAccessible(UUID questionId, UUID teacherId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(LECTURE_QUESTION)
                        .join(LECTURE).on(LECTURE_QUESTION.LECTURE_ID.eq(LECTURE.ID))
                        .where(LECTURE_QUESTION.ID.eq(questionId)
                                .and(LECTURE.TEACHER_ID.eq(teacherId)))
        );
    }

    public void updateQuestionAnsweredStatus(UUID questionId, boolean isAnswered) {
        dsl.update(LECTURE_QUESTION)
                .set(LECTURE_QUESTION.IS_ANSWERED, isAnswered)
                .set(LECTURE_QUESTION.UPDATED_AT, LocalDateTime.now())
                .where(LECTURE_QUESTION.ID.eq(questionId))
                .execute();
    }
}