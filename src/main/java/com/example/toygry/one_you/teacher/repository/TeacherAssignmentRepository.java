package com.example.toygry.one_you.teacher.repository;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.teacher.dto.PendingFeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

/**
 * 선생님용 과제 관리 Repository
 */
@Repository
@RequiredArgsConstructor
public class TeacherAssignmentRepository {

    private final DSLContext dsl;

    /**
     * 특정 선생님의 피드백 대기 과제 목록 조회
     * 
     * @param teacherId 선생님 ID (users 테이블의 id, role='TEACHER')
     * @return 피드백 대기 과제 목록
     */
    public List<PendingFeedbackResponse.PendingAssignment> findPendingFeedbackByTeacher(UUID teacherId) {
        return dsl
            .select(
                STUDENT_REVIEW_SUBMISSION.ID.as("submission_id"),
                STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID,
                LECTURE_CHAPTER.TITLE.as("chapter_title"),
                LECTURE_DETAIL.TITLE.as("detail_title"),
                LECTURE_DETAIL.TYPE.as("detail_type"),
                STUDENT_REVIEW_SUBMISSION.USER_ID.as("student_id"),
                USERS.NAME.as("student_name"),
                STUDENT_REVIEW_SUBMISSION.REVIEW_URL,
                STUDENT_REVIEW_SUBMISSION.CREATED_AT.as("submitted_at")
            )
            .from(STUDENT_REVIEW_SUBMISSION)
            .join(LECTURE_DETAIL).on(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(LECTURE_DETAIL.ID))
            .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
            .join(LECTURE).on(LECTURE_CHAPTER.LECTURE_ID.eq(LECTURE.ID))
            .join(USERS).on(STUDENT_REVIEW_SUBMISSION.USER_ID.eq(USERS.ID))
            .where(STUDENT_REVIEW_SUBMISSION.TEACHER_FEEDBACK.isNull()) // 피드백이 아직 없는 것만
            .and(LECTURE.TEACHER_ID.eq(teacherId)) // 해당 선생님의 강의만
            .orderBy(STUDENT_REVIEW_SUBMISSION.CREATED_AT.desc()) // 최신 제출 순
            .fetch()
            .stream()
            .map(record -> new PendingFeedbackResponse.PendingAssignment(
                record.get("submission_id", UUID.class),
                record.get(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID),
                record.get("chapter_title", String.class),
                record.get("detail_title", String.class),
                record.get("detail_type", String.class),
                record.get("student_id", UUID.class),
                record.get("student_name", String.class),
                record.get(STUDENT_REVIEW_SUBMISSION.REVIEW_URL),
                record.get("submitted_at", LocalDateTime.class)
            ))
            .toList();
    }

    /**
     * lecture_detail_id로 lecture_id 조회
     */
    public UUID getLectureIdByDetailId(UUID lectureDetailId) {
        return dsl
            .select(LECTURE.ID)
            .from(LECTURE_DETAIL)
            .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
            .join(LECTURE).on(LECTURE_CHAPTER.LECTURE_ID.eq(LECTURE.ID))
            .where(LECTURE_DETAIL.ID.eq(lectureDetailId))
            .fetchOne(LECTURE.ID);
    }

    /**
     * 강의 정보 조회 (제목, 카테고리)
     */
    public LectureInfo getLectureInfo(UUID lectureId) {
        Record record = dsl
            .select(LECTURE.TITLE, LECTURE.CATEGORY)
            .from(LECTURE)
            .where(LECTURE.ID.eq(lectureId))
            .fetchOne();

        return new LectureInfo(
            record.get(LECTURE.TITLE),
            record.get(LECTURE.CATEGORY)
        );
    }

    /**
     * 제출물 정보 조회
     */
    public SubmissionInfo getSubmissionInfo(UUID submissionId) {
        Record record = dsl
            .select(STUDENT_REVIEW_SUBMISSION.USER_ID, STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID)
            .from(STUDENT_REVIEW_SUBMISSION)
            .where(STUDENT_REVIEW_SUBMISSION.ID.eq(submissionId))
            .fetchOne();
            
        if (record == null) {
            throw new BaseException(OneYouStatusCode.SUBMISSION_NOT_FOUND);
        }
        
        return new SubmissionInfo(
            record.get(STUDENT_REVIEW_SUBMISSION.USER_ID),
            record.get(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID)
        );
    }

    /**
     * 선생님의 제출물 접근 권한 확인
     */
    public void validateTeacherAccess(UUID submissionId, UUID teacherId) {
        boolean hasAccess = dsl
            .selectCount()
            .from(STUDENT_REVIEW_SUBMISSION)
            .join(LECTURE_DETAIL).on(STUDENT_REVIEW_SUBMISSION.LECTURE_DETAIL_ID.eq(LECTURE_DETAIL.ID))
            .join(LECTURE_CHAPTER).on(LECTURE_DETAIL.LECTURE_CHAPTER_ID.eq(LECTURE_CHAPTER.ID))
            .join(LECTURE).on(LECTURE_CHAPTER.LECTURE_ID.eq(LECTURE.ID))
            .where(STUDENT_REVIEW_SUBMISSION.ID.eq(submissionId))
            .and(LECTURE.TEACHER_ID.eq(teacherId))
            .fetchOne(0, int.class) > 0;
            
        if (!hasAccess) {
            throw new BaseException(OneYouStatusCode.ACCESS_DENIED);
        }
    }

    /**
     * 피드백 업데이트
     */
    public void updateFeedback(UUID submissionId, String feedback) {
        int updated = dsl
            .update(STUDENT_REVIEW_SUBMISSION)
            .set(STUDENT_REVIEW_SUBMISSION.TEACHER_FEEDBACK, feedback)
            .set(STUDENT_REVIEW_SUBMISSION.UPDATED_AT, LocalDateTime.now())
            .where(STUDENT_REVIEW_SUBMISSION.ID.eq(submissionId))
            .execute();
            
        if (updated == 0) {
            throw new BaseException(OneYouStatusCode.SUBMISSION_NOT_FOUND);
        }
    }

    /**
     * 학생 진도 상태 업데이트
     */
    public void updateStudentProgress(UUID userId, UUID lectureDetailId, boolean isCompleted) {
        // 이미 진도 기록이 있는지 확인
        boolean exists = dsl
            .selectCount()
            .from(STUDENT_LECTURE_PROGRESS)
            .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId))
            .and(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(lectureDetailId))
            .fetchOne(0, int.class) > 0;
            
        if (exists) {
            // 업데이트
            dsl.update(STUDENT_LECTURE_PROGRESS)
                .set(STUDENT_LECTURE_PROGRESS.IS_COMPLETED, isCompleted)
                .set(STUDENT_LECTURE_PROGRESS.COMPLETED_AT, isCompleted ? LocalDateTime.now() : null)
                .set(STUDENT_LECTURE_PROGRESS.UPDATED_AT, LocalDateTime.now())
                .where(STUDENT_LECTURE_PROGRESS.USER_ID.eq(userId))
                .and(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID.eq(lectureDetailId))
                .execute();
        } else {
            // 삽입
            dsl.insertInto(STUDENT_LECTURE_PROGRESS)
                .set(STUDENT_LECTURE_PROGRESS.ID, UUID.randomUUID())
                .set(STUDENT_LECTURE_PROGRESS.USER_ID, userId)
                .set(STUDENT_LECTURE_PROGRESS.LECTURE_DETAIL_ID, lectureDetailId)
                .set(STUDENT_LECTURE_PROGRESS.IS_COMPLETED, isCompleted)
                .set(STUDENT_LECTURE_PROGRESS.COMPLETED_AT, isCompleted ? LocalDateTime.now() : null)
                .set(STUDENT_LECTURE_PROGRESS.CREATED_AT, LocalDateTime.now())
                .set(STUDENT_LECTURE_PROGRESS.UPDATED_AT, LocalDateTime.now())
                .execute();
        }
    }

    /**
     * 강의 정보 Record
     */
    public record LectureInfo(String title, String category) {}
    
    /**
     * 제출물 정보 Record
     */
    public record SubmissionInfo(UUID userId, UUID lectureDetailId) {}
}