package com.connectx.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.connectx.entity.Message;
import com.connectx.enums.MessageStatus;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	List<Message> findByReceiverIdOrderBySentAtAsc(Long receiverId);

	List<Message> findByGroupIdOrderBySentAtAsc(Long groupId);

    List<Message> findBySenderIdOrderBySentAtAsc(Long senderId);

    List<Message> findByContentContainingIgnoreCaseAndIsDeletedFalse(
            String keyword);
    @Query("""
        SELECT m FROM Message m
        WHERE
        (
            (m.sender.id = :user1 AND m.receiver.id = :user2)
            OR
            (m.sender.id = :user2 AND m.receiver.id = :user1)
        )
        AND m.isDeleted = false
        AND
        (
            LOWER(COALESCE(m.content, ''))
            LIKE LOWER(CONCAT('%', :keyword, '%'))

            OR

            LOWER(COALESCE(m.fileName, ''))
            LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY m.sentAt ASC
        """)
    List<Message> searchPrivateChat(
            @Param("user1") Long user1,
            @Param("user2") Long user2,
            @Param("keyword") String keyword);

    @Query("""
        SELECT m FROM Message m
        WHERE
        (
            (m.sender.id = :user1 AND m.receiver.id = :user2)
            OR
            (m.sender.id = :user2 AND m.receiver.id = :user1)
        )
        ORDER BY m.sentAt ASC
        """)
    List<Message> getConversation(
            @Param("user1") Long user1,
            @Param("user2") Long user2);
    
    @Modifying(
        clearAutomatically = true,
        flushAutomatically = true
    )
    @Transactional
    @Query("""
        UPDATE Message m
        SET 
            m.status = :status,
            m.readAt = :readTime
        WHERE 
            m.sender.id = :senderId
            AND m.receiver.id = :receiverId
            AND m.status <> :status
        """)
    int markMessagesAsRead(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("status") MessageStatus status,
            @Param("readTime") LocalDateTime readTime);

    @Query("""
        SELECT m FROM Message m
        WHERE 
            m.group.id = :groupId
            AND m.isDeleted = false
            AND
            (
                LOWER(COALESCE(m.content, ''))
                LIKE LOWER(CONCAT('%', :keyword, '%'))

                OR

                LOWER(COALESCE(m.fileName, ''))
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        ORDER BY m.sentAt ASC
        """)
    List<Message> searchGroupMessages(
            @Param("groupId") Long groupId,
            @Param("keyword") String keyword);

}