package com.connectx.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.connectx.dto.MessageReactionRequestDTO;
import com.connectx.dto.MessageReactionResponseDTO;
import com.connectx.entity.Message;
import com.connectx.entity.MessageReaction;
import com.connectx.entity.User;
import com.connectx.exception.MessageNotFoundException;
import com.connectx.exception.UserNotFoundException;
import com.connectx.repository.MessageReactionRepository;
import com.connectx.repository.MessageRepository;
import com.connectx.repository.UserRepository;
import com.connectx.service.MessageReactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageReactionServiceImpl
        implements MessageReactionService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageReactionRepository reactionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageReactionResponseDTO addReaction(
            MessageReactionRequestDTO request) {

        Message message = messageRepository
                .findById(request.getMessageId())
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found"));

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        MessageReaction reaction = reactionRepository
                .findByMessage_IdAndUser_Id(
                        request.getMessageId(),
                        request.getUserId())
                .orElse(
                        MessageReaction.builder()
                                .message(message)
                                .user(user)
                                .build());

        reaction.setReaction(request.getReaction());

        MessageReaction saved = reactionRepository.save(reaction);

        MessageReactionResponseDTO response =
                MessageReactionResponseDTO.builder()
                        .id(saved.getId())
                        .messageId(saved.getMessage().getId())
                        .userId(saved.getUser().getId())
                        .username(saved.getUser().getUsername())
                        .reaction(saved.getReaction())
                        .build();

        
        if (message.getReceiver() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/reactions/" + message.getSender().getId(),
                    response);

            messagingTemplate.convertAndSend(
                    "/topic/reactions/" + message.getReceiver().getId(),
                    response);
        }

        if (message.getGroup() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/group-reactions/" + message.getGroup().getId(),
                    response);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageReactionResponseDTO> getReactions(
            Long messageId) {

        return reactionRepository
                .findByMessage_Id(messageId)
                .stream()
                .map(reaction ->
                        MessageReactionResponseDTO.builder()
                                .id(reaction.getId())
                                .messageId(reaction.getMessage().getId())
                                .userId(reaction.getUser().getId())
                                .username(reaction.getUser().getUsername())
                                .reaction(reaction.getReaction())
                                .build())
                .collect(Collectors.toList());
    }

    @Override
    public void removeReaction(
            Long messageId,
            Long userId) {

        MessageReaction reaction = reactionRepository
                .findByMessage_IdAndUser_Id(messageId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Reaction not found"));

        reactionRepository.delete(reaction);
    }
}