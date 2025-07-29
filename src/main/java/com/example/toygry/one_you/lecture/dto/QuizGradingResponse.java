package com.example.toygry.one_you.lecture.dto;

import java.util.List;
import java.util.UUID;

public record QuizGradingResponse(
                UUID lectureDetailId,
                int totalQuestions,
                int correctAnswers,
                int score,
                boolean isPerfectScore,
                List<QuizResult> results) {
        public record QuizResult(
                        UUID quizId,
                        String question,
                        UUID selectedOptionId,
                        String selectedOptionText,
                        UUID correctOptionId,
                        String correctOptionText,
                        boolean isCorrect,
                        String hint) {
        }
}