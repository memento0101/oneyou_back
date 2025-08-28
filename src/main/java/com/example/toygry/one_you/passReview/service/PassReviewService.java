package com.example.toygry.one_you.passReview.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.jooq.generated.tables.records.PassReviewRecord;
import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import com.example.toygry.one_you.passReview.dto.PassReviewUpdateRequest;
import com.example.toygry.one_you.passReview.repository.PassReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassReviewService {

    private final PassReviewRepository passReviewRepository;

    public List<PassReviewResponse> getAllPassReviews() {
        return passReviewRepository.findAll();
    }

    public void createPassReview(UserTokenPrincipal userTokenPrincipal, PassReviewRequest passReviewRequest) {
        passReviewRepository.save(userTokenPrincipal.getUuid(), passReviewRequest);
    }

    public PassReviewResponse updatePassReview(UUID userId, String passReviewId, PassReviewUpdateRequest request) {
        UUID reviewUuid = UUID.fromString(passReviewId);

        // 본인이 작성한 후기인지 확인하기
        passReviewRepository.findByIdAndUserId(reviewUuid, userId)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.REVIEW_NOT_FOUND));

        // 본인이 작성한 후기가 맞다면 수정하기 ~
        PassReviewRecord updateRecord = passReviewRepository.updateReview(
                reviewUuid,
                request.passYear(),
                request.targetUniversity(),
                request.title(),
                request.content()
        );

        return PassReviewResponse.fromRecord(updateRecord);
    }
}
