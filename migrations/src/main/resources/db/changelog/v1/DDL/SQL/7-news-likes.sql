create table news_likes (
    account_id bigint not null,
    news_id uuid not null,
    constraint news_likes_pk primary key (account_id, news_id),
    constraint news_likes_account_fk foreign key (account_id)
                        references accounts (id)
                on delete cascade,
    constraint news_likes_news_fk foreign key (news_id)
        references news (id)
        on delete cascade
)