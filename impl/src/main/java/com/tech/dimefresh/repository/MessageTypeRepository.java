package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.MessageType;
import com.tech.dimefresh.entity.MessageTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MessageTypeRepository extends JpaRepository<MessageType, Integer> {

    Optional<MessageType> findByTitle(MessageTypeEnum title);
}
