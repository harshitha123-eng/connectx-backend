package com.connectx.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.connectx.dto.NotificationResponseDTO;
import com.connectx.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    
    @GetMapping("/{receiverId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @PathVariable Long receiverId) {

        return ResponseEntity.ok(
                notificationService.getNotifications(receiverId));
    }

 
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok("Notification marked as read");
    }

   
    @PutMapping("/receiver/{receiverId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long receiverId) {

        notificationService.markAllAsRead(receiverId);

        return ResponseEntity.ok("All notifications marked as read");
    }

 
    @GetMapping("/receiver/{receiverId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long receiverId) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(receiverId));
    }
}