package com.connectx.service;

import java.util.List;
import com.connectx.dto.UserRequestDTO;
import com.connectx.dto.UserResponseDTO;
import com.connectx.enums.OnlineStatus;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO request);

    UserResponseDTO getUserById(Long userId);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long userId, UserRequestDTO request);

    void deleteUser(Long userId);
    
    UserResponseDTO updateOnlineStatus(
            Long userId,
            OnlineStatus status);
}