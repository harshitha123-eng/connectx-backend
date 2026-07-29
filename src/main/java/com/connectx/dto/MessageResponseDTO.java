package com.connectx.dto;

import com.connectx.enums.MessageType;
import com.connectx.enums.MessageStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponseDTO {

    private Long id;

    private Long senderId;
    private String senderName;

    private Long receiverId;
    private String receiverName;

    private Long groupId;
    private String groupName;

    private String content;

    private MessageType type;

    private MessageStatus status;

    private String fileUrl;
    private String fileName;

    private Boolean isDeleted;

    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}