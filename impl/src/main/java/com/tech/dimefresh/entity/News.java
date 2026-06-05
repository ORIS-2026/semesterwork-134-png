package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "news")
@Setter
@Getter
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * сообщение от бота содержащее сгенерированную картинку
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_chat_message_id")
    private ChatMessage imageChatMessage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_chat_message_id")
    private ChatMessage userChatMessage;

    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "published_at")
    private Instant publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsStatus status;
}
