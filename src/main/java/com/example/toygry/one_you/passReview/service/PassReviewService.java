package com.example.toygry.one_you.passReview.service;

import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import com.example.toygry.one_you.passReview.repository.PassReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
