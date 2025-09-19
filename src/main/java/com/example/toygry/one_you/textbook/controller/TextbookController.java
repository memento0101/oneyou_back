package com.example.toygry.one_you.textbook.controller;

import com.example.toygry.one_you.textbook.dto.TextbookRequest;
import com.example.toygry.one_you.textbook.dto.TextbookResponse;
import com.example.toygry.one_you.textbook.dto.TextbookUpdateRequest;
import com.example.toygry.one_you.textbook.service.TextbookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/textbooks")
@RequiredArgsConstructor
@Tag(name = "Textbook", description = "교재 관리 API")
public class TextbookController {

    private final TextbookService textbookService;

    @GetMapping
    @Operation(summary = "모든 교재 조회", description = "모든 교재를 강의 정보와 함께 조회합니다.")
    public ResponseEntity<List<TextbookResponse>> getAllTextbooks() {
        List<TextbookResponse> textbooks = textbookService.getAllTextbooks();
        return ResponseEntity.ok(textbooks);
    }

    @GetMapping("/lecture/{lectureId}")
    @Operation(summary = "강의별 교재 조회", description = "특정 강의에 속한 교재들을 조회합니다.")
    public ResponseEntity<List<TextbookResponse.SimpleTextbookResponse>> getTextbooksByLectureId(
            @Parameter(description = "강의 ID") @PathVariable UUID lectureId) {
        List<TextbookResponse.SimpleTextbookResponse> textbooks =
                textbookService.getTextbooksByLectureId(lectureId);
        return ResponseEntity.ok(textbooks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "교재 상세 조회", description = "교재 ID로 교재 상세 정보를 조회합니다.")
    public ResponseEntity<TextbookResponse> getTextbookById(
            @Parameter(description = "교재 ID") @PathVariable UUID id) {
        TextbookResponse textbook = textbookService.getTextbookById(id);
        return ResponseEntity.ok(textbook);
    }

    @PostMapping
    @Operation(summary = "교재 생성", description = "새로운 교재를 생성합니다.")
    public ResponseEntity<Map<String, Object>> createTextbook(@RequestBody TextbookRequest request) {
        UUID textbookId = textbookService.createTextbook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "교재가 성공적으로 생성되었습니다.",
                        "textbookId", textbookId
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "교재 수정", description = "교재 정보를 수정합니다.")
    public ResponseEntity<Map<String, String>> updateTextbook(
            @Parameter(description = "교재 ID") @PathVariable UUID id,
            @RequestBody TextbookUpdateRequest request) {
        textbookService.updateTextbook(id, request);
        return ResponseEntity.ok(Map.of("message", "교재가 성공적으로 수정되었습니다."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "교재 삭제", description = "교재를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteTextbook(
            @Parameter(description = "교재 ID") @PathVariable UUID id) {
        textbookService.deleteTextbook(id);
        return ResponseEntity.ok(Map.of("message", "교재가 성공적으로 삭제되었습니다."));
    }
}