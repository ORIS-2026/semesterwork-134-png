package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "s3_objects")
@Getter
@Setter
public class S3Object {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false, length = 100)
    private String bucket;
}
