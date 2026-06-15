
create table news (
    id uuid not null,
    image_chat_message_id uuid not null,
    user_chat_message_id uuid not null,

    created_at pg_catalog.timestamptz not null default pg_catalog.now(),
    published_at pg_catalog.timestamptz default pg_catalog.now(),

    status varchar(20) not null,
    constraint news_pk primary key (id),
    constraint news_image_chat_message_fk  foreign key (image_chat_message_id)
                  references chat_messages (id)
                  on delete cascade,
    constraint news_user_chat_message_fk foreign key (user_chat_message_id)
                  references chat_messages (id)
                  on delete restrict
)