package com.connectx.dto;

import com.connectx.enums.ReactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageReactionResponseDTO {

    private Long id;

    private Long messageId;

    private Long userId;

    private String username;

    private ReactionType reaction;
}