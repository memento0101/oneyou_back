package com.example.toygry.one_you.lecture.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LectureAttachmentResponse(
        UUID id,
        UUID lectureDetailId,
        String fileName,
        String originalFileName,
        String filePath,
        Long fileSize,
        String fileType,
        String mimeType,
        String description,
        Integer downloadCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}