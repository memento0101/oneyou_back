package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.lecture.dto.LectureResponse;
import com.example.toygry.one_you.lecture.dto.LectureUserResponse;
import com.example.toygry.one_you.lecture.dto.UserLectureResponse;
import com.example.toygry.one_you.lecture.repository.LectureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    public List<LectureResponse> getAllLecture() {
         return lectureRepository.findAllLectures();
    }

    public List<UserLectureResponse> getUserLectures(UUID userId) {
        return lectureRepository.findUserLectureByUserId(userId);
    }

    public LectureUserResponse getLectureWithUsers(UUID lectureId) {
        // 1. 강의 정보 조회
        LectureResponse lectureInfo = lectureRepository.findLectureById(lectureId);

        // 2. 해당 강의를 수강하는 사용자 정보 조회
        List<LectureUserResponse.UserLectureInfo> users = lectureRepository.getUsersByLectureId(lectureId);

        // 3. 사용자 리스트를 강의 정보에 설정하여 반환
        return LectureUserResponse.builder()
                .lecture(lectureInfo)
                .users(users)
                .build();
    }
}
