package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "수강후기 목록 응답")
public record LectureReviewListResponse(
        @Schema(description = "총 후기 수", example = "25")
        Long totalCount,

        @Schema(description = "평균 평점", example = "4.2")
        Double averageRating,

        @Schema(description = "평점별 개수")
        RatingStatistics ratingStatistics,

        @Schema(description = "후기 목록")
        List<LectureReviewResponse> reviews
) {
    @Schema(description = "평점별 통계")
    public record RatingStatistics(
            @Schema(description = "5점 개수", example = "10")
            Long rating5Count,

            @Schema(description = "4점 개수", example = "8")
            Long rating4Count,

            @Schema(description = "3점 개수", example = "4")
            Long rating3Count,

            @Schema(description = "2점 개수", example = "2")
            Long rating2Count,

            @Schema(description = "1점 개수", example = "1")
            Long rating1Count
    ) {}
}