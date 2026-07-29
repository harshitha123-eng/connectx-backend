package com.connectx.service.impl;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.connectx.dto.UserStatusDTO;
import com.connectx.enums.OnlineStatus;
import com.connectx.repository.UserRepository;

@Service
public class UserPresenceService {

    private final ConcurrentHashMap<Long, Integer> sessionCount = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public UserPresenceService(
            SimpMessagingTemplate messagingTemplate,
            UserRepository userRepository) {

        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }
    
    public void userConnected(Long userId) {

        sessionCount.merge(userId, 1, Integer::sum);

        userRepository.findById(userId).ifPresent(user -> {
            user.setOnlineStatus(OnlineStatus.ONLINE);
            user.setLastSeen(LocalDateTime.now());

            userRepository.save(user);

            messagingTemplate.convertAndSend(
                    "/topic/status",
                    new UserStatusDTO(userId, "ONLINE"));
        });
    }

    public Set<Long> getOnlineUsers() {
        return sessionCount.keySet();
    }

    // Update lastSeen whenever user performs an action
    public void updateLastSeen(Long userId) {

        userRepository.findById(userId).ifPresent(user -> {

            user.setLastSeen(LocalDateTime.now());

            userRepository.save(user);
        });
    }

    public void userDisconnected(Long userId) {

        sessionCount.computeIfPresent(userId, (k, v) -> v - 1);

        if (sessionCount.getOrDefault(userId, 0) <= 0) {

            sessionCount.remove(userId);

            userRepository.findById(userId).ifPresent(user -> {

                user.setOnlineStatus(OnlineStatus.OFFLINE);
                user.setLastSeen(LocalDateTime.now());

                userRepository.save(user);

                messagingTemplate.convertAndSend(
                        "/topic/status",
                        new UserStatusDTO(userId, "OFFLINE"));
            });
        }
    }

    public boolean isOnline(Long userId) {
        return sessionCount.getOrDefault(userId, 0) > 0;
    }
}