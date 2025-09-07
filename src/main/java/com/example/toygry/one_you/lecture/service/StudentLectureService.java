package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.lecture.dto.StudentLectureResponse;
import com.example.toygry.one_you.lecture.dto.SubmissionCheckResponse;
import com.example.toygry.one_you.lecture.dto.TeacherLectureGroupResponse;
import com.example.toygry.one_you.lecture.repository.StudentLectureRepository;
import com.example.toygry.one_you.lecture.repository.StudentReviewSubmissionRepository;
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
    private final StudentReviewSubmissionRepository studentReviewSubmissionRepository;

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

    public SubmissionCheckResponse checkSubmission(UUID userId, UUID lectureDetailId) {
        var submission = studentReviewSubmissionRepository.findSubmissionByUserAndLectureDetail(userId, lectureDetailId);
        
        if (submission != null && submission.getReviewUrl() != null) {
            return new SubmissionCheckResponse(
                    true,
                    submission.getReviewUrl(),
                    submission.getCreatedAt()
            );
        }
        
        return new SubmissionCheckResponse(false, null, null);
    }

}
