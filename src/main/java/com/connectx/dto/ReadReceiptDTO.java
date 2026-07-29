package com.connectx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptDTO {

    private Long senderId;
    private Long receiverId;
}