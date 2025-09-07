package com.example.toygry.one_you.mainBannerFirst.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstOrderItem;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstRequest;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstResponse;
import com.example.toygry.one_you.mainBannerFirst.service.MainBannerFirstService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/home/first")
@RequiredArgsConstructor
public class MainBannerFirstController {

    private final MainBannerFirstService mainBannerFirstService;

    @Operation(summary = "활성화 되어있는 첫번째 배너 조회", description = "활성화 되어있는 첫번째 배너를 조회합니다")
    @GetMapping
    public ApiResponse<List<MainBannerFirstResponse>> getActiveMainBannerFirst() {
        return ApiResponse.success(mainBannerFirstService.getActiveMainBannerFirst());
    }

    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBanner(@PathVariable UUID id) {
        mainBannerFirstService.deleteBanner(id);
        return ApiResponse.success("배너가 삭제 되었습니다.");
    }

    @Operation(summary = "배너 활성화 여부 변경", description = "배너의 활성화 여부를 변경합니다, 최대 5개까지 활성화 가능")
    @PatchMapping("/{id}/active")
    public ApiResponse<String> updateBannerActive(
            @PathVariable UUID id
    ) {
        mainBannerFirstService.updateBannerStatus(id);
        return ApiResponse.success("배너 활성화 상태가 변경되었습니다.");
    }

    @Operation(summary = "배너 순서 변경", description = "배너의 순서를 변경합니다")
    @PutMapping("/order")
    public ApiResponse<String> updateBannerOrder(@RequestBody List<MainBannerFirstOrderItem> request) {
        mainBannerFirstService.updateBannerOrder(request);
        return ApiResponse.success("배너 순서가 성공적으로 변경되었습니다.");
    }

    @Operation(summary = "배너 생성", description = "새로운 배너를 생성합니다 기본적으로 비활성화 되어있습니다")
    @PostMapping
    public ApiResponse<String> createBanner(@RequestBody MainBannerFirstRequest request) {
        mainBannerFirstService.createBanner(request);
        return ApiResponse.success("배너가 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "배너 수정", description = "배너를 수정합니다 순서는 여기서 바꿀 수 없습니다")
    @PutMapping("/{id}")
    public ApiResponse<String> updateBanner(
            @PathVariable UUID id,
            @RequestBody MainBannerFirstRequest request) {
        mainBannerFirstService.updateBanner(id, request);
        return ApiResponse.success("배너가 성공적으로 수정되었습니다.");
    }

}
