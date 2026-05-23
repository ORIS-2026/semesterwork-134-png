create table chats (
    id bigserial not null,
    owner_id bigint,
    constraint chat_pk primary key (id),
    constraint owner_fk foreign key (owner_id)
                   references accounts (id)
)