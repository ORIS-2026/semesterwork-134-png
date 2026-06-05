package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("select msg from ChatMessage msg where msg.chat.id = :chatId")
    Page<ChatMessage> findAllByChatId(@Param("chatId") Long chatId, Pageable pageable);

}
