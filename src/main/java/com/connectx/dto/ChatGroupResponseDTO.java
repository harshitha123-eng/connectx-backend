package com.connectx.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGroupResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String groupPicture;

    private Long adminId;

    private String adminUsername;

    private Integer memberCount;

    private Set<Long> memberIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}