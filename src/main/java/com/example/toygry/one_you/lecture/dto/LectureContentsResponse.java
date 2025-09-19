package com.example.toygry.one_you.lecture.dto;

import com.example.toygry.one_you.jooq.generated.tables.records.*;

import java.util.List;
import java.util.UUID;

public record LectureContentsResponse(

        UUID lectureDetailId,
        String title,
        String type,      // LIVE, VIDEO, QUIZ

        // LIVE or VIDEO 공통
        VideoInfo video,
        String contents,

        // 비디오 전용
        String reviewUrl,
        String teacherFeedback,

        // 퀴즈 전용
        List<QuizWithOptions> quizzes,

        // 첨부파일 (모든 타입 공통)
        List<LectureAttachmentResponse> attachments
) {

    // 비디오 강의 응답 생성
    public static LectureContentsResponse ofVideo(LectureDetailRecord detail, LectureContentRecord content, VideoRecord video, StudentReviewSubmissionRecord submission, List<LectureAttachmentResponse> attachments) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                video != null ? VideoInfo.from(video) : null,
                content.getContents(),
                submission != null ? submission.getReviewUrl() : null,
                submission != null ? submission.getTeacherFeedback() : null,
                null,
                attachments
        );
    }

    // 라이브 강의 응답 생성
    public static LectureContentsResponse ofLive(LectureDetailRecord detail, LectureContentRecord content, VideoRecord video, List<LectureAttachmentResponse> attachments) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                video != null ? VideoInfo.from(video) : null,
                content.getContents(),
                null,
                null,
                null,
                attachments
        );
    }

    // 퀴즈 응답 생성
    public static LectureContentsResponse ofQuiz(LectureDetailRecord detail, List<QuizWithOptions> quizzes, List<LectureAttachmentResponse> attachments) {
        return new LectureContentsResponse(
                detail.getId(),
                detail.getTitle(),
                detail.getType(),
                null,
                null,
                null,
                null,
                quizzes,
                attachments
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

    // 비디오 정보
    public record VideoInfo(
            UUID videoId,
            String title,
            String platform,
            String externalVideoId,
            String embedUrl,
            String thumbnailUrl,
            Boolean isLive,
            String liveStatus
    ) {
        public static VideoInfo from(VideoRecord video) {
            return new VideoInfo(
                    video.getId(),
                    video.getTitle(),
                    video.getPlatform(),
                    video.getExternalVideoId(),
                    video.getEmbedUrl(),
                    video.getThumbnailUrl(),
                    video.getIsLive(),
                    video.getLiveStatus()
            );
        }
    }
}
