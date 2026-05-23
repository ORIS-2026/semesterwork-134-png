package com.tech.dimefresh.repository;

import com.tech.dimefresh.entity.AiRequest;
import com.tech.dimefresh.entity.AiRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiRequestRepository extends JpaRepository<AiRequest, UUID> {


    @Query("select ar from AiRequest ar where ar.status = :reqStatus")
    List<AiRequest> findAllWithStatus(@Param("reqStatus") AiRequestStatus status);

    List<AiRequest> findByChatMsgId_Chat_IdAndStatus(Long chatId, AiRequestStatus status);
}
