CREATE TABLE main_banner_first (
                                   id UUID PRIMARY KEY,
                                   title TEXT NOT NULL,
                                   image TEXT NOT NULL,
                                   url TEXT,
                                   banner_order INTEGER DEFAULT 0,
                                   active BOOLEAN DEFAULT TRUE,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);