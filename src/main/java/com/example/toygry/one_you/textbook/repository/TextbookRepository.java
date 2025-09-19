package com.example.toygry.one_you.textbook.repository;

import com.example.toygry.one_you.jooq.generated.tables.pojos.Textbook;
import com.example.toygry.one_you.textbook.dto.TextbookRequest;
import com.example.toygry.one_you.textbook.dto.TextbookResponse;
import com.example.toygry.one_you.textbook.dto.TextbookUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Textbook.TEXTBOOK;
import static com.example.toygry.one_you.jooq.generated.tables.Lecture.LECTURE;

@Repository
@RequiredArgsConstructor
public class TextbookRepository {

    private final DSLContext dsl;

    public List<TextbookResponse> findAllTextbooks() {
        return dsl.select(
                        TEXTBOOK.ID,
                        TEXTBOOK.NAME,
                        TEXTBOOK.DESCRIPTION,
                        TEXTBOOK.DESCRIPTION_IMG,
                        TEXTBOOK.IMG,
                        TEXTBOOK.LINK,
                        TEXTBOOK.PRICE,
                        TEXTBOOK.LECTURE_ID,
                        LECTURE.TITLE.as("lectureTitle"),
                        TEXTBOOK.CREATED_AT,
                        TEXTBOOK.UPDATED_AT
                )
                .from(TEXTBOOK)
                .leftJoin(LECTURE).on(TEXTBOOK.LECTURE_ID.eq(LECTURE.ID))
                .orderBy(TEXTBOOK.CREATED_AT.desc())
                .fetchInto(TextbookResponse.class);
    }

    public List<TextbookResponse.SimpleTextbookResponse> findTextbooksByLectureId(UUID lectureId) {
        return dsl.select(
                        TEXTBOOK.ID,
                        TEXTBOOK.NAME,
                        TEXTBOOK.DESCRIPTION,
                        TEXTBOOK.DESCRIPTION_IMG,
                        TEXTBOOK.IMG,
                        TEXTBOOK.LINK,
                        TEXTBOOK.PRICE,
                        TEXTBOOK.CREATED_AT,
                        TEXTBOOK.UPDATED_AT
                )
                .from(TEXTBOOK)
                .where(TEXTBOOK.LECTURE_ID.eq(lectureId))
                .orderBy(TEXTBOOK.CREATED_AT.desc())
                .fetchInto(TextbookResponse.SimpleTextbookResponse.class);
    }

    public TextbookResponse findTextbookById(UUID id) {
        return dsl.select(
                        TEXTBOOK.ID,
                        TEXTBOOK.NAME,
                        TEXTBOOK.DESCRIPTION,
                        TEXTBOOK.DESCRIPTION_IMG,
                        TEXTBOOK.IMG,
                        TEXTBOOK.LINK,
                        TEXTBOOK.PRICE,
                        TEXTBOOK.LECTURE_ID,
                        LECTURE.TITLE.as("lectureTitle"),
                        TEXTBOOK.CREATED_AT,
                        TEXTBOOK.UPDATED_AT
                )
                .from(TEXTBOOK)
                .leftJoin(LECTURE).on(TEXTBOOK.LECTURE_ID.eq(LECTURE.ID))
                .where(TEXTBOOK.ID.eq(id))
                .fetchOneInto(TextbookResponse.class);
    }

    public Textbook findSimpleTextbookById(UUID id) {
        return dsl.selectFrom(TEXTBOOK)
                .where(TEXTBOOK.ID.eq(id))
                .fetchOneInto(Textbook.class);
    }

    public UUID createTextbook(TextbookRequest request) {
        UUID textbookId = UUID.randomUUID();

        dsl.insertInto(TEXTBOOK)
                .set(TEXTBOOK.ID, textbookId)
                .set(TEXTBOOK.NAME, request.name())
                .set(TEXTBOOK.DESCRIPTION, request.description())
                .set(TEXTBOOK.DESCRIPTION_IMG, request.descriptionImg())
                .set(TEXTBOOK.IMG, request.img())
                .set(TEXTBOOK.LINK, request.link())
                .set(TEXTBOOK.PRICE, request.price())
                .set(TEXTBOOK.LECTURE_ID, request.lectureId())
                .set(TEXTBOOK.CREATED_AT, LocalDateTime.now())
                .set(TEXTBOOK.UPDATED_AT, LocalDateTime.now())
                .execute();

        return textbookId;
    }

    public void updateTextbook(UUID id, TextbookUpdateRequest request) {
        dsl.update(TEXTBOOK)
                .set(TEXTBOOK.NAME, request.name())
                .set(TEXTBOOK.DESCRIPTION, request.description())
                .set(TEXTBOOK.DESCRIPTION_IMG, request.descriptionImg())
                .set(TEXTBOOK.IMG, request.img())
                .set(TEXTBOOK.LINK, request.link())
                .set(TEXTBOOK.PRICE, request.price())
                .set(TEXTBOOK.LECTURE_ID, request.lectureId())
                .set(TEXTBOOK.UPDATED_AT, LocalDateTime.now())
                .where(TEXTBOOK.ID.eq(id))
                .execute();
    }

    public void deleteTextbook(UUID id) {
        dsl.deleteFrom(TEXTBOOK)
                .where(TEXTBOOK.ID.eq(id))
                .execute();
    }

    public boolean existsById(UUID id) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(TEXTBOOK)
                        .where(TEXTBOOK.ID.eq(id))
        );
    }
}