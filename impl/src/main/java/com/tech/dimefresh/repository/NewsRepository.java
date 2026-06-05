package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.News;
import com.tech.dimefresh.service.dto.NewsDataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {
    @Query("select n from News n where n.imageChatMessage.id = ?1")
    Optional<News> findByImageChatMessageId(UUID chatMessageId);

    @Transactional(readOnly = true)
    @NativeQuery("""
        select n.id as newsId,
               user_cm.msg_content_text as prompt,
               so.id::varchar as s3Key,
               so.bucket as s3Bucket,
               a.id as accountId,
               a.name as name,
               n.published_at as publishedAt
            from news n
            join chat_messages user_cm on n.user_chat_message_id = user_cm.id
            join chat_messages image_cm on n.image_chat_message_id = image_cm.id
            join s3_objects so on so.id = image_cm.msg_content_s3_object_id
            join accounts a on user_cm.account_id = a.id
        order by n.published_at desc
        limit ?2 offset ?1
""")
    List<NewsDataProjection> findNewsPage(Integer offset, Integer limit);
}
