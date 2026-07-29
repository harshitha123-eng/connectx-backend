package com.connectx.dto;

import java.time.LocalDateTime;
import com.connectx.enums.OnlineStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;

    private String fullName;

    private String username;

    private String email;

    private String profilePicture;

    private String statusMessage;

    private OnlineStatus onlineStatus;

    private LocalDateTime lastSeen;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}