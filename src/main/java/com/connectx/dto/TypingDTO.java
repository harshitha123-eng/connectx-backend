package com.connectx.dto;

import lombok.Data;

@Data
public class TypingDTO {

    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String senderName;
    private boolean typing;
}