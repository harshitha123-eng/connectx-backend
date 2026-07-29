package com.connectx.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponseDTO {

    private Long id;

    private Long senderId;

    private String senderName;

    private Long receiverId;

    private String title;

    private String message;

    private String type;     

    private Long messageId;

    private Boolean isRead;

    private LocalDateTime createdAt;
}