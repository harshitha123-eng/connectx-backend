package com.connectx.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.connectx.entity.User;
import com.connectx.repository.UserRepository;
import com.connectx.security.JwtService;
import com.connectx.service.impl.UserPresenceService;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserPresenceService presenceService;

    public WebSocketAuthChannelInterceptor(
            JwtService jwtService,
            UserRepository userRepository,
            @Lazy UserPresenceService presenceService) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.presenceService = presenceService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            System.out.println("======================================");
            System.out.println("STOMP CONNECT RECEIVED");

            List<String> authHeaders =
                    accessor.getNativeHeader("Authorization");

            System.out.println("AUTH HEADER: " + authHeaders);

            if (authHeaders == null || authHeaders.isEmpty()) {

                System.out.println("TOKEN NOT RECEIVED");
                System.out.println("======================================");

                return message;
            }

            String token = authHeaders.get(0);

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            try {

                System.out.println("JWT TOKEN RECEIVED");

                String username = jwtService.extractUsername(token);

                System.out.println("USERNAME FROM TOKEN: " + username);

                User user = userRepository.findByUsername(username)
                        .orElse(null);

                if (user == null) {

                    System.out.println("USER NOT FOUND");
                    System.out.println("======================================");

                    return message;
                }

                System.out.println("USER FOUND");
                System.out.println("USER ID: " + user.getId());
                System.out.println("USERNAME: " + user.getUsername());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getId().toString(),
                                null,
                                Collections.emptyList());

                accessor.setUser(authentication);

                Map<String, Object> sessionAttributes =
                        accessor.getSessionAttributes();

                if (sessionAttributes != null) {

                    sessionAttributes.put("username", username);
                    sessionAttributes.put("userId", user.getId());

                    System.out.println("SESSION ATTRIBUTES STORED");
                }

                System.out.println("CALLING USER CONNECTED...");

                presenceService.userConnected(user.getId());

                System.out.println("USER MARKED ONLINE");
                System.out.println("WEBSOCKET AUTH SUCCESS");
                System.out.println("======================================");

            } catch (Exception e) {

                System.out.println("WEBSOCKET AUTH ERROR");
                e.printStackTrace();
                System.out.println("======================================");
            }
        }

        return message;
    }
}