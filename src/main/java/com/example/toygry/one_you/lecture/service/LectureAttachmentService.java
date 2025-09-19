package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentRequest;
import com.example.toygry.one_you.lecture.dto.LectureAttachmentResponse;
import com.example.toygry.one_you.lecture.repository.LectureAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LectureAttachmentService {

    private final LectureAttachmentRepository lectureAttachmentRepository;
    private static final String UPLOAD_DIR = "uploads/lecture-attachments/";

    public LectureAttachmentResponse uploadAttachment(UUID lectureDetailId, MultipartFile file, String description) {
        validateFile(file);

        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (UUID + 원본 확장자)
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null && originalFileName.contains(".") ?
                    originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID().toString() + fileExtension;

            // 파일 저장
            Path targetPath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 데이터베이스에 정보 저장
            LectureAttachmentRequest request = new LectureAttachmentRequest(
                    lectureDetailId,
                    fileName,
                    originalFileName,
                    targetPath.toString(),
                    file.getSize(),
                    getFileType(originalFileName),
                    file.getContentType(),
                    description
            );

            return lectureAttachmentRepository.insertAttachment(request);

        } catch (IOException e) {
            throw new BaseException(OneYouStatusCode.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    public List<LectureAttachmentResponse> getAttachmentsByLectureDetail(UUID lectureDetailId) {
        return lectureAttachmentRepository.findAttachmentsByLectureDetailId(lectureDetailId);
    }

    public LectureAttachmentResponse getAttachment(UUID attachmentId) {
        LectureAttachmentResponse attachment = lectureAttachmentRepository.findAttachmentById(attachmentId);
        if (attachment == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "첨부파일을 찾을 수 없습니다.");
        }
        return attachment;
    }

    public Resource downloadAttachment(UUID attachmentId) {
        LectureAttachmentResponse attachment = getAttachment(attachmentId);

        try {
            Path filePath = Paths.get(attachment.filePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // 다운로드 횟수 증가
                lectureAttachmentRepository.incrementDownloadCount(attachmentId);
                return resource;
            } else {
                throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "파일을 찾을 수 없습니다.");
            }
        } catch (MalformedURLException e) {
            throw new BaseException(OneYouStatusCode.INTERNAL_SERVER_ERROR, "파일 경로가 올바르지 않습니다.");
        }
    }

    public LectureAttachmentResponse updateAttachment(UUID attachmentId, LectureAttachmentRequest request) {
        LectureAttachmentResponse existingAttachment = getAttachment(attachmentId);

        // 기존 정보 유지하면서 요청된 필드만 업데이트
        LectureAttachmentRequest updateRequest = new LectureAttachmentRequest(
                existingAttachment.lectureDetailId(),
                existingAttachment.fileName(),
                request.originalFileName() != null ? request.originalFileName() : existingAttachment.originalFileName(),
                existingAttachment.filePath(),
                existingAttachment.fileSize(),
                existingAttachment.fileType(),
                existingAttachment.mimeType(),
                request.description() != null ? request.description() : existingAttachment.description()
        );

        return lectureAttachmentRepository.updateAttachment(attachmentId, updateRequest);
    }

    public void deleteAttachment(UUID attachmentId) {
        LectureAttachmentResponse attachment = getAttachment(attachmentId);

        // 파일 시스템에서 파일 삭제
        try {
            Path filePath = Paths.get(attachment.filePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 파일 삭제 실패해도 DB 레코드는 삭제
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }

        // 데이터베이스에서 삭제
        lectureAttachmentRepository.deleteAttachment(attachmentId);
    }

    public void deleteAttachments(List<UUID> attachmentIds) {
        for (UUID attachmentId : attachmentIds) {
            deleteAttachment(attachmentId);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "파일이 선택되지 않았습니다.");
        }

        // 파일 크기 제한 (100MB)
        long maxSize = 100 * 1024 * 1024L;
        if (file.getSize() > maxSize) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "파일 크기는 100MB를 초과할 수 없습니다.");
        }

        // 위험한 파일 확장자 차단
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null) {
            String extension = originalFileName.toLowerCase();
            if (extension.endsWith(".exe") || extension.endsWith(".bat") ||
                extension.endsWith(".cmd") || extension.endsWith(".scr") ||
                extension.endsWith(".com") || extension.endsWith(".pif")) {
                throw new BaseException(OneYouStatusCode.BAD_REQUEST, "허용되지 않는 파일 형식입니다.");
            }
        }
    }

    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "UNKNOWN";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "pdf" -> "PDF";
            case "doc", "docx" -> "WORD";
            case "xls", "xlsx" -> "EXCEL";
            case "ppt", "pptx" -> "POWERPOINT";
            case "jpg", "jpeg", "png", "gif", "bmp" -> "IMAGE";
            case "mp4", "avi", "mov", "wmv" -> "VIDEO";
            case "mp3", "wav", "flac" -> "AUDIO";
            case "zip", "rar", "7z" -> "ARCHIVE";
            case "txt" -> "TEXT";
            default -> "OTHER";
        };
    }
}