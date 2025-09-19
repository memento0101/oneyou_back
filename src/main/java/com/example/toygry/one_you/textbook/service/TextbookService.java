package com.example.toygry.one_you.textbook.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.textbook.dto.TextbookRequest;
import com.example.toygry.one_you.textbook.dto.TextbookResponse;
import com.example.toygry.one_you.textbook.dto.TextbookUpdateRequest;
import com.example.toygry.one_you.textbook.repository.TextbookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TextbookService {

    private final TextbookRepository textbookRepository;

    public List<TextbookResponse> getAllTextbooks() {
        return textbookRepository.findAllTextbooks();
    }

    public List<TextbookResponse.SimpleTextbookResponse> getTextbooksByLectureId(UUID lectureId) {
        return textbookRepository.findTextbooksByLectureId(lectureId);
    }

    public TextbookResponse getTextbookById(UUID id) {
        TextbookResponse textbook = textbookRepository.findTextbookById(id);
        if (textbook == null) {
            throw new BaseException(OneYouStatusCode.NOT_FOUND, "교재를 찾을 수 없습니다.");
        }
        return textbook;
    }

    public UUID createTextbook(TextbookRequest request) {
        validateTextbookRequest(request);
        return textbookRepository.createTextbook(request);
    }

    public void updateTextbook(UUID id, TextbookUpdateRequest request) {
        if (!textbookRepository.existsById(id)) {
            throw new BaseException(OneYouStatusCode.NOT_FOUND, "교재를 찾을 수 없습니다.");
        }
        validateTextbookUpdateRequest(request);
        textbookRepository.updateTextbook(id, request);
    }

    public void deleteTextbook(UUID id) {
        if (!textbookRepository.existsById(id)) {
            throw new BaseException(OneYouStatusCode.NOT_FOUND, "교재를 찾을 수 없습니다.");
        }
        textbookRepository.deleteTextbook(id);
    }

    private void validateTextbookRequest(TextbookRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "교재명은 필수입니다.");
        }
        if (request.price() == null || request.price() < 0) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }
        if (request.lectureId() == null) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 ID는 필수입니다.");
        }
    }

    private void validateTextbookUpdateRequest(TextbookUpdateRequest request) {
        if (request.name() == null || request.name().trim().isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "교재명은 필수입니다.");
        }
        if (request.price() == null || request.price() < 0) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }
        if (request.lectureId() == null) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 ID는 필수입니다.");
        }
    }
}