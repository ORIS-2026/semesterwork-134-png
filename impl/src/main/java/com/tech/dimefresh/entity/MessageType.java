package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "message_types")
@Getter
@Setter
public class MessageType {

    @Id
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 100, nullable = false)
    private MessageTypeEnum title;
}
