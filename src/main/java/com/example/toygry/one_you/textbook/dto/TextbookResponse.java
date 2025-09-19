package com.example.toygry.one_you.textbook.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TextbookResponse(
        UUID id,
        String name,
        String description,
        String descriptionImg,
        String img,
        String link,
        Integer price,
        UUID lectureId,
        String lectureTitle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public record SimpleTextbookResponse(
            UUID id,
            String name,
            String description,
            String descriptionImg,
            String img,
            String link,
            Integer price,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}