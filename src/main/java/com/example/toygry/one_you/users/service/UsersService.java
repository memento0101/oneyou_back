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
            // goal_universities null 체크
            List<UserResponse.GoalUniversity> goals = null;
            if (user.getGoalUniversities() != null) {
                goals = objectmapper.readValue(
                        user.getGoalUniversities().data(),
                        new TypeReference<List<UserResponse.GoalUniversity>>() {});
            }

            // eju_scores null 체크
            UserResponse.EjuScores eju = null;
            if (user.getEjuScores() != null) {
                eju = objectmapper.readValue(
                        user.getEjuScores().data(),
                        new TypeReference<UserResponse.EjuScores>() {});
            }

            // teaching_subjects null 체크
            List<String> teachingSubjects = null;
            if (user.getTeachingSubjects() != null) {
                teachingSubjects = objectmapper.readValue(
                        user.getTeachingSubjects().data(),
                        new TypeReference<List<String>>() {});
            }

            return new UserResponse(
                    user.getId(),
                    user.getUserId(),
                    user.getName(),
                    // 학생 관련 필드
                    user.getStudentContact(),
                    user.getParentContact(),
                    user.getAddress(),
                    goals,
                    user.getStudyYears(),
                    user.getMajorType(),
                    eju,
                    user.getNote(),
                    // 선생님 관련 필드
                    user.getImage(),
                    teachingSubjects,
                    user.getBankName(),
                    user.getAccountNumber(),
                    user.getAccountHolder(),
                    user.getBusinessNumber(),
                    // 공통 필드
                    user.getActive(),
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
