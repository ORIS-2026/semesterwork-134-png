package com.tech.dimefresh.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(length = 100)
    private String email;

    @Column(name = "is_oauthed")
    private boolean oauthed;

    @Column(name = "google_id")
    private String googleId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_s3_object_id")
    private S3Object avatarS3Object;
}
