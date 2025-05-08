package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.lecture.dto.LectureResponse;
import com.example.toygry.one_you.lecture.repository.LectureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    public List<LectureResponse> getAllLecture() {
         return lectureRepository.findAllLectures();
    }
}
