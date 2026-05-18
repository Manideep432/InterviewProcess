package com.login.repository;

import com.login.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Chat Message Repository
 * 
 * @author Bob
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Find all messages ordered by timestamp descending (newest first)
     */
    List<ChatMessage> findAllByOrderByTimestampDesc();
    
    /**
     * Find messages by username
     */
    List<ChatMessage> findByUsernameOrderByTimestampDesc(String username);
    
    /**
     * Find recent messages (limit)
     */
    List<ChatMessage> findTop50ByOrderByTimestampDesc();
}

// Made with Bob