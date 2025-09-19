package com.example.toygry.one_you.university.repository;

import com.example.toygry.one_you.university.dto.UniversityRequest;
import com.example.toygry.one_you.university.dto.UniversityResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.UNIVERSITY;

@Repository
@RequiredArgsConstructor
public class UniversityRepository {

    private final DSLContext dsl;

    public List<UniversityResponse> findAll() {
        return dsl.selectFrom(UNIVERSITY)
                .orderBy(UNIVERSITY.NAME.asc())
                .fetch()
                .map(UniversityResponse::fromRecord);
    }

    public List<UniversityResponse> findByType(String universityType) {
        return dsl.selectFrom(UNIVERSITY)
                .where(UNIVERSITY.UNIVERSITY_TYPE.eq(universityType))
                .orderBy(UNIVERSITY.NAME.asc())
                .fetch()
                .map(UniversityResponse::fromRecord);
    }

    public Optional<UniversityResponse> findById(UUID id) {
        return dsl.selectFrom(UNIVERSITY)
                .where(UNIVERSITY.ID.eq(id))
                .fetchOptional()
                .map(UniversityResponse::fromRecord);
    }

    public UUID save(UniversityRequest request) {
        UUID id = UUID.randomUUID();

        dsl.insertInto(UNIVERSITY)
                .set(UNIVERSITY.ID, id)
                .set(UNIVERSITY.NAME, request.name())
                .set(UNIVERSITY.LOGO_IMAGE, request.logoImage())
                .set(UNIVERSITY.UNIVERSITY_TYPE, request.universityType())
                .set(UNIVERSITY.CREATED_AT, LocalDateTime.now())
                .set(UNIVERSITY.UPDATED_AT, LocalDateTime.now())
                .execute();

        return id;
    }

    public void update(UUID id, UniversityRequest request) {
        dsl.update(UNIVERSITY)
                .set(UNIVERSITY.NAME, request.name())
                .set(UNIVERSITY.LOGO_IMAGE, request.logoImage())
                .set(UNIVERSITY.UNIVERSITY_TYPE, request.universityType())
                .set(UNIVERSITY.UPDATED_AT, LocalDateTime.now())
                .where(UNIVERSITY.ID.eq(id))
                .execute();
    }

    public void deleteById(UUID id) {
        dsl.deleteFrom(UNIVERSITY)
                .where(UNIVERSITY.ID.eq(id))
                .execute();
    }
}