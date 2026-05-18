package com.login.service;

import com.login.model.ChatMessage;
import com.login.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chat Service - Business logic for chat operations
 * 
 * @author Bob
 */
@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    /**
     * Save a new chat message
     */
    public ChatMessage saveMessage(String username, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUsername(username);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setMessageType("USER");
        
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * Get all messages (limited to recent 50)
     */
    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findTop50ByOrderByTimestampDesc();
    }

    /**
     * Get messages by username
     */
    public List<ChatMessage> getMessagesByUsername(String username) {
        return chatMessageRepository.findByUsernameOrderByTimestampDesc(username);
    }

    /**
     * Delete a message by ID
     */
    public void deleteMessage(Long id) {
        chatMessageRepository.deleteById(id);
    }

    /**
     * Get message by ID
     */
    public ChatMessage getMessageById(Long id) {
        return chatMessageRepository.findById(id).orElse(null);
    }
}

// Made with Bob