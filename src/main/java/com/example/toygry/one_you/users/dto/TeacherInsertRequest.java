package com.example.toygry.one_you.users.dto;

import java.time.LocalDateTime;

public record TeacherInsertRequest(
        String userId,
        String password,
        String name,
        String image

) {

}