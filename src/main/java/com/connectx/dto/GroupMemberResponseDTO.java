package com.connectx.dto;

import com.connectx.enums.OnlineStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberResponseDTO {

    private Long id;

    private String fullName;

    private String username;

    private String email;

    private String profilePicture;

    private String statusMessage;

    private OnlineStatus onlineStatus;
}