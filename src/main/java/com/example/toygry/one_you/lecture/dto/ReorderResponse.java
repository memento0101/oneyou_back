package com.example.toygry.one_you.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "목차 순서 변경 응답")
public record ReorderResponse(
        @Schema(description = "처리된 챕터 수")
        int processedChapters,

        @Schema(description = "처리된 강의 상세 수")
        int processedDetails,

        @Schema(description = "처리 결과 메시지")
        String message
) {
}