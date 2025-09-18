package com.example.toygry.one_you.video.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.lecture.service.LectureService;
import com.example.toygry.one_you.video.dto.VideoUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/lectures-video")
@RequiredArgsConstructor
@Tag(name = "강의 비디오 관리", description = "강의 비디오 업로드 및 관리 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class LectureVideoController {

    private final LectureService lectureService;

    @Operation(
            summary = "강의 비디오 업로드",
            description = "선생님이 강의에 비디오를 Vimeo에 업로드합니다. 대용량 파일 지원 (최대 10분 대기시간)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 형식 또는 크기)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음 (선생님만 업로드 가능)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "업로드 실패")
    })
    @PostMapping("/{lectureContentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<VideoUploadResponse> uploadLectureVideo(
            @Parameter(description = "강의 콘텐츠 ID", required = true)
            @PathVariable UUID lectureContentId,

            @Parameter(description = "업로드할 비디오 파일", required = true)
            @RequestParam("video") MultipartFile videoFile,

            @AuthenticationPrincipal UserTokenPrincipal principal) {

        // 서비스 레이어로 위임
        VideoUploadResponse response = lectureService.uploadVideoToVimeo(lectureContentId, videoFile, principal.getUuid());
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "강의 비디오 정보 조회",
            description = "강의의 비디오 정보를 조회합니다."
    )
    @GetMapping("/{lectureContentId}")
    public ApiResponse<VideoUploadResponse> getLectureVideo(
            @Parameter(description = "강의 콘텐츠 ID", required = true)
            @PathVariable UUID lectureContentId) {

        VideoUploadResponse response = lectureService.getVideoInfo(lectureContentId);
        return ApiResponse.success(response);
    }

    @Operation(
            summary = "강의 비디오 삭제",
            description = "선생님이 강의의 비디오를 삭제합니다. (Vimeo에서는 삭제되지 않고 DB에서만 연결 해제)"
    )
    @DeleteMapping("/{lectureContentId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<String> deleteLectureVideo(
            @Parameter(description = "강의 콘텐츠 ID", required = true)
            @PathVariable UUID lectureContentId,

            @AuthenticationPrincipal UserTokenPrincipal principal) {

        lectureService.removeVideoId(lectureContentId);
        return ApiResponse.success("비디오가 삭제되었습니다.");
    }
}