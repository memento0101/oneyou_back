package com.example.toygry.one_you.video.repository;

import com.example.toygry.one_you.jooq.generated.tables.records.VideoRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.example.toygry.one_you.jooq.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class VideoRepository {

    private final DSLContext dsl;

    public VideoRecord findById(UUID videoId) {
        return dsl.selectFrom(VIDEO)
                .where(VIDEO.ID.eq(videoId))
                .fetchOneInto(VideoRecord.class);
    }

    public VideoRecord findByPlatformAndExternalId(String platform, String externalVideoId) {
        return dsl.selectFrom(VIDEO)
                .where(VIDEO.PLATFORM.eq(platform)
                        .and(VIDEO.EXTERNAL_VIDEO_ID.eq(externalVideoId)))
                .fetchOneInto(VideoRecord.class);
    }

    public void insert(VideoRecord video) {
        dsl.insertInto(VIDEO)
                .set(video)
                .execute();
    }

    public void update(VideoRecord video) {
        dsl.update(VIDEO)
                .set(video)
                .where(VIDEO.ID.eq(video.getId()))
                .execute();
    }
}