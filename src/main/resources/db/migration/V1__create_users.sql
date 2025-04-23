CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role     varchar(20) not null,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       s_phone_number VARCHAR(20),
                       p_phone_number VARCHAR(20),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (id, username, password,role, name, email, s_phone_number, p_phone_number)
VALUES
    (RANDOM_UUID(), 'jungry', '1234','ADMIN', '정구리', 'jungry@email.com', '010-1111-1111', '010-2222-2222'),
    (RANDOM_UUID(), 'devy', '1234','STUDENT', '유냑준', 'devy@email.com', '010-3333-4444', '010-5555-6666');
