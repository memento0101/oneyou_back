package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentRequest;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentResponse;
import com.example.toygry.one_you.lecture.service.LectureAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lecture-attachments")
@RequiredArgsConstructor
@Tag(name = "Lecture Attachment", description = "강의 첨부파일 관리 API")
public class LectureAttachmentController {

    private final LectureAttachmentService lectureAttachmentService;

    @PostMapping("/upload")
    @Operation(summary = "첨부파일 업로드", description = "강의 상세에 첨부파일을 업로드합니다.")
    public ApiResponse<LectureAttachmentResponse> uploadAttachment(
            @Parameter(description = "강의 상세 ID") @RequestParam UUID lectureDetailId,
            @Parameter(description = "업로드할 파일") @RequestParam("file") MultipartFile file,
            @Parameter(description = "파일 설명 (선택사항)") @RequestParam(required = false) String description) {

        LectureAttachmentResponse response = lectureAttachmentService.uploadAttachment(lectureDetailId, file, description);
        return ApiResponse.success(response);
    }

    @GetMapping("/lecture-detail/{lectureDetailId}")
    @Operation(summary = "강의별 첨부파일 목록 조회", description = "특정 강의 상세의 모든 첨부파일을 조회합니다.")
    public ApiResponse<List<LectureAttachmentResponse>> getAttachmentsByLectureDetail(
            @Parameter(description = "강의 상세 ID") @PathVariable UUID lectureDetailId) {

        List<LectureAttachmentResponse> attachments = lectureAttachmentService.getAttachmentsByLectureDetail(lectureDetailId);
        return ApiResponse.success(attachments);
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "첨부파일 정보 조회", description = "특정 첨부파일의 정보를 조회합니다.")
    public ApiResponse<LectureAttachmentResponse> getAttachment(
            @Parameter(description = "첨부파일 ID") @PathVariable UUID attachmentId) {

        LectureAttachmentResponse attachment = lectureAttachmentService.getAttachment(attachmentId);
        return ApiResponse.success(attachment);
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "첨부파일 다운로드", description = "첨부파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "첨부파일 ID") @PathVariable UUID attachmentId) {

        Resource resource = lectureAttachmentService.downloadAttachment(attachmentId);
        LectureAttachmentResponse attachment = lectureAttachmentService.getAttachment(attachmentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.originalFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/{attachmentId}")
    @Operation(summary = "첨부파일 정보 수정", description = "첨부파일의 메타데이터를 수정합니다.")
    public ApiResponse<LectureAttachmentResponse> updateAttachment(
            @Parameter(description = "첨부파일 ID") @PathVariable UUID attachmentId,
            @RequestBody LectureAttachmentRequest request) {

        LectureAttachmentResponse response = lectureAttachmentService.updateAttachment(attachmentId, request);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "첨부파일 삭제", description = "첨부파일을 삭제합니다.")
    public ApiResponse<String> deleteAttachment(
            @Parameter(description = "첨부파일 ID") @PathVariable UUID attachmentId) {

        lectureAttachmentService.deleteAttachment(attachmentId);
        return ApiResponse.success("첨부파일이 성공적으로 삭제되었습니다.");
    }

    @DeleteMapping("/batch")
    @Operation(summary = "첨부파일 일괄 삭제", description = "여러 첨부파일을 일괄 삭제합니다.")
    public ApiResponse<String> deleteAttachments(
            @Parameter(description = "삭제할 첨부파일 ID 목록") @RequestBody List<UUID> attachmentIds) {

        lectureAttachmentService.deleteAttachments(attachmentIds);
        return ApiResponse.success("첨부파일들이 성공적으로 삭제되었습니다.");
    }
}