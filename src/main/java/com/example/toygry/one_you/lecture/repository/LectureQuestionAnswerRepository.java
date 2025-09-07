package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.lecture.dto.LectureQuestionAnswerResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class LectureQuestionAnswerRepository {

    private final DSLContext dsl;

    public UUID createAnswer(UUID questionId, UUID teacherId, String content) {
        return dsl.insertInto(LECTURE_QUESTION_ANSWER)
                .set(LECTURE_QUESTION_ANSWER.QUESTION_ID, questionId)
                .set(LECTURE_QUESTION_ANSWER.TEACHER_ID, teacherId)
                .set(LECTURE_QUESTION_ANSWER.CONTENT, content)
                .returningResult(LECTURE_QUESTION_ANSWER.ID)
                .fetchOne()
                .get(LECTURE_QUESTION_ANSWER.ID);
    }

    public void updateAnswer(UUID questionId, String content) {
        dsl.update(LECTURE_QUESTION_ANSWER)
                .set(LECTURE_QUESTION_ANSWER.CONTENT, content)
                .set(LECTURE_QUESTION_ANSWER.UPDATED_AT, LocalDateTime.now())
                .where(LECTURE_QUESTION_ANSWER.QUESTION_ID.eq(questionId))
                .execute();
    }

    public Optional<LectureQuestionAnswerResponse> findAnswerByQuestionId(UUID questionId) {
        return dsl.select(
                        LECTURE_QUESTION_ANSWER.ID,
                        LECTURE_QUESTION_ANSWER.QUESTION_ID,
                        LECTURE_QUESTION_ANSWER.TEACHER_ID,
                        USERS.NAME.as("teacherName"),
                        LECTURE_QUESTION_ANSWER.CONTENT,
                        LECTURE_QUESTION_ANSWER.CREATED_AT,
                        LECTURE_QUESTION_ANSWER.UPDATED_AT
                )
                .from(LECTURE_QUESTION_ANSWER)
                .join(USERS).on(LECTURE_QUESTION_ANSWER.TEACHER_ID.eq(USERS.ID))
                .where(LECTURE_QUESTION_ANSWER.QUESTION_ID.eq(questionId))
                .fetchOptionalInto(LectureQuestionAnswerResponse.class);
    }

    public boolean existsByQuestionId(UUID questionId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(LECTURE_QUESTION_ANSWER)
                        .where(LECTURE_QUESTION_ANSWER.QUESTION_ID.eq(questionId))
        );
    }
}