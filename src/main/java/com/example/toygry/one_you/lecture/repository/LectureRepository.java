package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.lecture.dto.LectureResponse;
import com.example.toygry.one_you.lecture.dto.LectureUserResponse;
import com.example.toygry.one_you.lecture.dto.UserLectureResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.LECTURE_CATEGORY;
import static com.example.toygry.one_you.jooq.generated.Tables.USER_LECTURE_MAPPING;
import static com.example.toygry.one_you.jooq.generated.tables.Lecture.LECTURE;
import static com.example.toygry.one_you.jooq.generated.tables.Users.USERS;


@Repository
@RequiredArgsConstructor
public class LectureRepository {

    private final DSLContext dsl;

    // 모든 강의 조회 (카테고리 이름 포함)
    public List<LectureResponse> findAllLectures() {
        return dsl.select(
                        LECTURE.ID,
                        LECTURE.TITLE,
                        LECTURE.DESCRIPTION,
                        LECTURE.INSTRUCTOR_NAME,
                        LECTURE.DURATION_MINUTES,
                        LECTURE.CATEGORY_ID,
                        LECTURE_CATEGORY.NAME.as("category_name"),
                        LECTURE.CREATED_AT,
                        LECTURE.UPDATED_AT
                )
                .from(LECTURE)
                .leftJoin(LECTURE_CATEGORY)
                .on(LECTURE.CATEGORY_ID.eq(LECTURE_CATEGORY.ID))
                .fetchInto(LectureResponse.class);
    }

    // 강의 정보 조회 (강의 + 카테고리)
    public LectureResponse findLectureById(UUID lectureId) {
        return dsl.select(
                        LECTURE.ID,
                        LECTURE.TITLE,
                        LECTURE.DESCRIPTION,
                        LECTURE.INSTRUCTOR_NAME,
                        LECTURE.DURATION_MINUTES,
                        LECTURE.CATEGORY_ID,
                        LECTURE_CATEGORY.NAME.as("category_name"),
                        LECTURE.CREATED_AT,
                        LECTURE.UPDATED_AT
                )
                .from(LECTURE)
                .leftJoin(LECTURE_CATEGORY)
                .on(LECTURE.CATEGORY_ID.eq(LECTURE_CATEGORY.ID))
                .where(LECTURE.ID.eq(lectureId))
                .fetchOneInto(LectureResponse.class);
    }

    // 강의를 수강하는 사용자 정보 조회
    public List<LectureUserResponse.UserLectureInfo> getUsersByLectureId(UUID lectureId) {
        return dsl.select(
                        USERS.ID.as("userId"),
                        USERS.USERNAME,
                        USERS.EMAIL,
                        USER_LECTURE_MAPPING.PROGRESS,
                        USER_LECTURE_MAPPING.START_DATE,
                        USER_LECTURE_MAPPING.END_DATE,
                        USER_LECTURE_MAPPING.IS_ACTIVE
                )
                .from(USER_LECTURE_MAPPING)
                .join(USERS).on(USERS.ID.eq(USER_LECTURE_MAPPING.USER_ID))
                .where(USER_LECTURE_MAPPING.LECTURE_ID.eq(lectureId))
                .fetchInto(LectureUserResponse.UserLectureInfo.class);
    }

    public List<UserLectureResponse> findUserLectureByUserId(UUID userId) {
        return dsl.select(
                LECTURE.ID.as("lectureId"),
                LECTURE.TITLE,
                LECTURE.INSTRUCTOR_NAME,
                USER_LECTURE_MAPPING.PROGRESS,
                USER_LECTURE_MAPPING.START_DATE,
                USER_LECTURE_MAPPING.END_DATE,
                USER_LECTURE_MAPPING.IS_ACTIVE,
                LECTURE_CATEGORY.NAME
        )
                .from(USER_LECTURE_MAPPING)
                .join(LECTURE).on(USER_LECTURE_MAPPING.LECTURE_ID.eq(LECTURE.ID))
                .leftJoin(LECTURE_CATEGORY).on(LECTURE.CATEGORY_ID.eq(LECTURE_CATEGORY.ID))
                .where(USER_LECTURE_MAPPING.USER_ID.eq(userId))
                .fetchInto(UserLectureResponse.class);
    }

}
