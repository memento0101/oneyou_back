package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.jooq.generated.tables.pojos.LectureAttachment;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentRequest;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.LectureAttachment.LECTURE_ATTACHMENT;

@Repository
@RequiredArgsConstructor
public class LectureAttachmentRepository {

    private final DSLContext dsl;

    public List<LectureAttachmentResponse> findAttachmentsByLectureDetailId(UUID lectureDetailId) {
        return dsl.select(
                        LECTURE_ATTACHMENT.ID,
                        LECTURE_ATTACHMENT.LECTURE_DETAIL_ID,
                        LECTURE_ATTACHMENT.FILE_NAME,
                        LECTURE_ATTACHMENT.ORIGINAL_FILE_NAME,
                        LECTURE_ATTACHMENT.FILE_PATH,
                        LECTURE_ATTACHMENT.FILE_SIZE,
                        LECTURE_ATTACHMENT.FILE_TYPE,
                        LECTURE_ATTACHMENT.MIME_TYPE,
                        LECTURE_ATTACHMENT.DESCRIPTION,
                        LECTURE_ATTACHMENT.DOWNLOAD_COUNT,
                        LECTURE_ATTACHMENT.CREATED_AT,
                        LECTURE_ATTACHMENT.UPDATED_AT
                )
                .from(LECTURE_ATTACHMENT)
                .where(LECTURE_ATTACHMENT.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .orderBy(LECTURE_ATTACHMENT.CREATED_AT.asc())
                .fetchInto(LectureAttachmentResponse.class);
    }

    public List<LectureAttachmentResponse> findAttachmentsByLectureDetailIds(List<UUID> lectureDetailIds) {
        return dsl.select(
                        LECTURE_ATTACHMENT.ID,
                        LECTURE_ATTACHMENT.LECTURE_DETAIL_ID,
                        LECTURE_ATTACHMENT.FILE_NAME,
                        LECTURE_ATTACHMENT.ORIGINAL_FILE_NAME,
                        LECTURE_ATTACHMENT.FILE_PATH,
                        LECTURE_ATTACHMENT.FILE_SIZE,
                        LECTURE_ATTACHMENT.FILE_TYPE,
                        LECTURE_ATTACHMENT.MIME_TYPE,
                        LECTURE_ATTACHMENT.DESCRIPTION,
                        LECTURE_ATTACHMENT.DOWNLOAD_COUNT,
                        LECTURE_ATTACHMENT.CREATED_AT,
                        LECTURE_ATTACHMENT.UPDATED_AT
                )
                .from(LECTURE_ATTACHMENT)
                .where(LECTURE_ATTACHMENT.LECTURE_DETAIL_ID.in(lectureDetailIds))
                .orderBy(LECTURE_ATTACHMENT.LECTURE_DETAIL_ID.asc(), LECTURE_ATTACHMENT.CREATED_AT.asc())
                .fetchInto(LectureAttachmentResponse.class);
    }

    public LectureAttachmentResponse findAttachmentById(UUID id) {
        return dsl.select(
                        LECTURE_ATTACHMENT.ID,
                        LECTURE_ATTACHMENT.LECTURE_DETAIL_ID,
                        LECTURE_ATTACHMENT.FILE_NAME,
                        LECTURE_ATTACHMENT.ORIGINAL_FILE_NAME,
                        LECTURE_ATTACHMENT.FILE_PATH,
                        LECTURE_ATTACHMENT.FILE_SIZE,
                        LECTURE_ATTACHMENT.FILE_TYPE,
                        LECTURE_ATTACHMENT.MIME_TYPE,
                        LECTURE_ATTACHMENT.DESCRIPTION,
                        LECTURE_ATTACHMENT.DOWNLOAD_COUNT,
                        LECTURE_ATTACHMENT.CREATED_AT,
                        LECTURE_ATTACHMENT.UPDATED_AT
                )
                .from(LECTURE_ATTACHMENT)
                .where(LECTURE_ATTACHMENT.ID.eq(id))
                .fetchOneInto(LectureAttachmentResponse.class);
    }

    public LectureAttachmentResponse insertAttachment(LectureAttachmentRequest request) {
        UUID attachmentId = UUID.randomUUID();

        dsl.insertInto(LECTURE_ATTACHMENT)
                .set(LECTURE_ATTACHMENT.ID, attachmentId)
                .set(LECTURE_ATTACHMENT.LECTURE_DETAIL_ID, request.lectureDetailId())
                .set(LECTURE_ATTACHMENT.FILE_NAME, request.fileName())
                .set(LECTURE_ATTACHMENT.ORIGINAL_FILE_NAME, request.originalFileName())
                .set(LECTURE_ATTACHMENT.FILE_PATH, request.filePath())
                .set(LECTURE_ATTACHMENT.FILE_SIZE, request.fileSize())
                .set(LECTURE_ATTACHMENT.FILE_TYPE, request.fileType())
                .set(LECTURE_ATTACHMENT.MIME_TYPE, request.mimeType())
                .set(LECTURE_ATTACHMENT.DESCRIPTION, request.description())
                .set(LECTURE_ATTACHMENT.DOWNLOAD_COUNT, 0)
                .set(LECTURE_ATTACHMENT.CREATED_AT, LocalDateTime.now())
                .set(LECTURE_ATTACHMENT.UPDATED_AT, LocalDateTime.now())
                .execute();

        return findAttachmentById(attachmentId);
    }

    public LectureAttachmentResponse updateAttachment(UUID id, LectureAttachmentRequest request) {
        dsl.update(LECTURE_ATTACHMENT)
                .set(LECTURE_ATTACHMENT.LECTURE_DETAIL_ID, request.lectureDetailId())
                .set(LECTURE_ATTACHMENT.FILE_NAME, request.fileName())
                .set(LECTURE_ATTACHMENT.ORIGINAL_FILE_NAME, request.originalFileName())
                .set(LECTURE_ATTACHMENT.FILE_PATH, request.filePath())
                .set(LECTURE_ATTACHMENT.FILE_SIZE, request.fileSize())
                .set(LECTURE_ATTACHMENT.FILE_TYPE, request.fileType())
                .set(LECTURE_ATTACHMENT.MIME_TYPE, request.mimeType())
                .set(LECTURE_ATTACHMENT.DESCRIPTION, request.description())
                .set(LECTURE_ATTACHMENT.UPDATED_AT, LocalDateTime.now())
                .where(LECTURE_ATTACHMENT.ID.eq(id))
                .execute();

        return findAttachmentById(id);
    }

    public void incrementDownloadCount(UUID id) {
        dsl.update(LECTURE_ATTACHMENT)
                .set(LECTURE_ATTACHMENT.DOWNLOAD_COUNT, LECTURE_ATTACHMENT.DOWNLOAD_COUNT.plus(1))
                .set(LECTURE_ATTACHMENT.UPDATED_AT, LocalDateTime.now())
                .where(LECTURE_ATTACHMENT.ID.eq(id))
                .execute();
    }

    public void deleteAttachment(UUID id) {
        dsl.deleteFrom(LECTURE_ATTACHMENT)
                .where(LECTURE_ATTACHMENT.ID.eq(id))
                .execute();
    }

    public boolean existsById(UUID id) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(LECTURE_ATTACHMENT)
                        .where(LECTURE_ATTACHMENT.ID.eq(id))
        );
    }
}