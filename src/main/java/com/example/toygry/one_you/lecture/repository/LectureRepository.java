package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.lecture.dto.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.toygry.one_you.jooq.generated.Tables.LECTURE_CATEGORY;
import static com.example.toygry.one_you.jooq.generated.tables.Lecture.LECTURE;

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
}
