package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.S3Object;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface S3ObjectRepository extends JpaRepository<S3Object, UUID> {
}
