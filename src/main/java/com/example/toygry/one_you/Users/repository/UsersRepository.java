package com.example.toygry.one_you.Users.repository;

import com.example.toygry.one_you.Users.dto.UserInsertRequest;
import com.example.toygry.one_you.jooq.generated.tables.daos.UsersDao;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
public class UsersRepository {

    private final UsersDao usersDao;
    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    // Dao 로 쓰는 방식 => JPA 감성
    public List<Users> findAllUsers() {
        return usersDao.findAll();
    }

    // table 갖다가 쓰는 방식
    public Users findByUsername(String username) {
        return dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOneInto(Users.class);
    }

    public void insertUser(UserInsertRequest req) {
        dsl.insertInto(USERS)
                .set(USERS.ID, UUID.randomUUID())
                .set(USERS.USERNAME, req.getUsername())
                .set(USERS.PASSWORD, passwordEncoder.encode(req.getPassword()))
                .set(USERS.ROLE, req.getRole())
                .set(USERS.NAME, req.getName())
                .set(USERS.EMAIL, req.getEmail())
                .set(USERS.S_PHONE_NUMBER, req.getSPhoneNumber())
                .set(USERS.P_PHONE_NUMBER, req.getPPhoneNumber())
                .set(USERS.CREATED_AT, LocalDateTime.now())
                .execute();
    }
}
