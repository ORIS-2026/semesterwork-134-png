create table s3_objects (
    id uuid not null default gen_random_uuid(),
    content_type varchar(100) not null,
    weight int not null,
    bucket varchar(100) not null,
    constraint s3_object_pk primary key (id)
);
