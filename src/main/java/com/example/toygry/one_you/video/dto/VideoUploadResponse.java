package com.example.toygry.one_you.video.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class VideoUploadResponse {

    private UUID lectureContentId;
    private String vimeoVideoId;
    private String fileName;
    private Long fileSize;
    private String message;

}