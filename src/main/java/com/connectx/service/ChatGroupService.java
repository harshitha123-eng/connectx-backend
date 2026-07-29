package com.connectx.service;

import java.util.List;
import com.connectx.dto.ChatGroupResponseDTO;
import com.connectx.dto.CreateGroupRequestDTO;
import com.connectx.dto.GroupMemberResponseDTO;
import com.connectx.dto.UpdateGroupRequestDTO;

public interface ChatGroupService {

    ChatGroupResponseDTO createGroup(CreateGroupRequestDTO request);

    ChatGroupResponseDTO getGroupById(Long groupId);

    List<ChatGroupResponseDTO> getGroupsByUser(Long userId);

    List<GroupMemberResponseDTO> getGroupMembers(Long groupId);

    ChatGroupResponseDTO addMember(
            Long groupId,
            Long userId,
            Long adminId);

    ChatGroupResponseDTO removeMember(
            Long groupId,
            Long userId,
            Long adminId);

    ChatGroupResponseDTO updateGroup(
            Long groupId,
            UpdateGroupRequestDTO request);

    void leaveGroup(
            Long groupId,
            Long userId);

    void deleteGroup(
            Long groupId,
            Long adminId);
}