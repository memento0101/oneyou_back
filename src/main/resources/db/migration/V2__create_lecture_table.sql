-- 필요 테이블
-- 강의 테이블 (전반적인 강의 정보)
-- 강의 detail 테이블 (이거는 혁준상이랑 상의하기)
-- 교재 테이블 (교재 가격이랑 , 다운링크 등등)
-- 수강평 테이블 (어떤 강의에 대한 수강평인지 필요)

CREATE TABLE teacher (
                         id UUID PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         active BOOLEAN DEFAULT true,
                         image TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE textbook (
                          id UUID PRIMARY KEY,
                          name TEXT NOT NULL,
                          description TEXT,
                          description_img TEXT,
                          img TEXT,
                          link TEXT,
                          price INTEGER NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lecture (
                         id UUID PRIMARY KEY,
                         title TEXT NOT NULL,
                         category TEXT,
                         course TEXT,
                         description TEXT,
                         price INTEGER NOT NULL,
                         textbook_id UUID REFERENCES textbook(id),
                         teacher_id UUID REFERENCES teacher(id),
                         period INTEGER,
                         target TEXT,
                         image TEXT,
                         url TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review (
                        id UUID PRIMARY KEY,
                        lecture_id UUID NOT NULL,
                        contents TEXT NOT NULL,
                        score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
                        user_id UUID NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                        CONSTRAINT fk_review_lecture FOREIGN KEY (lecture_id) REFERENCES lecture(id),
                        CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO textbook (id, name, description, description_img, img, link, price, created_at, updated_at)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'EJU 종합 교재',
           'EJU 시험 준비를 위한 종합 교재입니다.',
           'https://ichef.bbci.co.uk/ace/standard/976/cpsprodpb/16620/production/_91408619_55df76d5-2245-41c1-8031-07a4da3f313f.jpg',
           'https://mblogthumb-phinf.pstatic.net/MjAyMTEwMDVfMjkz/MDAxNjMzNDE5NDM5MzY1.C69FSduuaiTt9LkMykKzsMu2YpWQk50LHninjXFSbNcg.yvzNU4LUEaHd-5VKTgzzfkm8kuXikMnE1VFtm4gj7-Ag.JPEG.parkamsterdam/IMG_3467.JPG?type=w800',
           'https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.naver.com%2Fsssss747%2F221675232598%3FviewType%3Dpc&psig=AOvVaw2nB1rY4dZMJGXnog-lwtPL&ust=1749127311911000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq9prl140DFQAAAAAdAAAAABAE',
            25000,
           now(),
           now()
       );

INSERT INTO teacher (id, name, active, image, created_at, updated_at)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           '김선생',
           true,
           'https://example.com/teacher/profile.jpg',
           now(),
           now()
       );

INSERT INTO lecture (
    id, title, category, course, description, price, textbook_id, teacher_id, period, target, image, url, created_at, updated_at
)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           'EJU 종합 강의',
           '일본유학',
           'EJU',
           'EJU 시험 준비를 위한 종합 강의입니다.',
           120000,
           '11111111-1111-1111-1111-111111111111', -- textbook FK
           '22222222-2222-2222-2222-222222222222', -- teacher FK
           30,
           '일본 유학 준비생',
           'https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.naver.com%2Fsssss747%2F221675232598%3FviewType%3Dpc&psig=AOvVaw2nB1rY4dZMJGXnog-lwtPL&ust=1749127311911000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCICq9prl140DFQAAAAAdAAAAABAE',
           'https://www.google.com/imgres?q=%ED%8E%98%ED%8E%98%EC%A7%A4&imgurl=https%3A%2F%2Fd2u3dcdbebyaiu.cloudfront.net%2Fuploads%2Fatch_img%2F38%2Fcc5a526bf63046da3ed3123f55f5c1ca_res.jpeg&imgrefurl=https%3A%2F%2Fwww.teamblind.com%2Fkr%2Fpost%2F%25EC%25A0%259C%25EC%259D%25BC-%25EC%25A2%258B%25EC%2595%2584%25ED%2595%2598%25EB%258A%2594-%25ED%258E%2598%25ED%258E%2598-%25EC%25A7%25A4-L75GCCmK&docid=z3qvv46_xmQpHM&tbnid=-WUel9QwIpAQUM&vet=12ahUKEwjw6cCR5deNAxWgVPUHHU-FDWYQM3oECHEQAA..i&w=530&h=492&hcb=2&ved=2ahUKEwjw6cCR5deNAxWgVPUHHU-FDWYQM3oECHEQAA',
           now(),
           now()
       );

INSERT INTO review (id, lecture_id, contents, score, user_id, created_at, updated_at) VALUES
    ('a7f36e7e-44a1-4c67-bd94-1c5aa1d61f01', '33333333-3333-3333-3333-333333333333', '정말 유익한 강의였습니다. 실무에 많은 도움이 되었어요.', 5, '5d726309-0785-47e2-8f81-257d74401543', NOW(), NOW());