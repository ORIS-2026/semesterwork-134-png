package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Поиск чата по владельцу
    Optional<Chat> findByOwnerId(Long ownerId);

}
