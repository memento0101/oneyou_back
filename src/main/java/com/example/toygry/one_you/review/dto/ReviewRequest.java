package com.example.toygry.one_you.review.dto;

import java.util.UUID;

public record ReviewRequest(
        UUID lectureId,
        String contents,
        int score

) {}
