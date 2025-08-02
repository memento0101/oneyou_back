-- 수강후기 테이블 (기존 review 테이블을 대체)
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

-- 인덱스 생성
CREATE INDEX idx_lecture_review_lecture_id ON lecture_review(lecture_id);
CREATE INDEX idx_lecture_review_user_id ON lecture_review(user_id);
CREATE INDEX idx_lecture_review_rating ON lecture_review(rating);
CREATE INDEX idx_lecture_review_created_at ON lecture_review(created_at DESC);

-- 공지사항 테이블
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

-- 공지사항 인덱스 생성
CREATE INDEX idx_notice_important ON notice(is_important);
CREATE INDEX idx_notice_created_at ON notice(created_at DESC);
CREATE INDEX idx_notice_author_id ON notice(author_id);