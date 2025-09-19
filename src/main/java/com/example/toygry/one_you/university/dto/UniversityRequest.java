package com.example.toygry.one_you.university.dto;

public record UniversityRequest(
        String name,
        String logoImage,
        String universityType  // NATIONAL, PRIVATE, MEDICAL
) {}