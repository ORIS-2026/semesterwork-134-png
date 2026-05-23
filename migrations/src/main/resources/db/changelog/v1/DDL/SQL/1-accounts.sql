CREATE TABLE accounts (
    id BIGSERIAL not null,
    name VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255),
    email VARCHAR(100) unique,
    is_oauthed BOOLEAN NOT NULL DEFAULT FALSE,
    google_id VARCHAR(40),
    constraint account_pk primary key (id)
);