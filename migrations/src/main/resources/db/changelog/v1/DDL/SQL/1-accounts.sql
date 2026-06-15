CREATE TABLE accounts (
    id BIGSERIAL not null,
    name VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255),
    email VARCHAR(100) unique,
    is_oauthed BOOLEAN NOT NULL DEFAULT FALSE,
    google_id VARCHAR(40),

    avatar_s3_object_id uuid,
    constraint account_pk primary key (id),
    constraint account_email_uniqueness unique (email),
    constraint account_s3_object_fk foreign key (avatar_s3_object_id)
                      references s3_objects (id)
                      on delete set null
);