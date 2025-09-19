package com.example.toygry.one_you.users.dto;

import java.util.List;

public record TeacherInsertRequest(
        String userId,
        String password,
        String name,
        String image,
        List<String> teachingSubjects,    // 강의 분야 (복수 선택)
        String bankName,                  // 은행명
        String accountNumber,             // 계좌번호
        String accountHolder,             // 예금주
        String businessNumber             // 사업자등록번호
) {}