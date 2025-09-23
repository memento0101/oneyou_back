package com.example.toygry.one_you.lecture.repository;

import com.example.toygry.one_you.lecture.dto.LectureCreateResponse;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.LectureImage.LECTURE_IMAGE;

@Repository
public class LectureImageRepository {

    private final DSLContext dsl;

    public LectureImageRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // 강의 이미지 저장
    public UUID insertLectureImage(UUID lectureId, String originalFileName, String storedFileName,
                                   String filePath, Long fileSize, String mimeType, Integer displayOrder,
                                   Boolean isPrimary, String altText, String description, UUID uploadedBy) {
        UUID imageId = UUID.randomUUID();

        dsl.insertInto(LECTURE_IMAGE)
                .set(LECTURE_IMAGE.ID, imageId)
                .set(LECTURE_IMAGE.LECTURE_ID, lectureId)
                .set(LECTURE_IMAGE.ORIGINAL_FILE_NAME, originalFileName)
                .set(LECTURE_IMAGE.STORED_FILE_NAME, storedFileName)
                .set(LECTURE_IMAGE.FILE_PATH, filePath)
                .set(LECTURE_IMAGE.FILE_SIZE, fileSize)
                .set(LECTURE_IMAGE.MIME_TYPE, mimeType)
                .set(LECTURE_IMAGE.DISPLAY_ORDER, displayOrder)
                .set(LECTURE_IMAGE.IS_PRIMARY, isPrimary)
                .set(LECTURE_IMAGE.ALT_TEXT, altText)
                .set(LECTURE_IMAGE.DESCRIPTION, description)
                .set(LECTURE_IMAGE.UPLOADED_BY, uploadedBy)
                .execute();

        return imageId;
    }

    // 강의별 이미지 목록 조회 (순서대로)
    public List<LectureCreateResponse.LectureImageInfo> findImagesByLectureId(UUID lectureId) {
        return dsl.select(
                        LECTURE_IMAGE.ID,
                        LECTURE_IMAGE.ORIGINAL_FILE_NAME,
                        LECTURE_IMAGE.FILE_PATH,
                        LECTURE_IMAGE.IS_PRIMARY,
                        LECTURE_IMAGE.DISPLAY_ORDER,
                        LECTURE_IMAGE.ALT_TEXT,
                        LECTURE_IMAGE.DESCRIPTION
                )
                .from(LECTURE_IMAGE)
                .where(LECTURE_IMAGE.LECTURE_ID.eq(lectureId))
                .orderBy(LECTURE_IMAGE.DISPLAY_ORDER.asc())
                .fetch(record -> new LectureCreateResponse.LectureImageInfo(
                        record.get(LECTURE_IMAGE.ID),
                        record.get(LECTURE_IMAGE.ORIGINAL_FILE_NAME),
                        record.get(LECTURE_IMAGE.FILE_PATH),
                        record.get(LECTURE_IMAGE.IS_PRIMARY),
                        record.get(LECTURE_IMAGE.DISPLAY_ORDER),
                        record.get(LECTURE_IMAGE.ALT_TEXT),
                        record.get(LECTURE_IMAGE.DESCRIPTION)
                ));
    }

    // 강의의 대표 이미지 해제 (새로운 대표 이미지 설정 전)
    public void clearPrimaryImage(UUID lectureId) {
        dsl.update(LECTURE_IMAGE)
                .set(LECTURE_IMAGE.IS_PRIMARY, false)
                .where(LECTURE_IMAGE.LECTURE_ID.eq(lectureId))
                .execute();
    }

    // 이미지 삭제
    public void deleteImage(UUID imageId) {
        dsl.deleteFrom(LECTURE_IMAGE)
                .where(LECTURE_IMAGE.ID.eq(imageId))
                .execute();
    }

    // 강의의 모든 이미지 삭제
    public void deleteAllImagesByLectureId(UUID lectureId) {
        dsl.deleteFrom(LECTURE_IMAGE)
                .where(LECTURE_IMAGE.LECTURE_ID.eq(lectureId))
                .execute();
    }
}