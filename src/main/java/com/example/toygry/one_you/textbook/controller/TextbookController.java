package com.example.toygry.one_you.textbook.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.textbook.dto.TextbookRequest;
import com.example.toygry.one_you.textbook.dto.TextbookResponse;
import com.example.toygry.one_you.textbook.dto.TextbookUpdateRequest;
import com.example.toygry.one_you.textbook.service.TextbookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/textbooks")
@RequiredArgsConstructor
@Tag(name = "Textbook", description = "교재 관리 API")
public class TextbookController {

    private final TextbookService textbookService;

    @GetMapping
    @Operation(summary = "모든 교재 조회", description = "모든 교재를 강의 정보와 함께 조회합니다.")
    public ApiResponse<List<TextbookResponse>> getAllTextbooks() {
        List<TextbookResponse> textbooks = textbookService.getAllTextbooks();
        return ApiResponse.success(textbooks);
    }

    @GetMapping("/lecture/{lectureId}")
    @Operation(summary = "강의별 교재 조회", description = "특정 강의에 속한 교재들을 조회합니다.")
    public ApiResponse<List<TextbookResponse.SimpleTextbookResponse>> getTextbooksByLectureId(
            @Parameter(description = "강의 ID") @PathVariable UUID lectureId) {
        List<TextbookResponse.SimpleTextbookResponse> textbooks =
                textbookService.getTextbooksByLectureId(lectureId);
        return ApiResponse.success(textbooks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "교재 상세 조회", description = "교재 ID로 교재 상세 정보를 조회합니다.")
    public ApiResponse<TextbookResponse> getTextbookById(
            @Parameter(description = "교재 ID") @PathVariable UUID id) {
        TextbookResponse textbook = textbookService.getTextbookById(id);
        return ApiResponse.success(textbook);
    }

    @PostMapping
    @Operation(summary = "교재 생성", description = "새로운 교재를 생성합니다.")
    public ApiResponse<String> createTextbook(@RequestBody TextbookRequest request) {
        textbookService.createTextbook(request);
        return ApiResponse.success("교재가 성공적으로 생성되었습니다.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "교재 수정", description = "교재 정보를 수정합니다.")
    public ApiResponse<String> updateTextbook(
            @Parameter(description = "교재 ID") @PathVariable UUID id,
            @RequestBody TextbookUpdateRequest request) {
        textbookService.updateTextbook(id, request);
        return ApiResponse.success("교재가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "교재 삭제", description = "교재를 삭제합니다.")
    public ApiResponse<String> deleteTextbook(
            @Parameter(description = "교재 ID") @PathVariable UUID id) {
        textbookService.deleteTextbook(id);
        return ApiResponse.success("교재가 성공적으로 삭제되었습니다.");
    }
}