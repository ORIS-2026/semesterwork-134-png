create table ai_requests (
    id uuid not null default gen_random_uuid(),
    api_service_request_id bigint,
    chat_msg_id uuid not null,
    status varchar(50) not null,
    constraint ai_request_chat_msg_fk foreign key (chat_msg_id)
        references chat_messages (id)
        on delete cascade,
    constraint ai_request_pk primary key (id)
);