-- lecture_quiz 테이블에 hint 컬럼 추가
ALTER TABLE lecture_quiz ADD COLUMN hint TEXT;

UPDATE lecture_quiz
SET hint = '속력은 거리를 시간으로 나눈 값입니다. 질량(kg)은 속력의 단위가 아닙니다.'
WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';