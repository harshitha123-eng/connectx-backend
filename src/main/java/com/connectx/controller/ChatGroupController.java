package com.connectx.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.connectx.dto.ChatGroupResponseDTO;
import com.connectx.dto.CreateGroupRequestDTO;
import com.connectx.dto.GroupMemberResponseDTO;
import com.connectx.dto.UpdateGroupRequestDTO;
import com.connectx.service.ChatGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ChatGroupController {

    private final ChatGroupService chatGroupService;

    @PostMapping
    public ResponseEntity<ChatGroupResponseDTO> createGroup(
            @Valid @RequestBody CreateGroupRequestDTO request) {

        return new ResponseEntity<>(
                chatGroupService.createGroup(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ChatGroupResponseDTO> getGroupById(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                chatGroupService.getGroupById(groupId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatGroupResponseDTO>> getGroupsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                chatGroupService.getGroupsByUser(userId));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponseDTO>> getGroupMembers(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                chatGroupService.getGroupMembers(groupId));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ChatGroupResponseDTO> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupRequestDTO request) {

        return ResponseEntity.ok(
                chatGroupService.updateGroup(groupId, request));
    }

    @PutMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ChatGroupResponseDTO> addMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam Long adminId) {

        return ResponseEntity.ok(
                chatGroupService.addMember(
                        groupId,
                        userId,
                        adminId));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ChatGroupResponseDTO> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam Long adminId) {

        return ResponseEntity.ok(
                chatGroupService.removeMember(
                        groupId,
                        userId,
                        adminId));
    }

    @DeleteMapping("/{groupId}/leave/{userId}")
    public ResponseEntity<String> leaveGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        chatGroupService.leaveGroup(groupId, userId);

        return ResponseEntity.ok("Left group successfully");
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(
            @PathVariable Long groupId,
            @RequestParam Long adminId) {

        chatGroupService.deleteGroup(groupId, adminId);

        return ResponseEntity.ok("Group deleted successfully");
    }
}