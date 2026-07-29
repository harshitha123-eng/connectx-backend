package com.connectx.dto;

import com.connectx.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDTO {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    private Long receiverId;   
    private Long groupId;      

    private String content;    

    private MessageType type;

    private String fileUrl;    
    private String fileName;
}