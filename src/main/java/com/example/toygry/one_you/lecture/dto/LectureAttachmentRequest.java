package com.example.toygry.one_you.lecture.dto;

import java.util.UUID;

public record LectureAttachmentRequest(
        UUID lectureDetailId,
        String fileName,
        String originalFileName,
        String filePath,
        Long fileSize,
        String fileType,
        String mimeType,
        String description
) {}