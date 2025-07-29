package com.example.toygry.one_you.review.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.jooq.generated.tables.Review;
import com.example.toygry.one_you.jooq.generated.tables.records.ReviewRecord;
import com.example.toygry.one_you.review.dto.ReviewRequest;
import com.example.toygry.one_you.review.dto.ReviewResponse;
import com.example.toygry.one_you.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAllReviews();
    }

    public List<ReviewResponse> getLectureReviews(UUID lectureId) {
        if (lectureId == null) {
            throw new BaseException(OneYouStatusCode.BadRequest, "강의 ID는 필수입니다.");
        }
        return reviewRepository.findByLectureId(lectureId);
    }

    public void saveReview(UserTokenPrincipal userTokenPrincipal, ReviewRequest request) {
        if (request.lectureId() == null || request.contents() == null || request.contents().isEmpty() || userTokenPrincipal == null) {
            throw new BaseException(OneYouStatusCode.BadRequest);
        }
        reviewRepository.saveReview(userTokenPrincipal.getUuid(), request);
    }

    public void updateReview(UUID reviewId, UserTokenPrincipal userTokenPrincipal, ReviewRequest request) {
        if (reviewId == null || request.contents() == null || request.contents().isEmpty() || userTokenPrincipal == null) {
            throw new BaseException(OneYouStatusCode.BadRequest);
        }

        ReviewRecord review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.ReviewNotFount));

        if (!review.getUserId().equals(userTokenPrincipal.getUuid())) {
            throw new BaseException(OneYouStatusCode.UserForbidden);
        }

        review.setContents(request.contents());
        review.setScore(request.score());
        reviewRepository.updateReview(review);
    }

    public void deleteReview(UUID reviewId, UserTokenPrincipal userTokenPrincipal) {
        if (reviewId == null || userTokenPrincipal == null) {
            throw new BaseException(OneYouStatusCode.BadRequest);
        }

        ReviewRecord review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.ReviewNotFount));

        if (!review.getUserId().equals(userTokenPrincipal.getUuid())) {
            throw new BaseException(OneYouStatusCode.UserForbidden, "본인 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.deleteReviewById(reviewId);
    }
}
