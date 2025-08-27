-- V7: 비디오 테이블 생성 및 lecture_content 연결

-- 비디오 테이블 생성
CREATE TABLE video (
    -- 기본 정보
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    
    -- 플랫폼 및 ID
    platform VARCHAR(20) NOT NULL CHECK (platform IN ('VIMEO', 'YOUTUBE_LIVE')),
    external_video_id VARCHAR(255) NOT NULL, -- Vimeo ID 또는 YouTube Video ID
    channel_id VARCHAR(255), -- YouTube Live용 (Vimeo는 NULL)
    
    -- 임베드 URL
    embed_url TEXT NOT NULL, -- iframe src에 사용할 URL
    thumbnail_url TEXT,
    
    -- 업로드 상태
    upload_status VARCHAR(20) DEFAULT 'READY' CHECK (upload_status IN ('UPLOADING', 'PROCESSING', 'READY', 'FAILED')),
    
    -- 라이브 관련 (YouTube Live만 사용)
    is_live BOOLEAN DEFAULT false,
    live_status VARCHAR(20) CHECK (live_status IN ('SCHEDULED', 'LIVE', 'ENDED')),
    scheduled_start_time TIMESTAMP,
    
    -- 관리 정보
    uploaded_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    
    -- 중복 방지
    UNIQUE(platform, external_video_id)
);

-- lecture_content 테이블에 video_id 컬럼 추가 및 video_url 컬럼 제거
ALTER TABLE lecture_content 
ADD COLUMN video_id UUID REFERENCES video(id) ON DELETE SET NULL;

-- 기존 video_url 컬럼 제거
ALTER TABLE lecture_content 
DROP COLUMN video_url;

-- 인덱스 생성
CREATE INDEX idx_video_platform_status ON video(platform, upload_status);
CREATE INDEX idx_video_live_status ON video(live_status) WHERE platform = 'YOUTUBE_LIVE';
CREATE INDEX idx_video_uploaded_by ON video(uploaded_by);
CREATE INDEX idx_lecture_content_video ON lecture_content(video_id);

-- 샘플 데이터 삽입
INSERT INTO video (id, title, platform, external_video_id, channel_id, embed_url, thumbnail_url, upload_status, is_live, live_status, scheduled_start_time, uploaded_by, created_at, updated_at)
VALUES 
    -- Vimeo 샘플 (VIDEO 타입용)
    ('11111111-1111-1111-1111-111111111111', 
     '물리학 1강 - 가속도 운동', 
     'VIMEO', 
     '123456789', 
     NULL, 
     'https://player.vimeo.com/video/123456789', 
     'https://i.vimeocdn.com/video/123456789.jpg', 
     'READY', 
     false, 
     NULL, 
     NULL, 
     '5d726309-0785-47e2-8f81-257d74401543', 
     NOW(), 
     NOW()),
     
    -- YouTube Live 샘플 (LIVE 타입용)
    ('22222222-2222-2222-2222-222222222222', 
     '물리학 라이브 수업 - 원운동', 
     'YOUTUBE_LIVE', 
     'dQw4w9WgXcQ', 
     'UCChannelId123', 
     'https://www.youtube.com/embed/dQw4w9WgXcQ', 
     'https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg', 
     'READY', 
     true, 
     'LIVE', 
     '2025-08-27 14:00:00', 
     '5d726309-0785-47e2-8f81-257d74401543', 
     NOW(), 
     NOW());

-- 기존 lecture_content와 새로운 video 연결 (샘플)
UPDATE lecture_content 
SET video_id = '11111111-1111-1111-1111-111111111111'
WHERE lecture_detail_id = '55555555-5555-5555-5555-555555555555'; -- VIDEO 타입 강의

UPDATE lecture_content 
SET video_id = '22222222-2222-2222-2222-222222222222'
WHERE lecture_detail_id = '66666666-6666-6666-6666-666666666666'; -- LIVE 타입 강의