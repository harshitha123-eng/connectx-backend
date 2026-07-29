package com.connectx.entity;

import java.time.LocalDateTime;
import com.connectx.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "message_reactions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"message_id", "user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reactedAt;

    @PrePersist
    public void onCreate() {
        this.reactedAt = LocalDateTime.now();
    }
}