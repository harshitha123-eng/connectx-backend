package com.connectx.controller;

import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.connectx.dto.UserRequestDTO;
import com.connectx.dto.UserResponseDTO;
import com.connectx.enums.OnlineStatus;
import com.connectx.service.UserService;
import com.connectx.service.impl.UserPresenceService;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserPresenceService presenceService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(
                userService.updateUser(id, dto));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> updateOnlineStatus(
            @PathVariable Long id,
            @RequestParam OnlineStatus status) {

        return ResponseEntity.ok(
                userService.updateOnlineStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                "User deleted successfully");
    }
    @GetMapping("/online-users")
    public Set<Long> getOnlineUsers() {
        return presenceService.getOnlineUsers();
    }
}