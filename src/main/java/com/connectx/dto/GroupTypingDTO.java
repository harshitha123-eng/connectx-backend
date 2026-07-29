package com.connectx.dto;

import lombok.Data;

@Data
public class GroupTypingDTO {

    private Long groupId;

    private Long senderId;

    private String senderName;

    private boolean typing;

}