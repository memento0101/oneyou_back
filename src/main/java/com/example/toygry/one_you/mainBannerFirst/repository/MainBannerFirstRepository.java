package com.example.toygry.one_you.mainBannerFirst.repository;

import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstOrderItem;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstRequest;
import com.example.toygry.one_you.mainBannerFirst.dto.MainBannerFirstResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.CaseConditionStep;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.MAIN_BANNER;

@Repository
@RequiredArgsConstructor
public class MainBannerFirstRepository {

    private final DSLContext dsl;

    public List<MainBannerFirstResponse> findActiveBanners() {
        return dsl.selectFrom(MAIN_BANNER)
                .where(MAIN_BANNER.ACTIVE.eq(true))
                .orderBy(MAIN_BANNER.BANNER_ORDER.asc())
                .fetch()
                .map(MainBannerFirstResponse::fromRecord);
    }

    public void deleteBanner(UUID id) {
        dsl.deleteFrom(MAIN_BANNER)
                .where(MAIN_BANNER.ID.eq(id))
                .execute();
    }

    public Boolean getCurrentActiveStatus(UUID id) {
        return dsl.select(MAIN_BANNER.ACTIVE)
                .from(MAIN_BANNER)
                .where(MAIN_BANNER.ID.eq(id))
                .fetchOne(MAIN_BANNER.ACTIVE);
    }


    public void activeBanner(UUID id, int nextOrder) {
        dsl.update(MAIN_BANNER)
                .set(MAIN_BANNER.ACTIVE, true)
                .set(MAIN_BANNER.UPDATED_AT, LocalDateTime.now())
                .set(MAIN_BANNER.BANNER_ORDER, nextOrder)
                .where(MAIN_BANNER.ID.eq(id))
                .execute();
    }
    
    public void inactiveBanner(UUID id) {
        dsl.update(MAIN_BANNER)
                .set(MAIN_BANNER.ACTIVE, false)
                .set(MAIN_BANNER.UPDATED_AT, LocalDateTime.now())
                .set(MAIN_BANNER.BANNER_ORDER, -1)
                .where(MAIN_BANNER.ID.eq(id))
                .execute();
    }

    public void bulkUpdateOrders(List<MainBannerFirstOrderItem> banners) {
        if (banners == null || banners.isEmpty()) return;

        // CASE 구문
        CaseConditionStep<Integer> orderCase = null;

        for (MainBannerFirstOrderItem banner : banners) {
            CaseConditionStep<Integer> step = DSL
                    .when(MAIN_BANNER.ID.eq(banner.id()), banner.order());

            orderCase = (orderCase == null) ? step : orderCase.when(MAIN_BANNER.ID.eq(banner.id()), banner.order());
        }

        List<UUID> ids = banners.stream().map(MainBannerFirstOrderItem::id).toList();

        dsl.update(MAIN_BANNER)
                .set(MAIN_BANNER.BANNER_ORDER, orderCase)
                .where(MAIN_BANNER.ID.in(ids))
                .execute();
    }


    public int findMaxOrderOfActiveBanners() {
        Integer maxOrder = dsl.select(MAIN_BANNER.BANNER_ORDER)
                .from(MAIN_BANNER)
                .where(MAIN_BANNER.ACTIVE.isTrue())
                .orderBy(MAIN_BANNER.BANNER_ORDER.desc())
                .fetchOne(0, Integer.class);

        return maxOrder == null ? 0 : maxOrder;
    }

    public void createBanner(MainBannerFirstRequest request) {
        dsl.insertInto(MAIN_BANNER)
                .set(MAIN_BANNER.ID, UUID.randomUUID())
                .set(MAIN_BANNER.TITLE, request.title())
                .set(MAIN_BANNER.IMAGE, request.image())
                .set(MAIN_BANNER.URL, request.url())
                .set(MAIN_BANNER.ACTIVE, false)
                .set(MAIN_BANNER.BANNER_ORDER, -1)
                .set(MAIN_BANNER.CREATED_AT, LocalDateTime.now())
                .set(MAIN_BANNER.UPDATED_AT, LocalDateTime.now())
                .execute();
    }

    public void updateBanner(UUID id, MainBannerFirstRequest request) {
        dsl.update(MAIN_BANNER)
                .set(MAIN_BANNER.TITLE, request.title())
                .set(MAIN_BANNER.IMAGE, request.image())
                .set(MAIN_BANNER.URL, request.url())
                .set(MAIN_BANNER.UPDATED_AT, LocalDateTime.now())
                .where(MAIN_BANNER.ID.eq(id))
                .execute();
    }

}
