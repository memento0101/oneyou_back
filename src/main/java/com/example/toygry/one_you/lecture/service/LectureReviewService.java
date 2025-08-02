package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.jooq.generated.tables.records.LectureReviewRecord;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.repository.LectureReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureReviewService {

    private final LectureReviewRepository lectureReviewRepository;

    @Transactional
    public LectureReviewResponse createReview(UUID userId, LectureReviewRequest request) {
        UUID lectureId = UUID.fromString(request.lectureId());

        // 이미 후기를 작성했는지 확인
        if (lectureReviewRepository.findByUserIdAndLectureId(userId, lectureId).isPresent()) {
            throw new BaseException(OneYouStatusCode.REVIEW_ALREADY_EXISTS);
        }

        // 강의 수강 완료 여부 확인 (50% 이상 완료)
        if (!lectureReviewRepository.hasUserCompletedLecture(userId, lectureId)) {
            throw new BaseException(OneYouStatusCode.REVIEW_NOT_ELIGIBLE);
        }

        LectureReviewRecord record = lectureReviewRepository.createReview(
                lectureId,
                userId,
                request.rating(),
                request.title(),
                request.content(),
                request.isAnonymous()
        );

        return mapToResponse(record, getUserName(userId, request.isAnonymous()));
    }

    public LectureReviewResponse getMyReview(UUID userId, String lectureId) {
        UUID lectureUuid = UUID.fromString(lectureId);

        LectureReviewRecord record = lectureReviewRepository.findByUserIdAndLectureId(userId, lectureUuid)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.REVIEW_NOT_FOUND));

        return mapToResponse(record, getUserName(userId, record.getIsAnonymous()));
    }

    public LectureReviewListResponse getReviewsByLecture(String lectureId, int page, int size) {
        UUID lectureUuid = UUID.fromString(lectureId);
        int offset = page * size;

        List<LectureReviewResponse> reviews = lectureReviewRepository.findReviewsByLectureId(lectureUuid, offset, size);
        long totalCount = lectureReviewRepository.countReviewsByLectureId(lectureUuid);
        Double averageRating = lectureReviewRepository.getAverageRatingByLectureId(lectureUuid);
        LectureReviewListResponse.RatingStatistics statistics = lectureReviewRepository.getRatingStatisticsByLectureId(lectureUuid);

        return new LectureReviewListResponse(
                totalCount,
                averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0,
                statistics,
                reviews
        );
    }

    @Transactional
    public LectureReviewResponse updateReview(UUID userId, String reviewId, LectureReviewUpdateRequest request) {
        UUID reviewUuid = UUID.fromString(reviewId);

        // 본인이 작성한 후기인지 확인
        LectureReviewRecord existingRecord = lectureReviewRepository.findByIdAndUserId(reviewUuid, userId)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.REVIEW_NOT_FOUND));

        LectureReviewRecord updatedRecord = lectureReviewRepository.updateReview(
                reviewUuid,
                request.rating(),
                request.title(),
                request.content(),
                request.isAnonymous()
        );

        return mapToResponse(updatedRecord, getUserName(userId, request.isAnonymous()));
    }

    @Transactional
    public void deleteReview(UUID userId, String reviewId) {
        UUID reviewUuid = UUID.fromString(reviewId);

        if (!lectureReviewRepository.deleteReview(reviewUuid, userId)) {
            throw new BaseException(OneYouStatusCode.REVIEW_NOT_FOUND);
        }
    }

    private LectureReviewResponse mapToResponse(LectureReviewRecord record, String userName) {
        return new LectureReviewResponse(
                record.getId().toString(),
                record.getLectureId().toString(),
                record.getUserId().toString(),
                userName,
                record.getRating(),
                record.getTitle(),
                record.getContent(),
                record.getIsAnonymous(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    private String getUserName(UUID userId, Boolean isAnonymous) {
        if (isAnonymous != null && isAnonymous) {
            return "익명";
        }
        // 실제 구현에서는 UsersRepository에서 사용자명을 조회해야 함
        // 현재는 간단히 userId를 반환
        return "사용자" + userId.toString().substring(0, 8);
    }
}