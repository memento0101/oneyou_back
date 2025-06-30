package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.lecture.dto.StudentLectureResponse;
import com.example.toygry.one_you.lecture.dto.TeacherLectureGroupResponse;
import com.example.toygry.one_you.lecture.repository.StudentLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentLectureService {

    private final StudentLectureRepository studentLectureRepository;

    public List<TeacherLectureGroupResponse> getActiveLecturesByUser(UUID userId) {
        List<StudentLectureResponse> rawList = studentLectureRepository.findActiveLecturesByUser(userId);

        return rawList.stream()
                .collect(Collectors.groupingBy(
                        StudentLectureResponse::teacherId,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), lectures -> new TeacherLectureGroupResponse(
                                lectures.getFirst().teacherId(),
                                lectures.getFirst().teacherName(),
                                lectures.stream().map(l -> new TeacherLectureGroupResponse.LectureItem(
                                        l.lectureId(), l.lectureTitle(), l.thumbnailUrl(), l.expireDate()
                                )).toList()
                        ))
                ))
                .values()
                .stream()
                .toList();
    }

}
