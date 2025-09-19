package com.example.toygry.one_you.university.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.university.dto.UniversityRequest;
import com.example.toygry.one_you.university.dto.UniversityResponse;
import com.example.toygry.one_you.university.repository.UniversityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    public List<UniversityResponse> getAllUniversities() {
        return universityRepository.findAll();
    }

    public List<UniversityResponse> getUniversitiesByType(String universityType) {
        return universityRepository.findByType(universityType);
    }

    public UniversityResponse getUniversityById(UUID id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.UNIVERSITY_NOT_FOUND));
    }

    public UUID createUniversity(UniversityRequest request) {
        validateUniversityType(request.universityType());
        return universityRepository.save(request);
    }

    public void updateUniversity(UUID id, UniversityRequest request) {
        // 존재 여부 확인
        getUniversityById(id);
        validateUniversityType(request.universityType());
        universityRepository.update(id, request);
    }

    public void deleteUniversity(UUID id) {
        // 존재 여부 확인
        getUniversityById(id);
        universityRepository.deleteById(id);
    }

    private void validateUniversityType(String universityType) {
        if (!List.of("NATIONAL", "PRIVATE", "MEDICAL").contains(universityType)) {
            throw new BaseException(OneYouStatusCode.UNIVERSITY_TYPE_INVALID);
        }
    }
}