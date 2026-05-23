package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Поиск чатов по владельцу
    List<Chat> findByOwnerId(Long ownerId);

    // Можно добавить пагинационный вариант
    Page<Chat> findByOwnerId(Long ownerId, Pageable pageable);
}
