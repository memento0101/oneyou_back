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

-- 강의 내용 (live, video)
CREATE TABLE lecture_content (
                                 id UUID PRIMARY KEY,
                                 lecture_detail_id UUID NOT NULL REFERENCES lecture_detail(id),
                                 video_url TEXT,
                                 contents TEXT,
                                 created_at TIMESTAMP DEFAULT now(),
                                 updated_at TIMESTAMP DEFAULT now()
);

-- 강의 내용 (quiz)
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

-- 학생 제출용
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

INSERT INTO lecture_chapter (id, lecture_id, title, chapter_order, created_at, updated_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', '역학', 1, NOW(), NOW());


INSERT INTO lecture_detail (id, lecture_chapter_id, title, type, detail_order, created_at, updated_at)
VALUES
    ('55555555-5555-5555-5555-555555555555', '44444444-4444-4444-4444-444444444444', '가속도 운동', 'VIDEO', 1, NOW(), NOW()),
    ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', '원운동', 'LIVE', 2, NOW(), NOW()),
    ('77777777-7777-7777-7777-7777-77777777', '44444444-4444-4444-4444-444444444444', '1단원 퀴즈', 'QUIZ', 3, NOW(), NOW());

INSERT INTO lecture_content (id, lecture_detail_id, video_url, contents, created_at, updated_at)
VALUES
    ('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', 'https://vimeo.com/video1', '가속도 운동 내용입니다.', NOW(), NOW()),
    ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', 'https://youtube.com/live1', '원운동 라이브 수업입니다.', NOW(), NOW());

INSERT INTO lecture_quiz (id, lecture_detail_id, question, created_at, updated_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '77777777-7777-7777-7777-777777777777', '다음 중 속력의 단위가 아닌 것은?', NOW(), NOW());

INSERT INTO lecture_quiz_option (id, lecture_quiz_id, option_text, is_correct)
VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s', false),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'km/h', false),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'm/s^2', false),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'kg', true),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'cm/s', false);

INSERT INTO student_lecture_progress (id, user_id, lecture_detail_id, is_completed,completed_at, created_at, updated_at)
VALUES
    ('aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa', '5d726309-0785-47e2-8f81-257d74401543', '55555555-5555-5555-5555-555555555555', true,NOW(), NOW(), NOW());

INSERT INTO student_review_submission (id, user_id, lecture_detail_id, review_url, teacher_feedback, created_at, updated_at)
VALUES ('bbbbbbbb-2222-2222-2222-bbbbbbbbbbbb','5d726309-0785-47e2-8f81-257d74401543', '55555555-5555-5555-5555-555555555555',  'https://review-url.com', '복습을 잘 했습니다.', NOW(), NOW());


INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at)
VALUES
    ('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '33333333-3333-3333-3333-333333333333',
     '2025-06-01 00:00:00', '2025-08-01 23:59:59', NOW(), NOW());

INSERT INTO teacher (id, name, active, image, created_at, updated_at) VALUES ('11111111-1111-1111-1111-111111111111', '정구리', true, 'image', '2025-06-30 16:09:38.000000', '2025-06-30 16:09:41.000000');

INSERT INTO textbook (id, name, description, description_img, img, link, price, created_at, updated_at) VALUES ('22222222-1111-1111-1111-111111111111', '물리 강의', '물리 강의 짱', 'image', 'image', 'link', 30000, '2025-06-30 16:13:49.000000', '2025-06-30 16:13:52.000000');
INSERT INTO textbook (id, name, description, description_img, img, link, price, created_at, updated_at) VALUES ('33333333-1111-1111-1111-111111111111', '화학 강의', '화학 강의 짱', 'image', 'image', 'link', 30000, '2025-06-30 16:13:49.000000', '2025-06-30 16:13:52.000000');

INSERT INTO lecture (id, title, category, course, description, price, textbook_id, teacher_id, period, target, image, url, created_at, updated_at) VALUES ('22222222-1111-1111-1111-111111111111', '물리 강의', '물리', '물리', '물리짱', 150000, '22222222-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 90, '물리바보', 'image', null, DEFAULT, DEFAULT);
INSERT INTO lecture (id, title, category, course, description, price, textbook_id, teacher_id, period, target, image, url, created_at, updated_at) VALUES ('33333333-1111-1111-1111-111111111111', '화학 강의', '화학', '화학', '화학짱', 200000, '33333333-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 90, '화학바보', 'image', null, DEFAULT, DEFAULT);


INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at)
VALUES
    ('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '33333333-3333-3333-3333-333333333333',
    '2025-06-01 00:00:00', '2025-08-01 23:59:59', NOW(), NOW());

INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at) VALUES ('bbbbbbbb-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '22222222-1111-1111-1111-111111111111', '2025-06-01 00:00:00.000000', '2025-08-01 23:59:59.000000', '2025-06-30 16:44:43.000000', '2025-06-30 16:44:44.000000');

INSERT INTO student_lecture (id, user_id, lecture_id, start_date, expire_date, created_at, updated_at)
VALUES
    ('cccccccc-bbbb-cccc-dddd-eeeeeeeeeeee', '5d726309-0785-47e2-8f81-257d74401543', '33333333-1111-1111-1111-111111111111',
     '2025-06-01 00:00:00', '2025-08-01 23:59:59', NOW(), NOW());

