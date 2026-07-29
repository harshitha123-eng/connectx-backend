package com.connectx.WebSocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.connectx.repository.UserRepository;
import com.connectx.service.impl.UserPresenceService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;
    private final UserPresenceService presenceService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

       
        if (event.getUser() == null) {
            return;
        }

        String username = event.getUser().getName();

        userRepository.findByUsername(username).ifPresent(user ->
                presenceService.userDisconnected(user.getId()));
    }
}