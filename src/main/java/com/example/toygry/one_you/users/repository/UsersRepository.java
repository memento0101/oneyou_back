package com.example.toygry.one_you.users.repository;

import com.example.toygry.one_you.jooq.generated.tables.daos.UsersDao;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import com.example.toygry.one_you.users.dto.UserInsertRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

//    private final UsersDao usersDao;
    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    // Dao 로 쓰는 방식 => JPA 감성
    public List<Users> findAllUsers() {
        return dsl.selectFrom(USERS).fetchInto(Users.class);
    }

    // table 갖다가 쓰는 방식
    public Users findByUsername(String username) {
        return dsl.selectFrom(USERS)
                .where(USERS.USER_ID.eq(username))
                .fetchOneInto(Users.class);
    }

    public void insertStudent(UserInsertRequest request) {
        String goalJson = toJson(request.goalUniversities());
        String ejuJson = toJson(request.ejuScores());

        dsl.insertInto(USERS)
                .set(USERS.ID, UUID.randomUUID())
                .set(USERS.USER_ID, request.userId())
                .set(USERS.PASSWORD, passwordEncoder.encode(request.password()))
                .set(USERS.NAME, request.name())
                .set(USERS.STUDENT_CONTACT, request.studentContact())
                .set(USERS.PARENT_CONTACT, request.parentContact())
                .set(USERS.ADDRESS, request.address())
                .set(USERS.GOAL_UNIVERSITIES, JSON.valueOf(goalJson))
                .set(USERS.STUDY_YEARS, request.studyYears())
                .set(USERS.MAJOR_TYPE, request.majorType())
                .set(USERS.EJU_SCORES, JSON.valueOf(ejuJson))
                .set(USERS.NOTE, request.note())
                .set(USERS.CREATED_AT, LocalDateTime.now())
                .set(USERS.UPDATED_AT, LocalDateTime.now())
                .set(USERS.ROLE,"STUDENT")
                .execute();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }
//
//    public Users findById(UUID id) {
//        return usersDao.findById(id);
//    }
}
