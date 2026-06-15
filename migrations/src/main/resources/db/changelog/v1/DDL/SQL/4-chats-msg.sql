create table message_types (
    id int not null,
    title varchar(100) not null,
    constraint message_type_pk primary key (id)
);

create table chat_messages (
    id uuid not null default gen_random_uuid(),
    message_type_id int not null,
    msg_content_text varchar(500),
    msg_content_s3_object_id uuid,
    created_at timestamptz not null default now(),

    is_by_bot bool not null default false,

    account_id bigint,

    chat_id bigint not null,

    constraint chat_message_pk primary key (id),
    constraint account_message_fk foreign key (account_id)
                           references accounts(id)
                           on delete set null,
    constraint chat_for_message_fk foreign key (chat_id)
        references chats(id)
        on delete cascade,
    constraint message_type_fk foreign key (message_type_id)
        references message_types(id)
        on delete restrict,
    constraint chat_message_s3_object_fk foreign key (msg_content_s3_object_id)
        references s3_objects(id)
        on delete set null
)