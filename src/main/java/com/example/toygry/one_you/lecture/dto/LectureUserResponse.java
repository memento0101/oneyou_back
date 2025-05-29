package com.example.toygry.one_you.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class LectureUserResponse {

    private LectureResponse lecture;
    private List<UserLectureInfo> users;


    @Getter
    @Builder
    public static class UserLectureInfo {
        private UUID userId;
        private String username;
        private String email;
        private int progress;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean isActive;
    }
}
