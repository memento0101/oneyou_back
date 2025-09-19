package com.example.toygry.one_you.textbook.dto;

import java.util.UUID;

public record TextbookRequest(
        String name,
        String description,
        String descriptionImg,
        String img,
        String link,
        Integer price,
        UUID lectureId
) {}