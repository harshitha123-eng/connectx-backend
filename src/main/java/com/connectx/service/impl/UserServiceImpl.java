package com.connectx.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.connectx.dto.UserRequestDTO;
import com.connectx.dto.UserResponseDTO;
import com.connectx.entity.User;
import com.connectx.enums.OnlineStatus;
import com.connectx.exception.DuplicateUserException;
import com.connectx.exception.UserNotFoundException;
import com.connectx.repository.UserRepository;
import com.connectx.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException(
                    "Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException(
                    "Email already exists");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profilePicture(dto.getProfilePicture())
                .statusMessage(dto.getStatusMessage())
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id " + id));

        return mapToResponse(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id " + id));

        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setProfilePicture(dto.getProfilePicture());
        user.setStatusMessage(dto.getStatusMessage());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(
                    "User not found with id " + id);
        }

        userRepository.deleteById(id);
    }
    
    @Override
    public UserResponseDTO updateOnlineStatus(
            Long userId,
            OnlineStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id " + userId));

        user.setOnlineStatus(status);

        if (status == OnlineStatus.OFFLINE) {
            user.setLastSeen(java.time.LocalDateTime.now());
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    private UserResponseDTO mapToResponse(User user) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .statusMessage(user.getStatusMessage())
                .onlineStatus(user.getOnlineStatus())
                .lastSeen(user.getLastSeen())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}