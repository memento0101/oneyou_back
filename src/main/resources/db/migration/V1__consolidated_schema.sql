-- OneYou 통합 데이터베이스 스키마 (V1-V7 통합)

-- 확장 기능 (UUID 생성용)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- 사용자 관련 테이블
-- ============================================================================

-- 사용자 테이블 (V1) - 학생과 강사 통합
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    
    -- 학생 관련 필드 (role='STUDENT'일 때 사용)
    student_contact VARCHAR(20),
    parent_contact VARCHAR(20),
    address TEXT,
    goal_universities JSON,           -- 강사는 NULL 허용
    study_years INTEGER,
    major_type VARCHAR(10),           -- 예: "문과", "이과"
    eju_scores JSON,                 -- 강사는 NULL 허용
    note TEXT,
    
    -- 강사 관련 필드 (role='TEACHER'일 때 사용)
    image TEXT,                      -- 강사 프로필 이미지
    teaching_subjects JSON,          -- 강의 분야 (배열 형태로 저장)
    bank_name VARCHAR(100),          -- 은행명
    account_number VARCHAR(50),      -- 계좌번호
    account_holder VARCHAR(100),     -- 예금주
    business_number VARCHAR(20),     -- 사업자등록번호
    
    -- 공통 필드
    active BOOLEAN DEFAULT true,     -- 학생/강사 모두 활성화 상태 관리
    role VARCHAR(50) DEFAULT 'STUDENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 강의 관련 기본 테이블
-- ============================================================================

-- 교사 테이블 제거됨 - users 테이블로 통합

-- 교재 테이블 (V2) - lecture_id 외래키 추가 (1:N 관계)
CREATE TABLE textbook (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    description_img TEXT,
    img TEXT,
    link TEXT,
    price INTEGER NOT NULL,
    lecture_id UUID NOT NULL REFERENCES lecture(id), -- 강의 참조 (한 강의에 여러 교재)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 강의 테이블 (V2) - teacher_id를 user_id로 변경, textbook_id 제거
CREATE TABLE lecture (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    category TEXT,
    course TEXT,
    description TEXT,
    price INTEGER NOT NULL,
    teacher_id UUID REFERENCES users(id), -- users 테이블 참조 (role='TEACHER')
    period INTEGER,
    target TEXT,
    image TEXT,
    url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 비디오 관리 테이블 (V7)
-- ============================================================================

-- 비디오 테이블
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

-- ============================================================================
-- 강의 구조 테이블 (V4)
-- ============================================================================

-- 강의 대단원
CREATE TABLE lecture_chapter (
    id UUID PRIMARY KEY,
    lecture_id UUID NOT NULL REFERENCES lecture(id),
    title TEXT NOT NULL,
    chapter_order INTEGER,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- 강의 소단원
CREATE TABLE lecture_detail (
    id UUID PRIMARY KEY,
    lecture_chapter_id UUID NOT NULL REFERENCES lecture_chapter(id),
    title TEXT NOT NULL,
    type VARCHAR(20) NOT NULL, -- LIVE, VIDEO, QUIZ
    detail_order INTEGER,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- 강의 내용 (live, video) - V7에서 video_url 제거하고 video_id 추가
CREATE TABLE lecture_content (
    id UUID PRIMARY KEY,
    lecture_detail_id UUID NOT NULL REFERENCES lecture_detail(id),
    video_id UUID REFERENCES video(id) ON DELETE SET NULL,
    contents TEXT,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- 강의 퀴즈
CREATE TABLE lecture_quiz (
    id UUID PRIMARY KEY,
    lecture_detail_id UUID NOT NULL REFERENCES lecture_detail(id),
    question TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- 퀴즈 선택지
CREATE TABLE lecture_quiz_option (
    id UUID PRIMARY KEY,
    lecture_quiz_id UUID NOT NULL REFERENCES lecture_quiz(id),
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- ============================================================================
-- 학생 수강 관련 테이블 (V4)
-- ============================================================================

-- 학생 강의 등록
CREATE TABLE student_lecture (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    lecture_id UUID NOT NULL REFERENCES lecture(id),
    start_date TIMESTAMP NOT NULL,
    expire_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),

    UNIQUE (user_id, lecture_id) -- 한 유저당 동일 강의 중복 수강 방지
);

-- 학생 진행률
CREATE TABLE student_lecture_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    lecture_detail_id UUID NOT NULL REFERENCES lecture_detail(id),
    is_completed BOOLEAN DEFAULT false,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),

    UNIQUE (user_id, lecture_detail_id) -- 한 강의당 한 건만
);

-- 학생 과제 제출
CREATE TABLE student_review_submission (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    lecture_detail_id UUID NOT NULL REFERENCES lecture_detail(id),
    review_url TEXT NOT NULL,
    teacher_feedback TEXT,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),

    UNIQUE (user_id, lecture_detail_id)
);

-- ============================================================================
-- 리뷰 및 후기 테이블 (V6)
-- ============================================================================

-- 강의 후기 테이블
CREATE TABLE lecture_review (
    id UUID PRIMARY KEY,
    lecture_id UUID NOT NULL REFERENCES lecture(id),
    user_id UUID NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_anonymous BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    
    -- 한 유저당 동일 강의에 대해 하나의 후기만 작성 가능
    UNIQUE (user_id, lecture_id)
);

-- ============================================================================
-- 배너 관련 테이블 (V3)
-- ============================================================================

CREATE TABLE main_banner (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    image TEXT NOT NULL,
    url TEXT,
    banner_order INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 공지사항 테이블 (V6)
-- ============================================================================

CREATE TABLE notice (
    id UUID PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_important BOOLEAN DEFAULT false,
    view_count INTEGER DEFAULT 0,
    author_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- ============================================================================
-- 대학 정보 테이블 (University)
-- ============================================================================

CREATE TABLE university (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_image TEXT,
    university_type VARCHAR(20) NOT NULL CHECK (university_type IN ('NATIONAL', 'PRIVATE', 'MEDICAL')),  -- 국립/사립/의과대학 구분
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- ============================================================================
-- 합격 후기 테이블 (PassReview)
-- ============================================================================

CREATE TABLE pass_review (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,

    -- 4개 질문-답변 컬럼
    academy_selection_reason TEXT NOT NULL,      -- 학원선택이유
    satisfying_content_reason TEXT NOT NULL,     -- 만족스러웠던 콘텐츠와 이유
    study_method TEXT NOT NULL,                  -- 학습노하우
    advice_for_students TEXT NOT NULL,           -- 다른 수강생에게 조언

    -- 대학 정보 (외래키)
    university_id UUID NOT NULL REFERENCES university(id),

    -- 합격 학과
    major VARCHAR(100) NOT NULL,

    pass_year INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- ============================================================================
-- 강의 질문 및 답변 테이블
-- ============================================================================

-- 강의 질문 테이블
CREATE TABLE lecture_question (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lecture_id UUID NOT NULL REFERENCES lecture(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_answered BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- 강의 질문 답변 테이블
CREATE TABLE lecture_question_answer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES lecture_question(id) ON DELETE CASCADE,
    teacher_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    
    -- 하나의 질문에 대해 하나의 답변만 (선생님이 답변 수정 가능)
    UNIQUE (question_id)
);

-- ============================================================================
-- 인덱스 생성 (V6, V7)
-- ============================================================================

-- 강의 후기 인덱스
CREATE INDEX idx_lecture_review_lecture_id ON lecture_review(lecture_id);
CREATE INDEX idx_lecture_review_user_id ON lecture_review(user_id);
CREATE INDEX idx_lecture_review_rating ON lecture_review(rating);
CREATE INDEX idx_lecture_review_created_at ON lecture_review(created_at DESC);

-- 공지사항 인덱스
CREATE INDEX idx_notice_important ON notice(is_important);
CREATE INDEX idx_notice_created_at ON notice(created_at DESC);
CREATE INDEX idx_notice_author_id ON notice(author_id);

-- 비디오 인덱스
CREATE INDEX idx_video_platform_status ON video(platform, upload_status);
CREATE INDEX idx_video_live_status ON video(live_status) WHERE platform = 'YOUTUBE_LIVE';
CREATE INDEX idx_video_uploaded_by ON video(uploaded_by);
CREATE INDEX idx_lecture_content_video ON lecture_content(video_id);

-- 강의 질문 인덱스
CREATE INDEX idx_lecture_question_lecture_id ON lecture_question(lecture_id);
CREATE INDEX idx_lecture_question_student_id ON lecture_question(student_id);
CREATE INDEX idx_lecture_question_answered ON lecture_question(is_answered);
CREATE INDEX idx_lecture_question_created_at ON lecture_question(created_at DESC);
CREATE INDEX idx_lecture_question_answer_question_id ON lecture_question_answer(question_id);
CREATE INDEX idx_lecture_question_answer_teacher_id ON lecture_question_answer(teacher_id);

-- ============================================================================
-- 초기 샘플 데이터
-- ============================================================================

-- 샘플 사용자 데이터 (학생 + 강사)
-- 학생 데이터
INSERT INTO users (
    id, user_id, password, name, student_contact, parent_contact, address,
    goal_universities, study_years, major_type, eju_scores, note, active, role
) VALUES (
    '5d726309-0785-47e2-8f81-257d74401543',
    'test@test.com',
    '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO',
    '홍길동',
    '010-1234-5678',
    '010-1111-2222',
    '서울시 강남구 테헤란로 123',
    '[
      {"school": "도쿄대", "major": "의학부"},
      {"school": "오사카대", "major": "경제학부"},
      {"school": "와세다대", "major": "법학부"}
    ]',
    2,
    '이과',
    '{
      "listening": 45,
      "reading": 60,
      "total_listen_read": 105,
      "science": 70,
      "subject1": "물리",
      "score1": 80,
      "subject2": "화학",
      "score2": 85
    }',
    '조용한 환경에서 공부하고 싶습니다.',
    true,
    'STUDENT'
);

-- 강사 데이터
INSERT INTO users (id, user_id, password, name, image, active, role, created_at, updated_at)
VALUES 
    ('22222222-2222-2222-2222-222222222222', 'teacher@teacher.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '빵구리', 'https://example.com/teacher/profile.jpg', true, 'TEACHER', now(), now()),
    ('11111111-1111-1111-1111-111111111111', 'teacher2@teacher2.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '정구리', 'image', true, 'TEACHER', now(), now());

-- 강의 데이터
INSERT INTO lecture (id, title, category, course, description, price, teacher_id, period, target, image, url, created_at, updated_at)
VALUES
    ('33333333-3333-3333-3333-333333333333', 'EJU 종합 강의', '일본유학', 'EJU', 'EJU 시험 준비를 위한 종합 강의입니다.', 120000,
     '22222222-2222-2222-2222-222222222222', 30, '일본 유학 준비생',
     'https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.naver.com%2Fsssss747%2F221675232598%3FviewType%3Dpc&psig=AOvVaw2nB1rY4dZMJGXnog-lwtPL&ust=1749127311911000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq9prl140DFQAAAAAdAAAAABAE',
     'https://www.google.com/imgres?q=%ED%8E%98%ED%8E%98%EC%A7%A4&imgurl=https%3A%2F%2Fd2u3dcdbebyaiu.cloudfront.net%2Fuploads%2Fatch_img%2F38%2Fcc5a526bf63046da3ed3123f55f5c1ca_res.jpeg&imgrefurl=https%3A%2F%2Fwww.teamblind.com%2Fkr%2Fpost%2F%25EC%25A0%259C%25EC%259D%25BC-%25EC%25A2%258B%25EC%2595%2584%25ED%2595%2598%25EB%258A%2594-%25ED%258E%2598%25ED%258E%2598-%25EC%25A7%25A4-L75GCCmK&docid=z3qvv46_xmQpHM&tbnid=-WUel9QwIpAQUM&vet=12ahUKEwjw6cCR5deNAxWgVPUHHU-FDWYQM3oECHEQAA..i&w=530&h=492&hcb=2&ved=2ahUKEwjw6cCR5deNAxWgVPUHHU-FDWYQM3oECHEQAA',
     now(), now()),
    ('22222222-1111-1111-1111-111111111111', '물리 강의', '물리', '물리', '물리짱', 150000,
     '11111111-1111-1111-1111-111111111111', 90, '물리바보', 'image', null, DEFAULT, DEFAULT),
    ('33333333-1111-1111-1111-111111111111', '화학 강의', '화학', '화학', '화학짱', 200000,
     '11111111-1111-1111-1111-111111111111', 90, '화학바보', 'image', null, DEFAULT, DEFAULT);


-- 비디오 데이터
INSERT INTO video (id, title, platform, external_video_id, channel_id, embed_url, thumbnail_url, upload_status, is_live, live_status, scheduled_start_time, uploaded_by, created_at, updated_at)
VALUES 
    -- Vimeo 샘플 (VIDEO 타입용)
    ('11111111-1111-1111-1111-111111111111', '물리학 1강 - 가속도 운동', 'VIMEO', '123456789', NULL, 
     'https://player.vimeo.com/video/123456789', 'https://i.vimeocdn.com/video/123456789.jpg', 'READY', 
     false, NULL, NULL, '5d726309-0785-47e2-8f81-257d74401543', NOW(), NOW()),
     
    -- YouTube Live 샘플 (LIVE 타입용)
    ('22222222-2222-2222-2222-222222222222', '물리학 라이브 수업 - 원운동', 'YOUTUBE_LIVE', 'dQw4w9WgXcQ', 'UCChannelId123', 
     'https://www.youtube.com/embed/dQw4w9WgXcQ', 'https://img.youtube.com/vi/dQw4w9WgXcQ/maxresdefault.jpg', 'READY', 
     true, 'LIVE', '2025-08-27 14:00:00', '5d726309-0785-47e2-8f81-257d74401543', NOW(), NOW());

-- 강의 구조 데이터
INSERT INTO lecture_chapter (id, lecture_id, title, chapter_order, created_at, updated_at)
VALUES ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '역학', 1, NOW(), NOW());

INSERT INTO lecture_detail (id, lecture_chapter_id, title, type, detail_order, created_at, updated_at)
VALUES
    ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', '가속도 운동', 'VIDEO', 1, NOW(), NOW()),
    ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '원운동', 'LIVE', 2, NOW(), NOW()),
    ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', '1단원 퀴즈', 'QUIZ', 3, NOW(), NOW());

INSERT INTO lecture_content (id, lecture_detail_id, video_id, contents, created_at, updated_at)
VALUES
    ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', '가속도 운동 내용입니다.', NOW(), NOW()),
    ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '원운동 라이브 수업입니다.', NOW(), NOW());

INSERT INTO lecture_quiz (id, lecture_detail_id, question, created_at, updated_at)
VALUES 
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777', 
     '다음 중 속력의 단위가 아닌 것은?', NOW(), NOW()),
    ('bbbbaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777',
     '뉴턴의 제1법칙(관성의 법칙)에 대한 설명으로 옳은 것은?', NOW(), NOW()),
    ('ccccaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777',
     '가속도의 단위는 무엇인가?', NOW(), NOW()),
    ('ddddaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777',
     '다음 중 벡터량이 아닌 것은?', NOW(), NOW()),
    ('eeeeaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777',
     '등속도 운동에서 가속도는 얼마인가?', NOW(), NOW());

INSERT INTO lecture_quiz_option (id, lecture_quiz_id, option_text, is_correct)
VALUES
    -- 1번 문제: 속력의 단위
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s', false),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'km/h', false),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s^2', false),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'kg', true),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'cm/s', false),
    
    -- 2번 문제: 뉴턴의 제1법칙
    ('11111111-1111-1111-1111-111111111111', 'bbbbaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '힘이 작용하면 물체는 반드시 운동한다', false),
    ('22222222-2222-2222-2222-222222222222', 'bbbbaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '외력이 0이면 물체는 등속직선운동을 한다', true),
    ('33333333-3333-3333-3333-333333333333', 'bbbbaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '가속도는 힘에 비례한다', false),
    ('44444444-4444-4444-4444-444444444444', 'bbbbaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '작용과 반작용은 크기가 같다', false),
    
    -- 3번 문제: 가속도의 단위
    ('55555555-5555-5555-5555-555555555555', 'ccccaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s', false),
    ('66666666-6666-6666-6666-666666666666', 'ccccaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s^2', true),
    ('77777777-7777-7777-7777-777777777777', 'ccccaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm^2/s', false),
    ('88888888-8888-8888-8888-888888888888', 'ccccaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'N', false),
    
    -- 4번 문제: 벡터량이 아닌 것
    ('99999999-9999-9999-9999-999999999999', 'ddddaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '속도', false),
    ('aaaabbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'ddddaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '변위', false),
    ('bbbbcccc-cccc-cccc-cccc-cccccccccccc', 'ddddaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '속력', true),
    ('ccccdddd-dddd-dddd-dddd-dddddddddddd', 'ddddaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '가속도', false),
    
    -- 5번 문제: 등속도 운동의 가속도
    ('ddddeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'eeeeaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '0', true),
    ('eeeeffff-ffff-ffff-ffff-ffffffffffff', 'eeeeaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '1', false),
    ('ffff1111-1111-1111-1111-111111111111', 'eeeeaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '속도와 같다', false),
    ('11112222-2222-2222-2222-222222222222', 'eeeeaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '시간에 비례한다', false);

-- 학생 수강 및 진행 데이터
INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at)
VALUES
    ('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '33333333-3333-3333-3333-333333333333',
     '2025-08-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('bbbbbbbb-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '22222222-1111-1111-1111-111111111111',
     '2025-08-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('cccccccc-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '33333333-1111-1111-1111-111111111111',
     '2025-08-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW());

INSERT INTO student_lecture_progress (id, user_id, lecture_detail_id, is_completed, completed_at, created_at, updated_at)
VALUES ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '5d726309-0785-47e2-8f81-257d74401543', '55555555-5555-5555-5555-555555555555', true, NOW(), NOW(), NOW());

INSERT INTO student_review_submission (id, user_id, lecture_detail_id, review_url, teacher_feedback, created_at, updated_at)
VALUES ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', '5d726309-0785-47e2-8f81-257d74401543', '55555555-5555-5555-5555-555555555555', 
        'https://review-url.com', '복습을 잘 했습니다.', NOW(), NOW());

-- 강의 질문 및 답변 샘플 데이터
INSERT INTO lecture_question (id, lecture_id, student_id, title, content, is_answered, created_at, updated_at)
VALUES 
    -- 답변 완료된 질문들
    ('12345678-1111-1111-1111-123456789abc', '33333333-3333-3333-3333-333333333333', '5d726309-0785-47e2-8f81-257d74401543', 
     'EJU 수학 문제 질문', 'EJU 수학에서 벡터의 내적과 외적의 차이점이 무엇인가요? 실제 문제에서 어떻게 구분해서 사용하는지 궁금합니다.', 
     true, '2025-08-25 10:30:00', NOW()),
    
    ('12345678-2222-2222-2222-123456789abc', '33333333-3333-3333-3333-333333333333', '5d726309-0785-47e2-8f81-257d74401543',
     '물리 가속도 공식 질문', '등가속도 운동에서 s = v₀t + ½at² 공식을 유도하는 과정을 자세히 설명해주세요.', 
     true, '2025-08-26 14:20:00', NOW()),
    
    -- 답변 대기중인 질문들  
    ('12345678-3333-3333-3333-123456789abc', '22222222-1111-1111-1111-111111111111', '5d726309-0785-47e2-8f81-257d74401543',
     '화학 반응 속도론', '화학 반응에서 온도가 10도 올라갈 때마다 반응 속도가 2배씩 빨라진다고 하는데, 이것의 과학적 원리는 무엇인가요?', 
     false, '2025-08-27 09:15:00', NOW()),
     
    ('12345678-4444-4444-4444-123456789abc', '33333333-1111-1111-1111-111111111111', '5d726309-0785-47e2-8f81-257d74401543',
     'EJU 일본어 문법', 'けれども와 だが의 차이점과 각각 어떤 상황에서 사용하는지 알고 싶습니다.', 
     false, '2025-08-27 16:45:00', NOW()),
     
    ('12345678-5555-5555-5555-123456789abc', '33333333-3333-3333-3333-333333333333', '5d726309-0785-47e2-8f81-257d74401543',
     '종합과목 문제 풀이', '종합과목에서 역사와 지리가 어떤 비율로 출제되는지, 그리고 각 영역별 효율적인 공부 방법을 알려주세요.', 
     false, '2025-08-28 11:30:00', NOW());

-- 강의 질문 답변 샘플 데이터
INSERT INTO lecture_question_answer (id, question_id, teacher_id, content, created_at, updated_at)
VALUES 
    ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '12345678-1111-1111-1111-123456789abc', '22222222-2222-2222-2222-222222222222',
     '좋은 질문입니다! 벡터의 내적과 외적의 차이를 설명드리겠습니다.

**내적(스칼라곱, Dot Product):**
- 결과: 스칼라 값 (크기만 있음)
- 공식: A⃗ · B⃗ = |A||B|cosθ
- 의미: 한 벡터가 다른 벡터 방향으로의 "투영"을 나타냄
- 활용: 일(Work) 계산, 각도 구하기, 수직 판별

**외적(벡터곱, Cross Product):**
- 결과: 벡터 값 (크기와 방향 있음)  
- 공식: |A⃗ × B⃗| = |A||B|sinθ
- 의미: 두 벡터에 수직인 새로운 벡터 생성
- 활용: 토크(Torque) 계산, 평행사변형 넓이

EJU에서는 주로 내적이 출제되니 내적 위주로 연습하세요!', '2025-08-25 15:45:00', NOW()),
    
    ('aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', '12345678-2222-2222-2222-123456789abc', '22222222-2222-2222-2222-222222222222',
     '등가속도 운동 공식 유도 과정을 단계별로 설명해드리겠습니다.

**주어진 조건:**
- 초기속도: v₀
- 가속도: a (일정)
- 시간: t

**단계별 유도:**

1) **속도 공식부터 시작**
   v = v₀ + at ... (1)

2) **평균속도 개념 적용**
   등가속도 운동에서 평균속도 = (초기속도 + 최종속도) ÷ 2
   v̄ = (v₀ + v) ÷ 2 = (v₀ + v₀ + at) ÷ 2 = v₀ + ½at ... (2)

3) **거리는 평균속도 × 시간**
   s = v̄ × t = (v₀ + ½at) × t = v₀t + ½at²

따라서 **s = v₀t + ½at²**가 유도됩니다!

이 공식은 EJU에서 매우 자주 출제되니 꼭 기억해두세요.', '2025-08-26 18:30:00', NOW());

-- ============================================================================
-- 선생님 피드백 API 테스트 데이터
-- ============================================================================

-- 추가 선생님 계정 (기존에 22222222-2222-2222-2222-222222222222가 있으므로 다른 ID 사용)
INSERT INTO users (id, user_id, password, name, role, active, created_at, updated_at)
VALUES 
    ('eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 'teacher1@test.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '뿡구리', 'TEACHER', true, NOW(), NOW()),
    ('ffffffff-1111-1111-1111-ffffffffffff', 'teacher2@test.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '짱구리', 'TEACHER', true, NOW(), NOW());

-- 추가 학생 계정
INSERT INTO users (id, user_id, password, name, role, student_contact, active, created_at, updated_at)
VALUES 
    ('aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'student1@test.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '김학생', 'STUDENT', '010-1111-1111', true, NOW(), NOW()),
    ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'student2@test.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '이학생', 'STUDENT', '010-2222-2222', true, NOW(), NOW()),
    ('cccccccc-2222-2222-2222-cccccccccccc', 'student3@test.com', '$2a$10$bm0E.N10OLa0NDBSCxYhZeoLo5VSPxnCSN9nWWQRlPOP9V1CFtpRO', '박학생', 'STUDENT', '010-3333-3333', true, NOW(), NOW());

-- 김선생님의 추가 강의
INSERT INTO lecture (id, title, category, course, description, price, teacher_id, period, target, image, url, created_at, updated_at)
VALUES
    ('dddddddd-1111-1111-1111-dddddddddddd', 'EJU 수학 심화', '수학', '수학', '수학 심화 과정', 180000, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 120, '수학 고득점 목표', 'image', null, NOW(), NOW()),
    ('eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 'EJU 종합과목', '종합과목', '종합과목', '종합과목 기초', 160000, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', 100, '종합과목 기초', 'image', null, NOW(), NOW());

-- 박선생님의 강의
INSERT INTO lecture (id, title, category, course, description, price, teacher_id, period, target, image, url, created_at, updated_at)
VALUES
    ('ffffffff-1111-1111-1111-ffffffffffff', 'EJU 일본어', '일본어', '일본어', '일본어 기초부터 고급까지', 140000, 'ffffffff-1111-1111-1111-ffffffffffff', 80, '일본어 초급자', 'image', null, NOW(), NOW());

-- 추가 강의 챕터
INSERT INTO lecture_chapter (id, lecture_id, title, chapter_order, created_at, updated_at)
VALUES
    ('dddddddd-2222-2222-2222-dddddddddddd', 'dddddddd-1111-1111-1111-dddddddddddd', '미분과 적분', 1, NOW(), NOW()),
    ('eeeeeeee-2222-2222-2222-eeeeeeeeeeee', 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', '일본 근현대사', 1, NOW(), NOW()),
    ('ffffffff-2222-2222-2222-ffffffffffff', 'ffffffff-1111-1111-1111-ffffffffffff', '기초 문법', 1, NOW(), NOW());

-- 추가 강의 세부 내용
INSERT INTO lecture_detail (id, lecture_chapter_id, title, type, detail_order, created_at, updated_at)
VALUES
    ('dddddddd-3333-3333-3333-dddddddddddd', 'dddddddd-2222-2222-2222-dddddddddddd', '미분의 기본 개념', 'VIDEO', 1, NOW(), NOW()),
    ('eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 'dddddddd-2222-2222-2222-dddddddddddd', '적분의 활용', 'LIVE', 2, NOW(), NOW()),
    ('ffffffff-3333-3333-3333-ffffffffffff', 'eeeeeeee-2222-2222-2222-eeeeeeeeeeee', '메이지 유신', 'VIDEO', 1, NOW(), NOW()),
    ('aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', 'ffffffff-2222-2222-2222-ffffffffffff', '조사와 조동사', 'LIVE', 1, NOW(), NOW());

-- 피드백 대기 중인 학생 제출물 (teacher_feedback이 null)
INSERT INTO student_review_submission (id, user_id, lecture_detail_id, review_url, teacher_feedback, created_at, updated_at)
VALUES 
    -- 김선생님 강의 - EJU 수학 심화
    ('dddddddd-4444-4444-4444-dddddddddddd', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'dddddddd-3333-3333-3333-dddddddddddd', 'https://youtube.com/watch?v=math_diff_1', null, '2025-09-16 14:30:00', NOW()),
    ('eeeeeeee-4444-4444-4444-eeeeeeeeeeee', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'dddddddd-3333-3333-3333-dddddddddddd', 'https://youtube.com/watch?v=math_diff_2', null, '2025-09-16 15:45:00', NOW()),
    ('ffffffff-4444-4444-4444-ffffffffffff', 'cccccccc-2222-2222-2222-cccccccccccc', 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 'https://youtube.com/watch?v=math_integ_1', null, '2025-09-17 09:20:00', NOW()),
    
    -- 김선생님 강의 - EJU 종합과목
    ('aaaaaaaa-4444-4444-4444-aaaaaaaaaaaa', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'ffffffff-3333-3333-3333-ffffffffffff', 'https://youtube.com/watch?v=history_meiji_1', null, '2025-09-17 11:15:00', NOW()),
    
    -- 박선생님 강의 - EJU 일본어
    ('bbbbbbbb-4444-4444-4444-bbbbbbbbbbbb', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', 'https://youtube.com/watch?v=japanese_grammar_1', null, '2025-09-17 13:40:00', NOW()),
    ('cccccccc-4444-4444-4444-cccccccccccc', 'cccccccc-2222-2222-2222-cccccccccccc', 'aaaaaaaa-3333-3333-3333-aaaaaaaaaaaa', 'https://youtube.com/watch?v=japanese_grammar_2', null, '2025-09-17 16:25:00', NOW());

-- 이미 피드백이 완료된 제출물 (비교용)
INSERT INTO student_review_submission (id, user_id, lecture_detail_id, review_url, teacher_feedback, created_at, updated_at)
VALUES 
    ('dddddddd-5555-5555-5555-dddddddddddd', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'eeeeeeee-3333-3333-3333-eeeeeeeeeeee', 'https://youtube.com/watch?v=completed_review', '잘 정리하였습니다. 다음 단계로 진행하세요.', '2025-09-15 10:00:00', NOW());

-- 학생 강의 수강 등록
INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at)
VALUES
    ('dddddddd-6666-6666-6666-dddddddddddd', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'dddddddd-1111-1111-1111-dddddddddddd', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('eeeeeeee-6666-6666-6666-eeeeeeeeeeee', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'dddddddd-1111-1111-1111-dddddddddddd', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('ffffffff-6666-6666-6666-ffffffffffff', 'cccccccc-2222-2222-2222-cccccccccccc', 'dddddddd-1111-1111-1111-dddddddddddd', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('aaaaaaaa-6666-6666-6666-aaaaaaaaaaaa', 'aaaaaaaa-2222-2222-2222-aaaaaaaaaaaa', 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('bbbbbbbb-6666-6666-6666-bbbbbbbbbbbb', 'bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb', 'ffffffff-1111-1111-1111-ffffffffffff', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW()),
    ('cccccccc-6666-6666-6666-cccccccccccc', 'cccccccc-2222-2222-2222-cccccccccccc', 'ffffffff-1111-1111-1111-ffffffffffff', '2025-09-01 00:00:00', '2025-12-01 23:59:59', NOW(), NOW());

-- ============================================================================
-- 교재 데이터 (모든 강의 생성 후)
-- ============================================================================

-- 교재 데이터
INSERT INTO textbook (id, name, description, description_img, img, link, price, lecture_id, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'EJU 종합 교재', 'EJU 시험 준비를 위한 종합 교재입니다.',
     'https://ichef.bbci.co.uk/ace/standard/976/cpsprodpb/16620/production/_91408619_55df76d5-2245-41c1-8031-07a4da3f313f.jpg',
     'https://mblogthumb-phinf.pstatic.net/MjAyMTEwMDVfMjkz/MDAxNjMzNDE5NDM5MzY1.C69FSduuaiTt9LkMykKzsMu2YpWQk50LHninjXFSbNcg.yvzNU4LUEaHd-5VKTgzzfkm8kuXikMnE1VFtm4gj7-Ag.JPEG.parkamsterdam/IMG_3467.JPG?type=w800',
     'https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.naver.com%2Fsssss747%2F221675232598%3FviewType%3Dpc&psig=AOvVaw2nB1rY4dZMJGXnog-lwtPL&ust=1749127311911000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq9prl140DFQAAAAAdAAAAABAE',
     25000, '33333333-3333-3333-3333-333333333333', now(), now()),
    ('22222222-1111-1111-1111-111111111111', '물리 교재', '물리 교재 짱', 'image', 'image', 'link', 30000, '22222222-1111-1111-1111-111111111111', now(), now()),
    ('33333333-1111-1111-1111-111111111111', '화학 교재', '화학 교재 짱', 'image', 'image', 'link', 30000, '33333333-1111-1111-1111-111111111111', now(), now()),
    ('dddddddd-1111-1111-1111-111111111111', 'EJU 수학 심화 교재', '수학 심화 과정을 위한 전문 교재', 'image', 'image', 'link', 40000, 'dddddddd-1111-1111-1111-dddddddddddd', now(), now()),
    ('eeeeeeee-1111-1111-1111-111111111111', 'EJU 종합과목 교재', '종합과목 기초 교재', 'image', 'image', 'link', 35000, 'eeeeeeee-1111-1111-1111-eeeeeeeeeeee', now(), now()),
    ('ffffffff-1111-1111-1111-111111111111', 'EJU 일본어 교재', '일본어 기초부터 고급까지', 'image', 'image', 'link', 32000, 'ffffffff-1111-1111-1111-ffffffffffff', now(), now());
