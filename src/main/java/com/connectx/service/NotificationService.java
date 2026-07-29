package com.connectx.service;

import java.util.List;
import com.connectx.dto.NotificationResponseDTO;

public interface NotificationService {

    NotificationResponseDTO createNotification(
            Long senderId,
            Long receiverId,
            String title,
            String message,
            Long messageId);

    List<NotificationResponseDTO> getNotifications(Long receiverId);

    void markAsRead(Long notificationId);

    void markAllAsRead(Long receiverId);

    long getUnreadCount(Long receiverId);
}