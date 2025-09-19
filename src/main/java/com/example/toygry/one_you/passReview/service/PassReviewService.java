package com.example.toygry.one_you.passReview.service;

import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.passReview.dto.PassReviewRequest;
import com.example.toygry.one_you.passReview.dto.PassReviewResponse;
import com.example.toygry.one_you.passReview.repository.PassReviewRepository;
import com.example.toygry.one_you.university.service.UniversityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PassReviewService {

    private final PassReviewRepository passReviewRepository;
    private final UniversityService universityService;

    public List<PassReviewResponse> getAllPassReviews() {
        return passReviewRepository.findAll();
    }

    public List<PassReviewResponse> getPassReviewsByUniversityType(String universityType) {
        return passReviewRepository.findByUniversityType(universityType);
    }

    public List<PassReviewResponse> getPassReviewsByUniversityId(UUID universityId) {
        // 대학 존재 여부 확인
        universityService.getUniversityById(universityId);
        return passReviewRepository.findByUniversityId(universityId);
    }

    public List<PassReviewResponse> getPassReviewsByPassYear(Integer passYear) {
        return passReviewRepository.findByPassYear(passYear);
    }

    public void createPassReview(UserTokenPrincipal userTokenPrincipal, PassReviewRequest passReviewRequest) {
        // 대학 존재 여부 확인
        universityService.getUniversityById(passReviewRequest.universityId());

        // 합격 후기 저장
        passReviewRepository.save(userTokenPrincipal.getUuid(), passReviewRequest);
    }
}
