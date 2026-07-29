package com.connectx.dto;

import com.connectx.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactionRequestDTO {

    @NotNull(message = "Message ID is required")
    private Long messageId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Reaction is required")
    private ReactionType reaction;
}