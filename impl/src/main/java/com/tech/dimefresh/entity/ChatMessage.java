package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_type_id")
    private MessageType messageType;

    @Column(name = "msg_content_text")
    private String msgContentText;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msg_content_s3_object_id")
    private S3Object s3Object;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "is_by_bot")
    private boolean byBot = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

}
