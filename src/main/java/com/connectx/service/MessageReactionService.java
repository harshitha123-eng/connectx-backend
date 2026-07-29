package com.connectx.service;

import java.util.List;
import com.connectx.dto.MessageReactionRequestDTO;
import com.connectx.dto.MessageReactionResponseDTO;

public interface MessageReactionService {

    MessageReactionResponseDTO addReaction(
            MessageReactionRequestDTO request);

    List<MessageReactionResponseDTO> getReactions(
            Long messageId);

    void removeReaction(
            Long messageId,
            Long userId);
}