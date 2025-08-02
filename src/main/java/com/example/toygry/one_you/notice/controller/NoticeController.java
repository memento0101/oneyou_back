package com.example.toygry.one_you.notice.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.config.security.UserTokenPrincipal;
import com.example.toygry.one_you.notice.dto.NoticeRequest;
import com.example.toygry.one_you.notice.dto.NoticeResponse;
import com.example.toygry.one_you.notice.dto.NoticeUpdateRequest;
import com.example.toygry.one_you.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Tag(name = "공지사항", description = "공지사항 작성, 조회, 수정, 삭제 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 목록 조회", description = "모든 공지사항을 페이징하여 조회합니다. 중요 공지가 최상단에 표시됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/list")
    public ApiResponse<List<NoticeResponse>> getNoticeList(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
    ) {
        List<NoticeResponse> notices = noticeService.getNoticeList(page, size);
        return ApiResponse.success(notices);
    }

    @Operation(summary = "공지사항 상세 조회", description = "특정 공지사항의 상세 내용을 조회합니다. 조회시 조회수가 1 증가합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeResponse> getNotice(
            @Parameter(description = "공지사항 ID") @PathVariable String noticeId
    ) {
        NoticeResponse notice = noticeService.getNotice(noticeId);
        return ApiResponse.success(notice);
    }

    @Operation(summary = "최근 공지사항 조회", description = "가장 최근에 작성된 공지사항을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "중요 공지사항 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/top")
    public ApiResponse<List<NoticeResponse>> getTopNotice(
            @Parameter(description = "조회할 공지사항 개수") @RequestParam(defaultValue = "5") int limit
    ) {
        List<NoticeResponse> topNotices = noticeService.getTopNotices(limit);
        return ApiResponse.success(topNotices);
    }

    @Operation(summary = "공지사항 작성", description = "새로운 공지사항을 작성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ApiResponse<String> createNotice(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Valid @RequestBody NoticeRequest request
    ) {
        String noticeId = noticeService.createNotice(userPrincipal.getUuid(), request);
        return ApiResponse.success(noticeId);
    }

    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다. 본인이 작성한 공지사항만 수정 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @PutMapping("/{noticeId}")
    public ApiResponse<String> updateNotice(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Parameter(description = "공지사항 ID") @PathVariable String noticeId,
            @Valid @RequestBody NoticeUpdateRequest request
    ) {
        String updatedNoticeId = noticeService.updateNotice(userPrincipal.getUuid(), noticeId, request);
        return ApiResponse.success(updatedNoticeId);
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다. 본인이 작성한 공지사항만 삭제 가능합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공지사항 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
    })
    @DeleteMapping("/{noticeId}")
    public ApiResponse<String> deleteNotice(
            @Parameter(hidden = true) @AuthenticationPrincipal UserTokenPrincipal userPrincipal,
            @Parameter(description = "공지사항 ID") @PathVariable String noticeId
    ) {
        noticeService.deleteNotice(userPrincipal.getUuid(), noticeId);
        return ApiResponse.success("공지사항이 삭제되었습니다.");
    }
}
