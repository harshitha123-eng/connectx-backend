package com.connectx.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.connectx.dto.MessageRequestDTO;
import com.connectx.dto.MessageResponseDTO;
import com.connectx.entity.ChatGroup;
import com.connectx.entity.Message;
import com.connectx.entity.User;
import com.connectx.enums.MessageStatus;
import com.connectx.enums.MessageType;
import com.connectx.exception.InvalidMessageException;
import com.connectx.exception.MessageNotFoundException;
import com.connectx.exception.UserNotFoundException;
import com.connectx.repository.ChatGroupRepository;
import com.connectx.repository.MessageRepository;
import com.connectx.repository.UserRepository;
import com.connectx.service.MessageService;
import com.connectx.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageResponseDTO sendMessage(MessageRequestDTO request) {

        validateMessageRequest(request);

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Sender not found with id: " + request.getSenderId()));

        User receiver = null;
        ChatGroup group = null;

        if (request.getReceiverId() != null) {
            receiver = userRepository.findById(request.getReceiverId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "Receiver not found with id: " + request.getReceiverId()));
        }

        if (request.getGroupId() != null) {

            group = chatGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new InvalidMessageException(
                            "Group not found"));

            boolean isMember = group.getMembers()
                    .stream()
                    .anyMatch(member ->
                            member.getId().equals(sender.getId()));

            if (!isMember) {
                throw new InvalidMessageException(
                        "Sender is not a member of this group");
            }
        }

        
        String content = (request.getType() == MessageType.TEXT)
                ? request.getContent()
                : "";

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .group(group)
                .content(content)
                .type(request.getType())
                .fileUrl(request.getFileUrl())
                .fileName(request.getFileName())
                .status(MessageStatus.SENT)
                .isDeleted(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        
        String notificationText;

        if (request.getType() == MessageType.TEXT) {
            notificationText = sender.getUsername() + ": " + content;
        } else {
            notificationText = sender.getUsername() + " sent a file";
        }

        if (receiver != null) {

            notificationService.createNotification(
                    sender.getId(),
                    receiver.getId(),
                    "New Message",
                    notificationText,
                    savedMessage.getId()
            );

        }
        else if (group != null) {

            for (User member : group.getMembers()) {

                if (!member.getId().equals(sender.getId())) {

                    notificationService.createNotification(
                            sender.getId(),
                            member.getId(),
                            group.getName(),
                            sender.getUsername() + ": " + content,
                            savedMessage.getId()
                    );
                }
            }
        }

        return mapToDTO(savedMessage);
        }    

    @Override
    @Transactional(readOnly = true)
    public MessageResponseDTO getMessageById(Long messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message not found with id: " + messageId));

        return mapToDTO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessagesByReceiver(Long receiverId) {

        return messageRepository.findByReceiverIdOrderBySentAtAsc(receiverId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessagesByGroup(Long groupId) {

    	return messageRepository.findByGroupIdOrderBySentAtAsc(groupId)
    	        .stream()
    	        .map(this::mapToDTO)
    	        .collect(Collectors.toList());
    }

    @Override
    public MessageResponseDTO updateMessageStatus(Long messageId, MessageStatus status) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message not found with id: " + messageId));

        message.setStatus(status);

        if (status == MessageStatus.DELIVERED &&
                message.getDeliveredAt() == null) {

            message.setDeliveredAt(LocalDateTime.now());
        }
        if (status == MessageStatus.READ &&
                message.getReadAt() == null) {

            message.setReadAt(LocalDateTime.now());
        }

        return mapToDTO(messageRepository.save(message));
    }

    @Override
    public List<MessageResponseDTO> markMessagesAsRead(Long senderId, Long receiverId) {

        int rows = messageRepository.markMessagesAsRead(
                senderId,
                receiverId,
                MessageStatus.READ,
                LocalDateTime.now()
        );

        return messageRepository.getConversation(senderId, receiverId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
 
    
    @Override
    public void deleteMessage(Long messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message not found with id: " + messageId));


        message.setIsDeleted(true);

        messageRepository.save(message);



        Map<String,Object> deleteEvent = new HashMap<>();

        deleteEvent.put("type", "DELETE_MESSAGE");
        deleteEvent.put("messageId", messageId);

        if (message.getReceiver() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/private/" + message.getReceiver().getId(),
                    deleteEvent
            );

            messagingTemplate.convertAndSend(
                    "/topic/private/" + message.getSender().getId(),
                    deleteEvent
            );
        }

        if(message.getGroup() != null){

            messagingTemplate.convertAndSend(
                    "/topic/group/" + message.getGroup().getId(),
                    deleteEvent
            );
        }

    }
    private void validateMessageRequest(MessageRequestDTO request) {

        if (request.getReceiverId() == null && request.getGroupId() == null) {
            throw new InvalidMessageException("Either receiverId or groupId is required");
        }

        if (request.getReceiverId() != null && request.getGroupId() != null) {
            throw new InvalidMessageException("Cannot send to both receiver and group");
        }

        if (request.getType() == null) {
            request.setType(MessageType.TEXT);
        }

        
        if (request.getType() == MessageType.TEXT) {
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                throw new InvalidMessageException("Text message content cannot be empty");
            }
        }

        
        if (request.getType() != MessageType.TEXT){

            if (request.getFileUrl() == null || request.getFileUrl().trim().isEmpty()) {
                throw new InvalidMessageException("File URL is required");
            }

            if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
                throw new InvalidMessageException("File Name is required");
            }
        }
    }

    
    private MessageResponseDTO mapToDTO(Message message) {

        return MessageResponseDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())

                .receiverId(message.getReceiver() != null ? message.getReceiver().getId() : null)
                .receiverName(message.getReceiver() != null ? message.getReceiver().getFullName() : null)

                .groupId(message.getGroup() != null ? message.getGroup().getId() : null)
                .groupName(message.getGroup() != null ? message.getGroup().getName() : null)

                .content(message.getContent())
                .type(message.getType())
                .status(message.getStatus())

                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())

                .isDeleted(message.getIsDeleted())
                .sentAt(message.getSentAt())
                .deliveredAt(message.getDeliveredAt())
                .readAt(message.getReadAt())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> searchMessages(
            String keyword) {

        return messageRepository
        		.findByContentContainingIgnoreCaseAndIsDeletedFalse(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> searchGroupMessages(
            Long groupId,
            String keyword) {

        return messageRepository
                .searchGroupMessages(groupId, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversation(
            Long user1,
            Long user2) {

        List<Message> messages = messageRepository.getConversation(user1, user2);

        System.out.println("Total Messages = " + messages.size());
        
        return messages.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public MessageResponseDTO getLatestReadMessage(Long senderId, Long receiverId) {

        Message message = messageRepository.getConversation(senderId, receiverId)
                .stream()
                .filter(m -> m.getStatus() == MessageStatus.READ)
                .reduce((first, second) -> second)
                .orElse(null);

        return message == null ? null : mapToDTO(message);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> searchPrivateMessages(
            Long user1,
            Long user2,
            String keyword) {

        return messageRepository
                .searchPrivateChat(
                        user1,
                        user2,
                        keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
                
    }
    
}