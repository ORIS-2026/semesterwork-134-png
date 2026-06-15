package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface S3ObjectRepository extends JpaRepository<S3Object, UUID> {
    @Query("select a.avatarS3Object from Account a where a.id = ?1")
    Optional<S3Object> findAvatarByAccountId(Long accountId);
}
