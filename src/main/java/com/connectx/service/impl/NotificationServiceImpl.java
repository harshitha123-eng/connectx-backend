package com.connectx.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.connectx.dto.NotificationResponseDTO;
import com.connectx.entity.Message;
import com.connectx.entity.Notification;
import com.connectx.entity.User;
import com.connectx.exception.MessageNotFoundException;
import com.connectx.exception.UserNotFoundException;
import com.connectx.repository.MessageRepository;
import com.connectx.repository.NotificationRepository;
import com.connectx.repository.UserRepository;
import com.connectx.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationResponseDTO createNotification(
            Long senderId,
            Long receiverId,
            String title,
            String message,
            Long messageId) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() ->
                        new UserNotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new UserNotFoundException("Receiver not found"));

        Message chatMessage = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found"));

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .sender(sender)
                .receiver(receiver)
                .chatMessage(chatMessage)
                .build();

        Notification saved = notificationRepository.save(notification);

        NotificationResponseDTO response = mapToDTO(saved);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiverId,
                response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotifications(Long receiverId) {

        return notificationRepository
                .findByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        notification.setIsRead(true);
    }

    @Override
    public void markAllAsRead(Long receiverId) {

        List<Notification> notifications =
                notificationRepository.findByReceiverIdAndIsReadFalse(receiverId);

        notifications.forEach(notification ->
                notification.setIsRead(true));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long receiverId) {

        return notificationRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    private NotificationResponseDTO mapToDTO(Notification notification) {

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .senderId(notification.getSender().getId())
                .senderName(notification.getSender().getFullName())
                .receiverId(notification.getReceiver().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .messageId(
                        notification.getChatMessage() != null
                                ? notification.getChatMessage().getId()
                                : null
                )
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}