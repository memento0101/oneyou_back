package com.example.toygry.one_you.mainBannerFirst.dto;

import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.tables.MainBannerFirst.MAIN_BANNER_FIRST;


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
                record.get(MAIN_BANNER_FIRST.ID),
                record.get(MAIN_BANNER_FIRST.TITLE),
                record.get(MAIN_BANNER_FIRST.IMAGE),
                record.get(MAIN_BANNER_FIRST.URL),
                record.get(MAIN_BANNER_FIRST.BANNER_ORDER),
                record.get(MAIN_BANNER_FIRST.ACTIVE),
                record.get(MAIN_BANNER_FIRST.CREATED_AT),
                record.get(MAIN_BANNER_FIRST.UPDATED_AT)
        );
    }
}
