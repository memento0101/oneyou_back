package com.example.toygry.one_you.lecture.dto;

import java.util.UUID;

public record StudentLinkSubmissionRequest(
        UUID lectureDetailId,
        String reviewUrl
) {
}