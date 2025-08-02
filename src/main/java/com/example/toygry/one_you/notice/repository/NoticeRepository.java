package com.example.toygry.one_you.notice.repository;

import com.example.toygry.one_you.jooq.generated.tables.records.NoticeRecord;
import com.example.toygry.one_you.notice.dto.NoticeResponse;
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
public class NoticeRepository {

    private final DSLContext dsl;

    public NoticeRecord createNotice(UUID authorId, String title, String content, Boolean isImportant) {
        LocalDateTime now = LocalDateTime.now();

        return dsl.insertInto(NOTICE)
                .set(NOTICE.ID, UUID.randomUUID())
                .set(NOTICE.TITLE, title)
                .set(NOTICE.CONTENT, content)
                .set(NOTICE.IS_IMPORTANT, isImportant != null ? isImportant : false)
                .set(NOTICE.VIEW_COUNT, 0)
                .set(NOTICE.AUTHOR_ID, authorId)
                .set(NOTICE.CREATED_AT, now)
                .set(NOTICE.UPDATED_AT, now)
                .returning()
                .fetchOne();
    }

    public List<NoticeResponse> findAllNotices(int offset, int limit) {
        return dsl.select(
                        NOTICE.ID,
                        NOTICE.TITLE,
                        NOTICE.CONTENT,
                        NOTICE.IS_IMPORTANT,
                        NOTICE.VIEW_COUNT,
                        NOTICE.AUTHOR_ID,
                        USERS.NAME.as("authorName"),
                        NOTICE.CREATED_AT,
                        NOTICE.UPDATED_AT
                )
                .from(NOTICE)
                .leftJoin(USERS).on(NOTICE.AUTHOR_ID.eq(USERS.ID))
                .orderBy(NOTICE.IS_IMPORTANT.desc(), NOTICE.CREATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(NoticeResponse.class);
    }

    public Optional<NoticeResponse> findNoticeById(UUID noticeId) {
        return Optional.ofNullable(
                dsl.select(
                                NOTICE.ID,
                                NOTICE.TITLE,
                                NOTICE.CONTENT,
                                NOTICE.IS_IMPORTANT,
                                NOTICE.VIEW_COUNT,
                                NOTICE.AUTHOR_ID,
                                USERS.NAME.as("authorName"),
                                NOTICE.CREATED_AT,
                                NOTICE.UPDATED_AT
                        )
                        .from(NOTICE)
                        .leftJoin(USERS).on(NOTICE.AUTHOR_ID.eq(USERS.ID))
                        .where(NOTICE.ID.eq(noticeId))
                        .fetchOneInto(NoticeResponse.class)
        );
    }

    public Optional<NoticeRecord> findNoticeRecordById(UUID noticeId) {
        return Optional.ofNullable(
                dsl.selectFrom(NOTICE)
                        .where(NOTICE.ID.eq(noticeId))
                        .fetchOne()
        );
    }

    public List<NoticeResponse> findTopNotices(int limit) {
        return dsl.select(
                        NOTICE.ID,
                        NOTICE.TITLE,
                        NOTICE.CONTENT,
                        NOTICE.IS_IMPORTANT,
                        NOTICE.VIEW_COUNT,
                        NOTICE.AUTHOR_ID,
                        USERS.NAME.as("authorName"),
                        NOTICE.CREATED_AT,
                        NOTICE.UPDATED_AT
                )
                .from(NOTICE)
                .leftJoin(USERS).on(NOTICE.AUTHOR_ID.eq(USERS.ID))
                .orderBy(NOTICE.CREATED_AT.desc())
                .limit(limit)
                .fetchInto(NoticeResponse.class);
    }

    public NoticeRecord updateNotice(UUID noticeId, String title, String content, Boolean isImportant) {
        LocalDateTime now = LocalDateTime.now();

        dsl.update(NOTICE)
                .set(NOTICE.TITLE, title)
                .set(NOTICE.CONTENT, content)
                .set(NOTICE.IS_IMPORTANT, isImportant != null ? isImportant : false)
                .set(NOTICE.UPDATED_AT, now)
                .where(NOTICE.ID.eq(noticeId))
                .execute();

        return dsl.selectFrom(NOTICE)
                .where(NOTICE.ID.eq(noticeId))
                .fetchOne();
    }

    public boolean deleteNotice(UUID noticeId) {
        int affectedRows = dsl.deleteFrom(NOTICE)
                .where(NOTICE.ID.eq(noticeId))
                .execute();

        return affectedRows > 0;
    }

    public void incrementViewCount(UUID noticeId) {
        dsl.update(NOTICE)
                .set(NOTICE.VIEW_COUNT, NOTICE.VIEW_COUNT.add(1))
                .where(NOTICE.ID.eq(noticeId))
                .execute();
    }

    public long countAllNotices() {
        return dsl.selectCount()
                .from(NOTICE)
                .fetchOne(0, Long.class);
    }
}