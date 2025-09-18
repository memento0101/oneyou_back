package com.example.toygry.one_you.video.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VimeoUploadService {

    @Value("${vimeo.access.token}")
    private String accessToken;

    @Value("${vimeo.upload.timeout-seconds:600}")
    private int timeoutSeconds;

    private final ObjectMapper objectMapper;

    private OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Vimeo에 비디오 파일을 업로드합니다.
     *
     * @param videoFile 업로드할 비디오 파일
     * @return Vimeo 비디오 ID
     * @throws IOException 업로드 실패 시
     */
    public String uploadVideo(MultipartFile videoFile) throws IOException {
        log.info("Vimeo 비디오 업로드 시작 - 파일명: {}, 크기: {} bytes",
                videoFile.getOriginalFilename(), videoFile.getSize());

        // 1. 업로드 티켓 생성
        String uploadUrl = createUploadTicket(videoFile.getSize());
        log.info("업로드 티켓 생성 완료 - URL: {}", uploadUrl);

        // 2. 영상 파일 업로드
        uploadVideoFile(uploadUrl, videoFile);
        log.info("비디오 파일 업로드 완료");

        // 3. Vimeo 비디오 ID 추출
        String videoId = extractVideoId(uploadUrl);
        log.info("Vimeo 비디오 ID 추출 완료: {}", videoId);

        return videoId;
    }

    /**
     * Vimeo API를 통해 업로드 티켓을 생성합니다.
     */
    private String createUploadTicket(long fileSize) throws IOException {
        OkHttpClient client = getHttpClient();

        String requestBody = String.format(
                "{\"upload\":{\"approach\":\"tus\",\"size\":%d}}",
                fileSize
        );

        Request request = new Request.Builder()
                .url("https://api.vimeo.com/me/videos")
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.vimeo.*+json;version=3.4")
                .post(RequestBody.create(
                        MediaType.parse("application/json"),
                        requestBody
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("업로드 티켓 생성 실패: " + response.code() + " - " + errorBody);
            }

            String responseBody = response.body().string();
            JsonNode node = objectMapper.readTree(responseBody);

            JsonNode uploadNode = node.get("upload");
            if (uploadNode == null) {
                throw new IOException("응답에서 upload 정보를 찾을 수 없습니다: " + responseBody);
            }

            JsonNode uploadLinkNode = uploadNode.get("upload_link");
            if (uploadLinkNode == null) {
                throw new IOException("응답에서 upload_link를 찾을 수 없습니다: " + responseBody);
            }

            return uploadLinkNode.asText();
        }
    }

    /**
     * TUS 프로토콜을 사용하여 실제 비디오 파일을 업로드합니다.
     */
    private void uploadVideoFile(String uploadUrl, MultipartFile videoFile) throws IOException {
        OkHttpClient client = getHttpClient();

        RequestBody fileBody = RequestBody.create(
                MediaType.parse("application/offset+octet-stream"),
                videoFile.getBytes()
        );

        Request request = new Request.Builder()
                .url(uploadUrl)
                .header("Tus-Resumable", "1.0.0")
                .header("Content-Type", "application/offset+octet-stream")
                .header("Upload-Offset", "0")
                .patch(fileBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("비디오 파일 업로드 실패: " + response.code() + " - " + errorBody);
            }
        }
    }

    /**
     * 업로드 URL에서 Vimeo 비디오 ID를 추출합니다.
     */
    private String extractVideoId(String uploadUrl) {
        // URL 형태: https://files.tus.vimeo.com/files/vimeo-prod-src-tus-us-east-1.../upload?upload_id=...&video_id=123456789
        // 또는: https://api.vimeo.com/videos/123456789

        if (uploadUrl.contains("video_id=")) {
            String[] parts = uploadUrl.split("video_id=");
            if (parts.length > 1) {
                String videoIdPart = parts[1];
                // & 이후 부분 제거
                return videoIdPart.split("&")[0];
            }
        }

        // 다른 형태의 URL에서 ID 추출 시도
        if (uploadUrl.contains("/videos/")) {
            String[] parts = uploadUrl.split("/videos/");
            if (parts.length > 1) {
                return parts[1].split("/")[0];
            }
        }

        throw new RuntimeException("업로드 URL에서 비디오 ID를 추출할 수 없습니다: " + uploadUrl);
    }
}