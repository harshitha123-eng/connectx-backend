package com.connectx.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.connectx.entity.MessageReaction;

public interface MessageReactionRepository
        extends JpaRepository<MessageReaction, Long> {

    List<MessageReaction> findByMessage_Id(Long messageId);

    Optional<MessageReaction> findByMessage_IdAndUser_Id(
            Long messageId,
            Long userId);
}