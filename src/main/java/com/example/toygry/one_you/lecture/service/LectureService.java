package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.jooq.generated.tables.records.*;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.repository.LectureRepository;
import com.example.toygry.one_you.lecture.repository.StudentLectureRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final StudentLectureRepository studentLectureRepository;

    public LectureDetailResponse getLectureDetail(UUID userId, UUID lectureId) {
        LocalDateTime expireDate = studentLectureRepository.findLectureExpireDate(userId, lectureId);
        if (expireDate == null || expireDate.isBefore(LocalDateTime.now())) {
            throw new BaseException(OneYouStatusCode.LectureForbidden, "수강 가능한 강의가 아닙니다.");
        }

        int total = lectureRepository.countTotalLectureDetails(lectureId);
        int completed = lectureRepository.countCompletedLectureDetails(userId, lectureId);
        double progress = total > 0 ? (completed * 100.0) / total : 0.0;

        List<LectureDetailWithProgressResponse> rawList = lectureRepository.findLectureChaptersWithProgress(userId, lectureId);

        // 그룹핑
        Map<UUID, LectureDetailResponse.Chapter> chapterMap = new LinkedHashMap<>();
        for (LectureDetailWithProgressResponse row : rawList) {
            chapterMap.computeIfAbsent(row.chapterId(), id -> new LectureDetailResponse.Chapter(id, row.chapterTitle(), new ArrayList<>()))
                    .details().add(new LectureDetailResponse.Detail(row.detailId(), row.detailTitle(), row.type(), Boolean.TRUE.equals(row.isCompleted())));
        }

        return new LectureDetailResponse(
                lectureId,
                "물리학 개론", // 필요시 추가조회
                (int) ChronoUnit.DAYS.between(LocalDateTime.now(), expireDate),
                Math.round(progress * 10.0) / 10.0,
                new ArrayList<>(chapterMap.values())
        );
    }

    public LectureContentsResponse getLectureContentsDetail(UUID userId,  LectureDetailRequest request) {

        LectureDetailRecord detail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (detail == null) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "해당 강의를 찾을 수 없습니다.");
        }


        if ("QUIZ".equalsIgnoreCase(request.type())) {
            List<LectureQuizRecord> quizList = lectureRepository.fetchLectureQuizzes(request.lectureDetailId());
            if (quizList.isEmpty()) {
                throw new BaseException(OneYouStatusCode.LectureNotFound, "등록된 퀴즈가 없습니다.");
            }

            // 각 퀴즈의 옵션도 함께 조회
            List<LectureContentsResponse.QuizWithOptions> quizResponses = quizList.stream().map(quiz -> {
                List<LectureQuizOptionRecord> options = lectureRepository.fetchQuizOptions(quiz.getId());
                return LectureContentsResponse.QuizWithOptions.from(quiz, options);
            }).toList();

            return LectureContentsResponse.ofQuiz(detail, quizResponses);
        }
        LectureContentRecord content = lectureRepository.fetchLectureContent(request.lectureDetailId());
        if (content == null) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "강의 내용이 없습니다.");
        }

        if ("VIDEO".equalsIgnoreCase(request.type())) {
            // 비디오 강의면 학생 과제 조회
            StudentReviewSubmissionRecord submission = lectureRepository.fetchStudentReviewSubmission(request.lectureDetailId(), userId);
            return LectureContentsResponse.ofVideo(detail, content, submission);
        }

        if ("LIVE".equalsIgnoreCase(request.type())) {
            return LectureContentsResponse.ofLive(detail, content);
        }

        throw new BaseException(OneYouStatusCode.BadRequest, "알 수 없는 강의 유형입니다.");


    }
}
