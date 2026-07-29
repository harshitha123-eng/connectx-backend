package com.connectx.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.connectx.dto.ChatGroupResponseDTO;
import com.connectx.dto.CreateGroupRequestDTO;
import com.connectx.dto.GroupMemberResponseDTO;
import com.connectx.dto.UpdateGroupRequestDTO;
import com.connectx.entity.ChatGroup;
import com.connectx.entity.User;
import com.connectx.exception.GroupMemberAlreadyExistsException;
import com.connectx.exception.GroupMemberNotFoundException;
import com.connectx.exception.GroupNotFoundException;
import com.connectx.exception.UnauthorizedGroupActionException;
import com.connectx.exception.UserNotFoundException;
import com.connectx.repository.ChatGroupRepository;
import com.connectx.repository.UserRepository;
import com.connectx.service.ChatGroupService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatGroupServiceImpl implements ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    @Override
    public ChatGroupResponseDTO createGroup(CreateGroupRequestDTO request) {

        User admin = getUser(request.getAdminId());

        ChatGroup group = ChatGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .groupPicture(request.getGroupPicture())
                .admin(admin)
                .build();

        
        group.getMembers().add(admin);
        admin.getGroups().add(group);

        
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {

            for (Long memberId : request.getMemberIds()) {

                if (memberId.equals(admin.getId())) {
                    continue;
                }

                User member = getUser(memberId);

                group.getMembers().add(member);
                member.getGroups().add(group);
            }
        }

        ChatGroup savedGroup = chatGroupRepository.save(group);

        return mapToDTO(savedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatGroupResponseDTO getGroupById(Long groupId) {

        ChatGroup group = getGroup(groupId);

        return mapToDTO(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatGroupResponseDTO> getGroupsByUser(Long userId) {

        getUser(userId);

        return chatGroupRepository.findByMembers_Id(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponseDTO> getGroupMembers(Long groupId) {

        ChatGroup group = getGroup(groupId);

        return group.getMembers()
                .stream()
                .map(user -> GroupMemberResponseDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .profilePicture(user.getProfilePicture())
                        .statusMessage(user.getStatusMessage())
                        .onlineStatus(user.getOnlineStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ChatGroupResponseDTO addMember(
            Long groupId,
            Long userId,
            Long adminId) {

        ChatGroup group = getGroup(groupId);

        validateAdmin(group, adminId);

        User user = getUser(userId);

        if (group.getMembers().contains(user)) {
            throw new GroupMemberAlreadyExistsException(
                    "User is already a member of this group");
        }

        group.getMembers().add(user);
        user.getGroups().add(group);

        ChatGroup updatedGroup = chatGroupRepository.save(group);

        return mapToDTO(updatedGroup);
    }

    @Override
    public ChatGroupResponseDTO removeMember(
            Long groupId,
            Long userId,
            Long adminId) {

        ChatGroup group = getGroup(groupId);

        validateAdmin(group, adminId);

        User user = getUser(userId);

        if (!group.getMembers().contains(user)) {
            throw new GroupMemberNotFoundException(
                    "User is not a member of this group");
        }

        if (group.getAdmin().getId().equals(userId)) {
            throw new UnauthorizedGroupActionException(
                    "Group admin cannot be removed");
        }

        group.getMembers().remove(user);
        user.getGroups().remove(group);

        ChatGroup updatedGroup = chatGroupRepository.save(group);

        return mapToDTO(updatedGroup);
    }
    @Override
    public ChatGroupResponseDTO updateGroup(
            Long groupId,
            UpdateGroupRequestDTO request) {

        ChatGroup group = getGroup(groupId);

        validateAdmin(group, request.getAdminId());

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setGroupPicture(request.getGroupPicture());

        ChatGroup updatedGroup = chatGroupRepository.save(group);

        return mapToDTO(updatedGroup);
    }

    @Override
    public void leaveGroup(
            Long groupId,
            Long userId) {

        ChatGroup group = getGroup(groupId);

        User user = getUser(userId);

        if (!group.getMembers().contains(user)) {
            throw new GroupMemberNotFoundException(
                    "User is not a member of this group");
        }

        if (group.getAdmin().getId().equals(userId)) {
            throw new UnauthorizedGroupActionException(
                    "Group admin cannot leave the group. Transfer admin rights or delete the group.");
        }

        group.getMembers().remove(user);
        user.getGroups().remove(group);

        chatGroupRepository.save(group);
    }

    @Override
    public void deleteGroup(
            Long groupId,
            Long adminId) {

        ChatGroup group = getGroup(groupId);

        validateAdmin(group, adminId);

        group.getMembers().forEach(member ->
                member.getGroups().remove(group));

        chatGroupRepository.delete(group);
    }
    
    private ChatGroup getGroup(Long groupId) {

        return chatGroupRepository.findById(groupId)
                .orElseThrow(() ->
                        new GroupNotFoundException(
                                "Group not found with ID: " + groupId));
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: " + userId));
    }

    private void validateAdmin(
            ChatGroup group,
            Long adminId) {

        if (!group.getAdmin().getId().equals(adminId)) {

            throw new UnauthorizedGroupActionException(
                    "Only group admin can perform this action");
        }
    }

    private ChatGroupResponseDTO mapToDTO(ChatGroup group) {

        return ChatGroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupPicture(group.getGroupPicture())
                .adminId(group.getAdmin().getId())
                .adminUsername(group.getAdmin().getUsername())
                .memberCount(group.getMembers().size())
                .memberIds(
                        group.getMembers()
                                .stream()
                                .map(User::getId)
                                .collect(Collectors.toSet()))
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}