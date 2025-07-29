package com.example.toygry.one_you.lecture.dto;

import java.util.List;
import java.util.UUID;

public record QuizSubmissionRequest(
        UUID lectureDetailId,
        List<QuizAnswer> answers
) {
    public record QuizAnswer(
            UUID quizId,
            UUID selectedOptionId
    ) {}
}