package com.example.toygry.one_you.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserLectureResponse {
    private UUID lectureId;
    private String title;
    private String instructorName;
    private int progress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private String categoryName;
}
