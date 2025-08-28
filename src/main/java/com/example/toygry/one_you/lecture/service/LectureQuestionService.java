package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.repository.LectureQuestionAnswerRepository;
import com.example.toygry.one_you.lecture.repository.LectureQuestionRepository;
import com.example.toygry.one_you.lecture.repository.StudentLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LectureQuestionService {

    private final LectureQuestionRepository questionRepository;
    private final LectureQuestionAnswerRepository answerRepository;
    private final StudentLectureRepository studentLectureRepository;

    @Transactional
    public UUID createQuestion(UUID lectureId, UUID studentId, LectureQuestionRequest request) {
        LocalDateTime expireDate = studentLectureRepository.findLectureExpireDate(studentId, lectureId);
        if (expireDate == null || expireDate.isBefore(LocalDateTime.now())) {
            throw new BaseException(OneYouStatusCode.LECTURE_FORBIDDEN);
        }

        return questionRepository.createQuestion(lectureId, studentId, request.title(), request.content());
    }

    public List<LectureQuestionListResponse> getQuestionsByStudent(UUID studentId) {
        return questionRepository.findQuestionsByStudentId(studentId);
    }

    public List<LectureQuestionListResponse> getQuestionsByTeacher(UUID teacherId) {
        return questionRepository.findQuestionsByTeacherId(teacherId);
    }

    public LectureQuestionResponse getQuestionDetail(UUID questionId, UUID userId) {
        LectureQuestionResponse question = questionRepository.findQuestionDetailById(questionId)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.NOT_FOUND));

        boolean hasAccess = questionRepository.isStudentQuestionOwner(questionId, userId) ||
                           questionRepository.isTeacherQuestionAccessible(questionId, userId);
        
        if (!hasAccess) {
            throw new BaseException(OneYouStatusCode.FORBIDDEN);
        }

        LectureQuestionAnswerResponse answer = answerRepository.findAnswerByQuestionId(questionId)
                .orElse(null);
        
        return new LectureQuestionResponse(
                question.id(),
                question.lectureId(),
                question.lectureTitle(),
                question.studentId(),
                question.studentName(),
                question.title(),
                question.content(),
                question.isAnswered(),
                question.createdAt(),
                question.updatedAt(),
                answer
        );
    }

    @Transactional
    public void createOrUpdateAnswer(UUID questionId, UUID teacherId, LectureQuestionAnswerRequest request) {
        if (!questionRepository.isTeacherQuestionAccessible(questionId, teacherId)) {
            throw new BaseException(OneYouStatusCode.FORBIDDEN);
        }

        if (answerRepository.existsByQuestionId(questionId)) {
            answerRepository.updateAnswer(questionId, request.content());
        } else {
            answerRepository.createAnswer(questionId, teacherId, request.content());
            questionRepository.updateQuestionAnsweredStatus(questionId, true);
        }
    }
}