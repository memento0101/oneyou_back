package com.example.toygry.one_you.mainBannerFirst.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.MainBanner.MAIN_BANNER;


public record MainBannerFirstResponse(
        UUID id,
        String title,
        String image,
        String url,
        int bannerOrder,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MainBannerFirstResponse fromRecord(Record record) {
        return new MainBannerFirstResponse(
                record.get(MAIN_BANNER.ID),
                record.get(MAIN_BANNER.TITLE),
                record.get(MAIN_BANNER.IMAGE),
                record.get(MAIN_BANNER.URL),
                record.get(MAIN_BANNER.BANNER_ORDER),
                record.get(MAIN_BANNER.ACTIVE),
                record.get(MAIN_BANNER.CREATED_AT),
                record.get(MAIN_BANNER.UPDATED_AT)
        );
    }
}
