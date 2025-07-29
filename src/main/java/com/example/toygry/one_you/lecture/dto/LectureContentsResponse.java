package com.example.toygry.one_you.lecture.dto;

import com.example.toygry.one_you.jooq.generated.tables.records.*;

import java.util.List;
import java.util.UUID;

public record LectureContentsResponse(

        UUID lectureDetailId,
        String title,
        String type,      // LIVE, VIDEO, QUIZ

        // LIVE or VIDEO 공통
        String videoUrl,
        String contents,

        // 비디오 전용
        String reviewUrl,
        String teacherFeedback,

        // 퀴즈 전용
        List<QuizWithOptions> quizzes
) {

    // 비디오 강의 응답 생성
    public static LectureContentsResponse ofVideo(LectureDetailRecord detail, LectureContentRecord content, StudentReviewSubmissionRecord submission) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                content.getVideoUrl(),
                content.getContents(),
                submission != null ? submission.getReviewUrl() : null,
                submission != null ? submission.getTeacherFeedback() : null,
                null
        );
    }

    // 라이브 강의 응답 생성
    public static LectureContentsResponse ofLive(LectureDetailRecord detail, LectureContentRecord content) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                content.getVideoUrl(),
                content.getContents(),
                null,
                null,
                null
        );
    }

    // 퀴즈 응답 생성
    public static LectureContentsResponse ofQuiz(LectureDetailRecord detail, List<QuizWithOptions> quizzes) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                null,
                null,
                null,
                null,
                quizzes
        );
    }

    // 퀴즈 + 선택지
    public record QuizWithOptions(
            UUID quizId,
            String question,
            List<Option> options
    ) {
        public static QuizWithOptions from(LectureQuizRecord quiz, List<LectureQuizOptionRecord> optionList) {
            List<Option> options = optionList.stream()
                    .map(opt -> new Option(opt.getId(), opt.getOptionText()))
                    .toList();

            return new QuizWithOptions(quiz.getId(), quiz.getQuestion(), options);
        }
    }

    // 선택지 정보
    public record Option(
            UUID optionId,
            String optionText
    ) {}
}
