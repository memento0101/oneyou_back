package com.example.toygry.one_you.university.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.UNIVERSITY;

public record UniversityResponse(
        UUID id,
        String name,
        String logoImage,
        String universityType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UniversityResponse fromRecord(Record record) {
        return new UniversityResponse(
                record.get(UNIVERSITY.ID),
                record.get(UNIVERSITY.NAME),
                record.get(UNIVERSITY.LOGO_IMAGE),
                record.get(UNIVERSITY.UNIVERSITY_TYPE),
                record.get(UNIVERSITY.CREATED_AT),
                record.get(UNIVERSITY.UPDATED_AT)
        );
    }
}