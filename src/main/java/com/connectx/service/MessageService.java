package com.connectx.service;

import java.util.List;
import com.connectx.dto.MessageRequestDTO;
import com.connectx.dto.MessageResponseDTO;
import com.connectx.enums.MessageStatus;

public interface MessageService {

    
    MessageResponseDTO sendMessage(MessageRequestDTO request);

    
    MessageResponseDTO getMessageById(Long messageId);

   
    List<MessageResponseDTO> getMessagesByReceiver(Long receiverId);

    List<MessageResponseDTO> getConversation(
            Long user1,
            Long user2);

    
    List<MessageResponseDTO> getMessagesByGroup(Long groupId);

 
    MessageResponseDTO updateMessageStatus(
            Long messageId,
            MessageStatus status);

    MessageResponseDTO getLatestReadMessage(
            Long senderId,
            Long receiverId);

    
    List<MessageResponseDTO> markMessagesAsRead(
            Long senderId,
            Long receiverId);

    
    List<MessageResponseDTO> searchMessages(
            String keyword);

    List<MessageResponseDTO> searchPrivateMessages(
            Long user1,
            Long user2,
            String keyword);

    
    List<MessageResponseDTO> searchGroupMessages(
            Long groupId,
            String keyword);

   
    void deleteMessage(Long messageId);
}