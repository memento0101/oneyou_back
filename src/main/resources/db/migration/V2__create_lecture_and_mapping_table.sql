-- create lecture_category
CREATE TABLE IF NOT EXISTS lecture_category(
          id UUID PRIMARY KEY,               -- 카테고리 ID
          name VARCHAR(100) NOT NULL UNIQUE, -- 카테고리 이름
          description TEXT,                  -- 카테고리 설명 (옵션)
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create Lecture tabel
create table IF NOT EXISTS lecture(
         id UUID PRIMARY KEY,                -- 강의 고유 ID
         category_id UUID,                   -- 카테고리 ID (FK)
         title VARCHAR(255) NOT NULL,        -- 강의 제목
         instructor_name VARCHAR(100) NOT NULL, -- 강사 이름
         description TEXT,                   -- 강의 설명
         duration_minutes INT,               -- 강의 전체 시간 (분 단위)
         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 수정일

        CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES lecture_category (id) ON DELETE SET NULL
);

-- create user_lecture_mapping
CREATE TABLE IF NOT EXISTS user_lecture_mapping(
          id UUID PRIMARY KEY,                  -- 매핑 고유 ID
          user_id UUID NOT NULL,                -- 학생 ID (users 테이블과 연관)
          lecture_id UUID NOT NULL,             -- 강의 ID (lecture 테이블과 연관)
          progress INT DEFAULT 0,               -- 강의 진행도 (0 ~ 100, 백분율)
          start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 수강 시작일
          end_date TIMESTAMP,                   -- 수강 종료일 (유효기간)
          is_active BOOLEAN DEFAULT true,       -- 수강 여부 (true: 진행 중, false: 만료)
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 매핑 생성일

          CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
          CONSTRAINT fk_lecture FOREIGN KEY (lecture_id) REFERENCES lecture (id) ON DELETE CASCADE
);

-- 카테고리 데이터 삽입
INSERT INTO lecture_category (id, name, description)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Programming', 'All programming related courses'),
    ('22222222-2222-2222-2222-222222222222', 'Web Development', 'Courses about building websites'),
    ('33333333-3333-3333-3333-333333333333', 'Design', 'Courses for graphic and UI/UX design');

-- 강의 데이터 삽입 (카테고리 추가)
INSERT INTO lecture (id, category_id, title, instructor_name, description, duration_minutes, created_at, updated_at)
VALUES
    ('aaa11111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Introduction to Programming', 'John Doe', 'Basic programming concepts for beginners', 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('aaa22222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Advanced Java', 'Jane Smith', 'In-depth Java programming techniques', 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bbb33333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'Web Development Basics', 'Alice Johnson', 'Introduction to web development', 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bbb44444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', 'Advanced Web Design', 'Chris Lee', 'Advanced web design techniques', 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ccc55555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', 'UI/UX Design Basics', 'Daisy Kim', 'Introduction to UI/UX design', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 유저가 강의를 수강하는 매핑 데이터 삽입
-- user_id = d3702a65-4506-47e4-bd97-b5753e378eac
INSERT INTO user_lecture_mapping (id, user_id, lecture_id, progress, start_date, end_date, is_active)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'd3702a65-4506-47e4-bd97-b5753e378eac', 'aaa11111-1111-1111-1111-111111111111', 50, CURRENT_TIMESTAMP, NULL, true),
    ('55555555-5555-5555-5555-555555555555', 'd3702a65-4506-47e4-bd97-b5753e378eac', 'bbb33333-3333-3333-3333-333333333333', 100, CURRENT_TIMESTAMP, NULL, true),
    ('66666666-6666-6666-6666-666666666666', 'd3702a65-4506-47e4-bd97-b5753e378eac', 'ccc55555-5555-5555-5555-555555555555', 30, CURRENT_TIMESTAMP, NULL, true);
