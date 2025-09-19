package com.example.toygry.one_you.university.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.university.dto.UniversityRequest;
import com.example.toygry.one_you.university.dto.UniversityResponse;
import com.example.toygry.one_you.university.service.UniversityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @Operation(summary = "전체 대학 목록 조회", description = "전체 대학 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<UniversityResponse>> getAllUniversities() {
        return ApiResponse.success(universityService.getAllUniversities());
    }

    @Operation(summary = "대학 타입별 조회", description = "대학 타입(NATIONAL/PRIVATE/MEDICAL)별로 대학을 조회합니다.")
    @GetMapping("/type/{universityType}")
    public ApiResponse<List<UniversityResponse>> getUniversitiesByType(
            @Parameter(description = "대학 타입 (NATIONAL, PRIVATE, MEDICAL)", required = true)
            @PathVariable String universityType
    ) {
        return ApiResponse.success(universityService.getUniversitiesByType(universityType));
    }

    @Operation(summary = "대학 상세 조회", description = "특정 대학의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<UniversityResponse> getUniversityById(
            @Parameter(description = "대학 ID", required = true) @PathVariable UUID id
    ) {
        return ApiResponse.success(universityService.getUniversityById(id));
    }

    @Operation(summary = "대학 생성", description = "새로운 대학을 생성합니다.")
    @PostMapping
    public ApiResponse<UUID> createUniversity(@RequestBody UniversityRequest request) {
        UUID universityId = universityService.createUniversity(request);
        return ApiResponse.success(universityId);
    }

    @Operation(summary = "대학 수정", description = "기존 대학 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ApiResponse<String> updateUniversity(
            @Parameter(description = "대학 ID", required = true) @PathVariable UUID id,
            @RequestBody UniversityRequest request
    ) {
        universityService.updateUniversity(id, request);
        return ApiResponse.success("대학 정보가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "대학 삭제", description = "기존 대학을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUniversity(
            @Parameter(description = "대학 ID", required = true) @PathVariable UUID id
    ) {
        universityService.deleteUniversity(id);
        return ApiResponse.success("대학이 성공적으로 삭제되었습니다.");
    }
}