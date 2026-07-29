package com.connectx.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.connectx.dto.MessageRequestDTO;
import com.connectx.dto.MessageResponseDTO;
import com.connectx.dto.ReadReceiptDTO;
import com.connectx.dto.TypingDTO;
import com.connectx.enums.MessageStatus;
import com.connectx.service.MessageService;
import com.connectx.dto.GroupTypingDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/private-message")
    public void sendPrivateMessage(
            MessageRequestDTO request,
            Principal principal) {

        MessageResponseDTO response =
                messageService.sendMessage(request);

        response = messageService.updateMessageStatus(
                response.getId(),
                MessageStatus.DELIVERED);

        messagingTemplate.convertAndSend(
                "/topic/private/" + request.getReceiverId(),
                response);

        messagingTemplate.convertAndSend(
                "/topic/private/" + request.getSenderId(),
                response);

        messagingTemplate.convertAndSend(
                "/topic/delivered/" + request.getSenderId(),
                response.getId());
    }

    @MessageMapping("/group-message")
    public void sendGroupMessage(
            MessageRequestDTO request,
            Principal principal) {
    	
    	System.out.println("GROUP MESSAGE RECEIVED");
        System.out.println("Group = " + request.getGroupId());
        System.out.println("Sender = " + request.getSenderId());
        System.out.println("Content = " + request.getContent());


        MessageResponseDTO response =
                messageService.sendMessage(request);
        
        System.out.println("Sending to /topic/group/" + request.getGroupId());

        messagingTemplate.convertAndSend(
                "/topic/group/" + request.getGroupId(),
                response);
    }

    @MessageMapping("/typing")
    public void typing(
            TypingDTO typingDTO,
            Principal principal) {

        messagingTemplate.convertAndSend(
                "/topic/typing/" + typingDTO.getReceiverId(),
                typingDTO);
    }
    
    @MessageMapping("/group-typing")
    public void groupTyping(
            GroupTypingDTO typingDTO,
            Principal principal) {
    	
        messagingTemplate.convertAndSend(
                "/topic/group-typing/" + typingDTO.getGroupId(),
                typingDTO
        );
    }
    
 @MessageMapping("/read")
 public void markMessagesAsRead(
         ReadReceiptDTO receipt,
         Principal principal) {

     List<MessageResponseDTO> updatedMessages =
             messageService.markMessagesAsRead(
                     receipt.getSenderId(),
                     receipt.getReceiverId());

     messagingTemplate.convertAndSend(
             "/topic/read/" + receipt.getSenderId(),
             updatedMessages);
 }
 
 @MessageMapping("/delete-message")
 public void deleteMessage(
         @org.springframework.messaging.handler.annotation.Payload Long messageId,
         Principal principal) {

     messageService.deleteMessage(messageId);
 }
}