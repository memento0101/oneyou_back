package com.example.toygry.one_you.users.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import com.example.toygry.one_you.users.dto.TeacherInsertRequest;
import com.example.toygry.one_you.users.dto.UserInsertRequest;
import com.example.toygry.one_you.users.dto.UserResponse;
import com.example.toygry.one_you.users.repository.UsersRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final ObjectMapper objectmapper;

    public List<UserResponse> getAllUsers() {
        return usersRepository.findAllUsers()
                .stream()
                .map(this::toRecord)
                .toList();
    }

    public List<UserResponse.GoalUniversity> getUserGoal(UUID userId) {
        return usersRepository.findGoalUniversities(userId);
    }

    private UserResponse toRecord(Users user) {
        try {
            var goals = objectmapper.readValue(
                    user.getGoalUniversities().data(),
                    new TypeReference<List<UserResponse.GoalUniversity>>() {});
            var eju = objectmapper.readValue(
                    user.getEjuScores().data(),
                    new TypeReference<UserResponse.EjuScores>() {});

            return new UserResponse(
                    user.getId(),
                    user.getUserId(),
                    user.getName(),
                    user.getStudentContact(),
                    user.getParentContact(),
                    user.getAddress(),
                    goals,
                    user.getStudyYears(),
                    user.getMajorType(),
                    eju,
                    user.getNote(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getRole()
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 에러", e);
        }
    }

    public void insertStudent(UserInsertRequest request) {
        usersRepository.insertStudent(request);
    }

    public void insertTeacher(TeacherInsertRequest request) {
        usersRepository.insertTeacher(request);
    }

    public UserResponse getUserById(UUID id) {
        return Optional.ofNullable(usersRepository.findByID(id))
                .map(this::toRecord)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.USER_NOT_FOUND));
    }

}
