-- src/main/resources/db/migration/V1__create_users_table.sql

-- 확장 기능 (UUID 생성용)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       user_id VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       student_contact VARCHAR(20),
                       parent_contact VARCHAR(20),
                       address TEXT,
                       goal_universities JSON NOT NULL,   -- JSON으로 저장, 순서 유지
                       study_years INTEGER,
                       major_type VARCHAR(10),            -- 예: "문과", "이과"
                       eju_scores JSON NOT NULL,          -- EJU 점수 JSON 구조
                       note TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 샘플 데이터 삽입
INSERT INTO users (
    id, user_id, password, name, student_contact, parent_contact, address,
    goal_universities, study_years, major_type, eju_scores, note
) VALUES (
             gen_random_uuid(),
             'student01@example.com',
             '$2a$10$examplehashedpassword',  -- BCrypt 암호 예시
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
             '조용한 환경에서 공부하고 싶습니다.'
         );
