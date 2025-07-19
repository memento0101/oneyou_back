package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.dto.LectureContentsResponse;
import com.example.toygry.one_you.lecture.dto.LectureDetailRequest;
import com.example.toygry.one_you.lecture.dto.LectureDetailResponse;
import com.example.toygry.one_you.lecture.dto.TeacherLectureGroupResponse;
import com.example.toygry.one_you.lecture.service.LectureService;
import com.example.toygry.one_you.lecture.service.StudentLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final StudentLectureService studentLectureService;
    private final LectureService lectureService;

    // 전체 강의 목록 출력
    @GetMapping("/list")
    public ApiResponse<List<TeacherLectureGroupResponse>> getLectureList(
            @AuthenticationPrincipal UserTokenPrincipal userPrincipal
    ) {
        return ApiResponse.success(studentLectureService.getActiveLecturesByUser(userPrincipal.getUuid()));
    }

    // 강의 목차 출력 + 현재 진행도, 남은 수강 일수도 출력
    @GetMapping("/{lectureId}")
    public ApiResponse<LectureDetailResponse> getLectureDetail(
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @PathVariable UUID lectureId
    ) {
        return ApiResponse.success(lectureService.getLectureDetail(userTokenPrincipal.getUuid(), lectureId));
    }

    // 강의 목차 클릭시 상세 출력
    @GetMapping("/detail")
    public ApiResponse<LectureContentsResponse> getLectureContentsDetail(
            @AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal,
            @RequestBody LectureDetailRequest lectureDetailRequest
    ) {
        return ApiResponse.success(lectureService.getLectureContentsDetail(userTokenPrincipal.getUuid(), lectureDetailRequest));
    }

    // 출석 체크



}
