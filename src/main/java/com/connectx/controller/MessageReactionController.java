package com.connectx.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.connectx.dto.MessageReactionRequestDTO;
import com.connectx.dto.MessageReactionResponseDTO;
import com.connectx.service.MessageReactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class MessageReactionController {

    private final MessageReactionService reactionService;

    @PostMapping
    public ResponseEntity<MessageReactionResponseDTO> addReaction(
            @RequestBody MessageReactionRequestDTO request) {

        return ResponseEntity.ok(
                reactionService.addReaction(request));
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<List<MessageReactionResponseDTO>> getReactions(
            @PathVariable Long messageId) {

        return ResponseEntity.ok(
                reactionService.getReactions(messageId));
    }
}