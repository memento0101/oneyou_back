package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.common.constants.Role;
import com.example.toygry.one_you.lecture.dto.StudentLectureResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class StudentLectureRepository {

    private final DSLContext dsl;

    // 학생의 활성화 된 강의 목록 출력
    public List<StudentLectureResponse> findActiveLecturesByUser(UUID userId) {
        LocalDateTime now = LocalDateTime.now();

        return dsl.select(
                        USERS.ID.as("teacherId"),
                        USERS.NAME.as("teacherName"),
                        LECTURE.ID.as("lectureId"),
                        LECTURE.TITLE.as("lectureTitle"),
                        LECTURE.IMAGE,
                        STUDENT_LECTURE.EXPIRE_DATE
                )
                .from(STUDENT_LECTURE)
                .join(LECTURE).on(STUDENT_LECTURE.LECTURE_ID.eq(LECTURE.ID))
                .join(USERS).on(LECTURE.TEACHER_ID.eq(USERS.ID).and(USERS.ROLE.eq(Role.TEACHER)))
                .where(STUDENT_LECTURE.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE.EXPIRE_DATE.gt(now)))
                .fetchInto(StudentLectureResponse.class);
    }

    // 강의 만료 날짜 조회
    public LocalDateTime findLectureExpireDate(UUID userId, UUID lectureId) {
        return dsl.select(STUDENT_LECTURE.EXPIRE_DATE)
                .from(STUDENT_LECTURE)
                .where(STUDENT_LECTURE.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE.LECTURE_ID.eq(lectureId)))
                .fetchOneInto(LocalDateTime.class);
    }

}
