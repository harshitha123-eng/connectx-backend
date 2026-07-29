package com.connectx.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.connectx.dto.MessageRequestDTO;
import com.connectx.dto.MessageResponseDTO;
import com.connectx.dto.ReadRequestDTO;
import com.connectx.enums.MessageStatus;
import com.connectx.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    
    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @Valid @RequestBody MessageRequestDTO request) {

        return new ResponseEntity<>(
                messageService.sendMessage(request),
                HttpStatus.CREATED);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> getMessageById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                messageService.getMessageById(id));
    }

    
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<MessageResponseDTO>> getPrivateMessages(
            @PathVariable Long receiverId) {

        return ResponseEntity.ok(
                messageService.getMessagesByReceiver(receiverId));
    }

    
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<MessageResponseDTO>> getGroupMessages(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                messageService.getMessagesByGroup(groupId));
    }

    
    @PutMapping("/{messageId}/status")
    public ResponseEntity<MessageResponseDTO> updateStatus(
            @PathVariable Long messageId,
            @RequestParam MessageStatus status) {

        return ResponseEntity.ok(
                messageService.updateMessageStatus(messageId, status));
    }

    
    @GetMapping("/search")
    public ResponseEntity<List<MessageResponseDTO>> searchMessages(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                messageService.searchMessages(keyword));
    }

    
    @GetMapping("/search/private")
    public ResponseEntity<List<MessageResponseDTO>> searchPrivateMessages(
            @RequestParam Long user1,
            @RequestParam Long user2,
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                messageService.searchPrivateMessages(
                        user1,
                        user2,
                        keyword));
    }
    
 
    @GetMapping("/search/group")
    public ResponseEntity<List<MessageResponseDTO>> searchGroupMessages(
            @RequestParam Long groupId,
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                messageService.searchGroupMessages(
                        groupId,
                        keyword));
    }

    
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageResponseDTO>> getConversation(
            @RequestParam Long user1,
            @RequestParam Long user2) {

        return ResponseEntity.ok(
                messageService.getConversation(user1, user2));
    }

  
    @PostMapping("/read")
    public ResponseEntity<String> markMessagesAsRead(
           @Valid @RequestBody ReadRequestDTO request) {

        messageService.markMessagesAsRead(
                request.getSenderId(),
                request.getReceiverId());

        return ResponseEntity.ok("Messages marked as read successfully");
    }

    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long messageId) {

        messageService.deleteMessage(messageId);

        return ResponseEntity.ok("Message deleted successfully");
    }
}