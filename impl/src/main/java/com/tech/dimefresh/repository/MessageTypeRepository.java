package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTypeRepository extends JpaRepository<MessageType, Integer> {

    Optional<MessageType> findByTitle(String title);
}
