package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "목차 순서 변경 요청")
public record ReorderRequest(
        @Schema(description = "정렬된 챕터 목록")
        List<ChapterOrderInfo> chapters
) {

    @Schema(description = "챕터 순서 정보")
    public record ChapterOrderInfo(
            @Schema(description = "챕터 ID")
            UUID chapterId,

            @Schema(description = "챕터 순서", example = "1")
            Integer chapterOrder,

            @Schema(description = "챕터에 속한 강의 상세 목록")
            List<DetailOrderInfo> details
    ) {}

    @Schema(description = "강의 상세 순서 정보")
    public record DetailOrderInfo(
            @Schema(description = "강의 상세 ID")
            UUID detailId,

            @Schema(description = "강의 상세 순서", example = "1")
            Integer detailOrder
    ) {}
}