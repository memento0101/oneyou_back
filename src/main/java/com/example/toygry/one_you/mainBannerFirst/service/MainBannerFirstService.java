package com.example.toygry.one_you.mainBannerFirst.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstOrderItem;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstRequest;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstResponse;
import com.example.toygry.one_you.mainBannerFirst.repository.MainBannerFirstRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MainBannerFirstService {

    private final MainBannerFirstRepository mainBannerFirstRepository;

    public List<MainBannerFirstResponse> getActiveMainBannerFirst() {
        return mainBannerFirstRepository.findActiveBanners();
    }

    public void deleteBanner(UUID id) {
        if (id == null) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "Id 를 확인해주세요");
        }
        mainBannerFirstRepository.deleteBanner(id);
    }

    public void updateBannerStatus(UUID id) {
        Boolean current = mainBannerFirstRepository.getCurrentActiveStatus(id);

        if (current == null) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "배너를 찾을 수 없습니다.");
        }

        if (!current) {
            int nextOrder = mainBannerFirstRepository.findMaxOrderOfActiveBanners();
            mainBannerFirstRepository.activeBanner(id, nextOrder + 1);
        } else {
            mainBannerFirstRepository.inactiveBanner(id);
        }
    }

    // 순서 변경
    public void updateBannerOrder(List<MainBannerFirstOrderItem> request) {
        mainBannerFirstRepository.bulkUpdateOrders(request);
    }

    public void createBanner(MainBannerFirstRequest request) {
        // 예외처리 필요, 최초 생성 시 무조건 false
        mainBannerFirstRepository.createBanner(request);
    }

    public void updateBanner(UUID id, MainBannerFirstRequest request) {
        mainBannerFirstRepository.updateBanner(id, request);
    }
}
