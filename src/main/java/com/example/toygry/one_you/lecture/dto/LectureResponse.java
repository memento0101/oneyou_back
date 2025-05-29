package com.example.toygry.one_you.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LectureResponse {
    private UUID id;
    private String title;
    private String description;
    private String instructorName;
    private int durationMinutes;
    private UUID categoryId;       // FK Category ID
    private String categoryName;   // Category Name (from lecture_category)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
