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
            chapterMap
                    .computeIfAbsent(row.chapterId(),
                            id -> new LectureDetailResponse.Chapter(id, row.chapterTitle(), new ArrayList<>()))
                    .details().add(new LectureDetailResponse.Detail(row.detailId(), row.detailTitle(), row.type(),
                            Boolean.TRUE.equals(row.isCompleted())));
        }

        return new LectureDetailResponse(
                lectureId,
                "물리학 개론", // 필요시 추가조회
                (int) ChronoUnit.DAYS.between(LocalDateTime.now(), expireDate),
                Math.round(progress * 10.0) / 10.0,
                new ArrayList<>(chapterMap.values()));
    }

    public LectureContentsResponse getLectureContentsDetail(UUID userId, LectureDetailRequest request) {

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
            StudentReviewSubmissionRecord submission = lectureRepository
                    .fetchStudentReviewSubmission(request.lectureDetailId(), userId);
            return LectureContentsResponse.ofVideo(detail, content, submission);
        }

        if ("LIVE".equalsIgnoreCase(request.type())) {
            return LectureContentsResponse.ofLive(detail, content);
        }

        throw new BaseException(OneYouStatusCode.BadRequest, "알 수 없는 강의 유형입니다.");

    }

    public void submitStudentLink(UUID userId, StudentLinkSubmissionRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "해당 강의를 찾을 수 없습니다.");
        }

        // 학생 리뷰 제출 정보 저장 또는 업데이트
        lectureRepository.insertOrUpdateStudentReviewSubmission(userId, request.lectureDetailId(), request.reviewUrl());

    }

    public QuizGradingResponse gradeQuiz(UUID userId, QuizSubmissionRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "해당 강의를 찾을 수 없습니다.");
        }

        // 해당 강의의 모든 퀴즈 조회
        List<LectureQuizRecord> quizzes = lectureRepository.fetchLectureQuizzes(request.lectureDetailId());
        if (quizzes.isEmpty()) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "등록된 퀴즈가 없습니다.");
        }

        List<QuizGradingResponse.QuizResult> results = new ArrayList<>();
        int correctAnswers = 0;

        // 각 답안을 채점
        for (QuizSubmissionRequest.QuizAnswer answer : request.answers()) {
            // 퀴즈 정보 조회
            LectureQuizRecord quiz = quizzes.stream()
                    .filter(q -> q.getId().equals(answer.quizId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(OneYouStatusCode.BadRequest, "존재하지 않는 퀴즈입니다."));

            // 선택한 옵션 정보 조회
            LectureQuizOptionRecord selectedOption = lectureRepository.fetchQuizOption(answer.selectedOptionId());
            if (selectedOption == null) {
                throw new BaseException(OneYouStatusCode.BadRequest, "존재하지 않는 옵션입니다.");
            }

            // 정답 옵션 조회
            LectureQuizOptionRecord correctOption = lectureRepository.fetchCorrectOption(answer.quizId());
            if (correctOption == null) {
                throw new BaseException(OneYouStatusCode.BadRequest, "정답이 설정되지 않은 퀴즈입니다.");
            }

            // 정답 여부 확인
            boolean isCorrect = selectedOption.getIsCorrect();
            if (isCorrect) {
                correctAnswers++;
            }

            // 힌트는 틀린 경우에만 제공
            String hint = isCorrect ? null : quiz.getHint();

            // 결과 추가
            results.add(new QuizGradingResponse.QuizResult(
                    quiz.getId(),
                    quiz.getQuestion(),
                    selectedOption.getId(),
                    selectedOption.getOptionText(),
                    correctOption.getId(),
                    correctOption.getOptionText(),
                    isCorrect,
                    hint
            ));
        }

        int totalQuestions = quizzes.size();
        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;
        boolean isPerfectScore = correctAnswers == totalQuestions;

        return new QuizGradingResponse(
                request.lectureDetailId(),
                totalQuestions,
                correctAnswers,
                score,
                isPerfectScore,
                results
        );
    }

    public String updateLectureProgress(UUID userId, LectureProgressRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LectureNotFound, "해당 강의를 찾을 수 없습니다.");
        }

        // 학생 강의 진도 업데이트
        lectureRepository.insertOrUpdateLectureProgress(userId, request.lectureDetailId(), request.isCompleted());

        return request.isCompleted() ? "강의가 완료로 표시되었습니다." : "강의 완료가 취소되었습니다.";
    }
}
