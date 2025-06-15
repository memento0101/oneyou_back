package com.example.toygry.one_you.mainBannerFirst.controller;

import com.example.toygry.one_you.common.response.ApiResponse;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstOrderItem;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstRequest;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstResponse;
import com.example.toygry.one_you.mainBannerFirst.service.MainBannerFirstService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/home/first")
@RequiredArgsConstructor
public class MainBannerFirstController {

    private final MainBannerFirstService mainBannerFirstService;

    @GetMapping
    public ApiResponse<List<MainBannerFirstResponse>> getActiveMainBannerFirst() {
        return ApiResponse.success(mainBannerFirstService.getActiveMainBannerFirst());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBanner(@PathVariable UUID id) {
        mainBannerFirstService.deleteBanner(id);
        return ApiResponse.success("배너가 삭제 되었습니다.");
    }

    // 배너 활성화 변경
    @PatchMapping("/{id}/active")
    public ApiResponse<String> updateBannerActive(
            @PathVariable UUID id
    ) {
        mainBannerFirstService.updateBannerStatus(id);
        return ApiResponse.success("배너 활성화 상태가 변경되었습니다.");
    }

    // 배너 순서 변경
    @PutMapping("/order")
    public ApiResponse<String> updateBannerOrder(@RequestBody List<MainBannerFirstOrderItem> request) {
        mainBannerFirstService.updateBannerOrder(request);
        return ApiResponse.success("배너 순서가 성공적으로 변경되었습니다.");
    }

    // 배너 생성
    @PostMapping
    public ApiResponse<String> createBanner(@RequestBody MainBannerFirstRequest request) {
        mainBannerFirstService.createBanner(request);
        return ApiResponse.success("배너가 성공적으로 생성되었습니다.");
    }

    // 배너 수정
    @PutMapping("/{id}")
    public ApiResponse<String> updateBanner(
            @PathVariable UUID id,
            @RequestBody MainBannerFirstRequest request) {
        mainBannerFirstService.updateBanner(id, request);
        return ApiResponse.success("배너가 성공적으로 수정되었습니다.");
    }

}
