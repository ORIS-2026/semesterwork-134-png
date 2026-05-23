package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ai_requests")
@Getter
@Setter
public class AiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiRequestStatus status;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_msg_id")
    private ChatMessage chatMsgId;

    @Column(name = "api_service_request_id")
    private Long apiServiceRequestId;
}
