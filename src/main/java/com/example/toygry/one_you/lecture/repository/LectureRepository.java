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

    // 강의 제목 조회
    public String findLectureTitle(UUID lectureId) {
        return dsl.select(LECTURE.TITLE)
                .from(LECTURE)
                .where(LECTURE.ID.eq(lectureId))
                .fetchOne(LECTURE.TITLE);
    }

    // 강의 생성
    public UUID insertLecture(String title, String subtitle, String category, UUID teacherId,
                             String description, Integer price, Integer period, String target, String url) {
        UUID lectureId = UUID.randomUUID();

        dsl.insertInto(LECTURE)
                .set(LECTURE.ID, lectureId)
                .set(LECTURE.TITLE, title)
                .set(LECTURE.SUBTITLE, subtitle)
                .set(LECTURE.CATEGORY, category)
                .set(LECTURE.TEACHER_ID, teacherId)
                .set(LECTURE.DESCRIPTION, description)
                .set(LECTURE.PRICE, price)
                .set(LECTURE.PERIOD, period)
                .set(LECTURE.TARGET, target)
                .set(LECTURE.URL, url)
                .execute();

        return lectureId;
    }

    // 강의 정보와 강사명 조회
    public LectureWithTeacherInfo findLectureWithTeacher(UUID lectureId) {
        return dsl.select(
                        LECTURE.ID,
                        LECTURE.TITLE,
                        LECTURE.SUBTITLE,
                        LECTURE.CATEGORY,
                        LECTURE.DESCRIPTION,
                        LECTURE.PRICE,
                        LECTURE.PERIOD,
                        LECTURE.TARGET,
                        LECTURE.URL,
                        LECTURE.CREATED_AT,
                        USERS.NAME.as("teacherName")
                )
                .from(LECTURE)
                .join(USERS).on(USERS.ID.eq(LECTURE.TEACHER_ID))
                .where(LECTURE.ID.eq(lectureId))
                .fetchOne(record -> new LectureWithTeacherInfo(
                        record.get(LECTURE.ID),
                        record.get(LECTURE.TITLE),
                        record.get(LECTURE.SUBTITLE),
                        record.get(LECTURE.CATEGORY),
                        record.get(LECTURE.DESCRIPTION),
                        record.get(LECTURE.PRICE),
                        record.get(LECTURE.PERIOD),
                        record.get(LECTURE.TARGET),
                        record.get(LECTURE.URL),
                        record.get("teacherName", String.class),
                        record.get(LECTURE.CREATED_AT)
                ));
    }

    // 강의 정보와 강사명을 담는 record 클래스
    public record LectureWithTeacherInfo(
            UUID lectureId,
            String title,
            String subtitle,
            String category,
            String description,
            Integer price,
            Integer period,
            String target,
            String url,
            String teacherName,
            java.time.LocalDateTime createdAt
    ) {}

    // === Chapter CRUD 메서드들 ===

    // 챕터 생성
    public UUID insertChapter(UUID lectureId, String title, Integer chapterOrder) {
        UUID chapterId = UUID.randomUUID();

        dsl.insertInto(LECTURE_CHAPTER)
                .set(LECTURE_CHAPTER.ID, chapterId)
                .set(LECTURE_CHAPTER.LECTURE_ID, lectureId)
                .set(LECTURE_CHAPTER.TITLE, title)
                .set(LECTURE_CHAPTER.CHAPTER_ORDER, chapterOrder)
                .execute();

        return chapterId;
    }

    // 챕터 조회
    public com.example.toygry.one_you.lecture.dto.ChapterResponse findChapterById(UUID chapterId) {
        return dsl.select(
                        LECTURE_CHAPTER.ID,
                        LECTURE_CHAPTER.LECTURE_ID,
                        LECTURE_CHAPTER.TITLE,
                        LECTURE_CHAPTER.CHAPTER_ORDER,
                        LECTURE_CHAPTER.CREATED_AT
                )
                .from(LECTURE_CHAPTER)
                .where(LECTURE_CHAPTER.ID.eq(chapterId))
                .fetchOne(record -> new com.example.toygry.one_you.lecture.dto.ChapterResponse(
                        record.get(LECTURE_CHAPTER.ID),
                        record.get(LECTURE_CHAPTER.LECTURE_ID),
                        record.get(LECTURE_CHAPTER.TITLE),
                        record.get(LECTURE_CHAPTER.CHAPTER_ORDER),
                        record.get(LECTURE_CHAPTER.CREATED_AT)
                ));
    }

    // 챕터 수정
    public void updateChapter(UUID chapterId, String title, Integer chapterOrder) {
        dsl.update(LECTURE_CHAPTER)
                .set(LECTURE_CHAPTER.TITLE, title)
                .set(LECTURE_CHAPTER.CHAPTER_ORDER, chapterOrder)
                .where(LECTURE_CHAPTER.ID.eq(chapterId))
                .execute();
    }

    // 챕터 삭제
    public void deleteChapter(UUID chapterId) {
        dsl.deleteFrom(LECTURE_CHAPTER)
                .where(LECTURE_CHAPTER.ID.eq(chapterId))
                .execute();
    }

    // === Detail CRUD 메서드들 ===

    // 디테일 생성
    public UUID insertDetail(UUID chapterId, String title, String type, Integer detailOrder) {
        UUID detailId = UUID.randomUUID();

        dsl.insertInto(LECTURE_DETAIL)
                .set(LECTURE_DETAIL.ID, detailId)
                .set(LECTURE_DETAIL.LECTURE_CHAPTER_ID, chapterId)
                .set(LECTURE_DETAIL.TITLE, title)
                .set(LECTURE_DETAIL.TYPE, type)
                .set(LECTURE_DETAIL.DETAIL_ORDER, detailOrder)
                .execute();

        return detailId;
    }

    // 디테일 조회
    public com.example.toygry.one_you.lecture.dto.DetailResponse findDetailById(UUID detailId) {
        return dsl.select(
                        LECTURE_DETAIL.ID,
                        LECTURE_DETAIL.LECTURE_CHAPTER_ID,
                        LECTURE_DETAIL.TITLE,
                        LECTURE_DETAIL.TYPE,
                        LECTURE_DETAIL.DETAIL_ORDER,
                        LECTURE_DETAIL.CREATED_AT
                )
                .from(LECTURE_DETAIL)
                .where(LECTURE_DETAIL.ID.eq(detailId))
                .fetchOne(record -> new com.example.toygry.one_you.lecture.dto.DetailResponse(
                        record.get(LECTURE_DETAIL.ID),
                        record.get(LECTURE_DETAIL.LECTURE_CHAPTER_ID),
                        record.get(LECTURE_DETAIL.TITLE),
                        record.get(LECTURE_DETAIL.TYPE),
                        record.get(LECTURE_DETAIL.DETAIL_ORDER),
                        record.get(LECTURE_DETAIL.CREATED_AT)
                ));
    }

    // 디테일 수정
    public void updateDetail(UUID detailId, String title, String type, Integer detailOrder) {
        dsl.update(LECTURE_DETAIL)
                .set(LECTURE_DETAIL.TITLE, title)
                .set(LECTURE_DETAIL.TYPE, type)
                .set(LECTURE_DETAIL.DETAIL_ORDER, detailOrder)
                .where(LECTURE_DETAIL.ID.eq(detailId))
                .execute();
    }

    // 디테일 삭제
    public void deleteDetail(UUID detailId) {
        dsl.deleteFrom(LECTURE_DETAIL)
                .where(LECTURE_DETAIL.ID.eq(detailId))
                .execute();
    }

    // === 순서 변경 관련 메서드들 ===

    // 챕터 순서 일괄 업데이트
    public void updateChapterOrders(List<com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo> chapters) {
        for (com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo chapter : chapters) {
            dsl.update(LECTURE_CHAPTER)
                    .set(LECTURE_CHAPTER.CHAPTER_ORDER, chapter.chapterOrder())
                    .where(LECTURE_CHAPTER.ID.eq(chapter.chapterId()))
                    .execute();
        }
    }

    // 강의 상세 순서 및 소속 챕터 일괄 업데이트
    public void updateDetailOrders(List<com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo> chapters) {
        for (com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo chapter : chapters) {
            for (com.example.toygry.one_you.lecture.dto.ReorderRequest.DetailOrderInfo detail : chapter.details()) {
                dsl.update(LECTURE_DETAIL)
                        .set(LECTURE_DETAIL.LECTURE_CHAPTER_ID, chapter.chapterId())
                        .set(LECTURE_DETAIL.DETAIL_ORDER, detail.detailOrder())
                        .where(LECTURE_DETAIL.ID.eq(detail.detailId()))
                        .execute();
            }
        }
    }

    // 특정 강의의 모든 챕터와 상세 정보 존재 여부 확인
    public boolean validateChaptersAndDetails(UUID lectureId, List<com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo> chapters) {
        // 해당 강의의 모든 챕터 ID 조회
        List<UUID> existingChapterIds = dsl.select(LECTURE_CHAPTER.ID)
                .from(LECTURE_CHAPTER)
                .where(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId))
                .fetchInto(UUID.class);

        // 해당 강의의 모든 상세 ID 조회
        List<UUID> existingDetailIds = dsl.select(LECTURE_DETAIL.ID)
                .from(LECTURE_DETAIL)
                .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
                .where(LECTURE_CHAPTER.LECTURE_ID.eq(lectureId))
                .fetchInto(UUID.class);

        // 요청된 챕터들이 모두 해당 강의에 속하는지 확인
        for (com.example.toygry.one_you.lecture.dto.ReorderRequest.ChapterOrderInfo chapter : chapters) {
            if (!existingChapterIds.contains(chapter.chapterId())) {
                return false;
            }

            // 요청된 상세들이 모두 해당 강의에 속하는지 확인
            for (com.example.toygry.one_you.lecture.dto.ReorderRequest.DetailOrderInfo detail : chapter.details()) {
                if (!existingDetailIds.contains(detail.detailId())) {
                    return false;
                }
            }
        }

        return true;
    }

    public LectureContentRecord fetchLectureContent(UUID lectureDetailId) {
        return dsl.selectFrom(LECTURE_CONTENT)
                .where(LECTURE_CONTENT.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .fetchOneInto(LectureContentRecord.class);
    }

    public VideoRecord fetchVideoByContentId(UUID lectureDetailId) {
        return dsl.select(VIDEO.fields())
                .from(LECTURE_CONTENT)
                .join(VIDEO).on(LECTURE_CONTENT.VIDEO_ID.eq(VIDEO.ID))
                .where(LECTURE_CONTENT.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .fetchOneInto(VideoRecord.class);
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

    public void insertOrUpdateStudentReviewSubmission(UUID userId, UUID lectureDetailId, String reviewUrl) {
        // 기존 제출 내역이 있는지 확인
        StudentReviewSubmissionRecord existing = dsl.selectFrom(STUDENT_REVIEW_SUBMISSION)
                .where(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(userId)
                        .and(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                .fetchOne();

        if (existing != null) {
            // 업데이트
            dsl.update(STUDENT_REVIEW_SUBMISSION)
                    .set(STUDENT_REVIEW_SUBMISSION.REVIEW_URL, reviewUrl)
                    .set(STUDENT_REVIEW_SUBMISSION.UPDATED_AT, java.time.LocalDateTime.now())
                    .where(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(userId)
                            .and(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                    .execute();
        } else {
            // 새로 삽입
            dsl.insertInto(STUDENT_REVIEW_SUBMISSION)
                    .set(STUDENT_REVIEW_SUBMISSION.ID, UUID.randomUUID())
                    .set(STUDENT_REVIEW_SUBMISSION.USER_ID, userId)
                    .set(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID, lectureDetailId)
                    .set(STUDENT_REVIEW_SUBMISSION.REVIEW_URL, reviewUrl)
                    .set(STUDENT_REVIEW_SUBMISSION.CREATED_AT, java.time.LocalDateTime.now())
                    .set(STUDENT_REVIEW_SUBMISSION.UPDATED_AT, java.time.LocalDateTime.now())
                    .execute();
        }
    }

    // 특정 퀴즈 옵션이 정답인지 확인
    public boolean isCorrectOption(UUID optionId) {
        return dsl.select(LECTURE_QUIZ_OPTION.IS_CORRECT)
                .from(LECTURE_QUIZ_OPTION)
                .where(LECTURE_QUIZ_OPTION.ID.eq(optionId))
                .fetchOne(LECTURE_QUIZ_OPTION.IS_CORRECT, Boolean.class);
    }

    // 퀴즈의 정답 옵션 조회
    public LectureQuizOptionRecord fetchCorrectOption(UUID quizId) {
        return dsl.selectFrom(LECTURE_QUIZ_OPTION)
                .where(LECTURE_QUIZ_OPTION.LECTURE_QUIZ_ID.eq(quizId)
                        .and(LECTURE_QUIZ_OPTION.IS_CORRECT.eq(true)))
                .fetchOneInto(LectureQuizOptionRecord.class);
    }

    // 선택한 옵션 정보 조회
    public LectureQuizOptionRecord fetchQuizOption(UUID optionId) {
        return dsl.selectFrom(LECTURE_QUIZ_OPTION)
                .where(LECTURE_QUIZ_OPTION.ID.eq(optionId))
                .fetchOneInto(LectureQuizOptionRecord.class);
    }

    // 학생 강의 진도 업데이트 (upsert)
    public void insertOrUpdateLectureProgress(UUID userId, UUID lectureDetailId, boolean isCompleted) {
        // 기존 진도 기록이 있는지 확인
        StudentLectureProgressRecord existing = dsl.selectFrom(STUDENT_LECTURE_PROGRESS)
                .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId)
                        .and(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                .fetchOne();

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (existing != null) {
            // 업데이트
            dsl.update(STUDENT_LECTURE_PROGRESS)
                    .set(STUDENT_LECTURE_PROGRESS.IS_COMPLETED, isCompleted)
                    .set(STUDENT_LECTURE_PROGRESS.COMPLETED_AT, isCompleted ? now : null)
                    .set(STUDENT_LECTURE_PROGRESS.UPDATED_AT, now)
                    .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId)
                            .and(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(lectureDetailId)))
                    .execute();
        } else {
            // 새로 삽입
            dsl.insertInto(STUDENT_LECTURE_PROGRESS)
                    .set(STUDENT_LECTURE_PROGRESS.ID, UUID.randomUUID())
                    .set(STUDENT_LECTURE_PROGRESS.USER_ID, userId)
                    .set(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID, lectureDetailId)
                    .set(STUDENT_LECTURE_PROGRESS.IS_COMPLETED, isCompleted)
                    .set(STUDENT_LECTURE_PROGRESS.COMPLETED_AT, isCompleted ? now : null)
                    .set(STUDENT_LECTURE_PROGRESS.CREATED_AT, now)
                    .set(STUDENT_LECTURE_PROGRESS.UPDATED_AT, now)
                    .execute();
        }
    }

    // =========================== 비디오 관련 메서드 ===========================

    /**
     * video 테이블에 새로운 비디오 레코드를 생성합니다.
     * @param vimeoVideoId Vimeo 비디오 ID
     * @return 생성된 video 레코드의 UUID
     */
    public UUID createVideoRecord(String vimeoVideoId) {
        UUID videoId = UUID.randomUUID();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 임시 사용자 ID (실제로는 업로드한 사용자 ID를 받아야 함)
        UUID uploadedBy = UUID.fromString("5d726309-0785-47e2-8f81-257d74401543");

        dsl.insertInto(VIDEO)
                .set(VIDEO.ID, videoId)
                .set(VIDEO.TITLE, "업로드된 비디오") // 기본값, 나중에 업데이트 가능
                .set(VIDEO.PLATFORM, "VIMEO")
                .set(VIDEO.EXTERNAL_VIDEO_ID, vimeoVideoId)
                .set(VIDEO.EMBED_URL, "https://player.vimeo.com/video/" + vimeoVideoId)
                .set(VIDEO.UPLOAD_STATUS, "READY")
                .set(VIDEO.IS_LIVE, false)
                .set(VIDEO.UPLOADED_BY, uploadedBy)
                .set(VIDEO.CREATED_AT, now)
                .set(VIDEO.UPDATED_AT, now)
                .execute();

        return videoId;
    }

    /**
     * lecture_content 테이블의 video_id를 업데이트합니다.
     * @param lectureContentId 강의 콘텐츠 ID (UUID 타입)
     * @param videoId 비디오 UUID
     */
    public void updateLectureContentVideoId(UUID lectureContentId, UUID videoId) {
        dsl.update(LECTURE_CONTENT)
                .set(LECTURE_CONTENT.VIDEO_ID, videoId)
                .set(LECTURE_CONTENT.UPDATED_AT, java.time.LocalDateTime.now())
                .where(LECTURE_CONTENT.ID.eq(lectureContentId))
                .execute();
    }

    /**
     * 강의 콘텐츠의 Vimeo 비디오 ID를 조회합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     * @return Vimeo 비디오 ID (없으면 null)
     */
    public String getVimeoVideoId(UUID lectureContentId) {
        return dsl.select(VIDEO.EXTERNAL_VIDEO_ID)
                .from(LECTURE_CONTENT)
                .join(VIDEO).on(LECTURE_CONTENT.VIDEO_ID.eq(VIDEO.ID))
                .where(LECTURE_CONTENT.ID.eq(lectureContentId)
                        .and(VIDEO.PLATFORM.eq("VIMEO")))
                .fetchOne(VIDEO.EXTERNAL_VIDEO_ID);
    }

    /**
     * lecture_content에서 video_id 연결을 제거합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     */
    public void removeLectureContentVideoId(UUID lectureContentId) {
        dsl.update(LECTURE_CONTENT)
                .setNull(LECTURE_CONTENT.VIDEO_ID)
                .set(LECTURE_CONTENT.UPDATED_AT, java.time.LocalDateTime.now())
                .where(LECTURE_CONTENT.ID.eq(lectureContentId))
                .execute();
    }
}
