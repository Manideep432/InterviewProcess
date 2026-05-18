package com.login.controller;

import com.login.model.ChatMessage;
import com.login.security.JwtUtil;
import com.login.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat Controller - REST endpoints for chat operations
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Send a new chat message
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        try {
            // Extract token and username
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Message cannot be empty"));
            }
            
            ChatMessage chatMessage = chatService.saveMessage(username, message);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", chatMessage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send message: " + e.getMessage()));
        }
    }

    /**
     * Get all chat messages
     */
    @GetMapping("/messages")
    public ResponseEntity<?> getAllMessages(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            // Extract token and validate
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            
            List<ChatMessage> messages = chatService.getAllMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch messages: " + e.getMessage()));
        }
    }

    /**
     * Get messages by username
     */
    @GetMapping("/messages/{username}")
    public ResponseEntity<?> getMessagesByUsername(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String username) {
        
        try {
            // Extract token and validate
            String token = authHeader.substring(7);
            String tokenUsername = jwtUtil.extractUsername(token);
            
            if (!jwtUtil.validateToken(token, tokenUsername)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            
            List<ChatMessage> messages = chatService.getMessagesByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch messages: " + e.getMessage()));
        }
    }

    /**
     * Delete a message
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<?> deleteMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            // Extract token and validate
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (!jwtUtil.validateToken(token, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            
            // Check if message exists and belongs to user
            ChatMessage message = chatService.getMessageById(id);
            if (message == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Message not found"));
            }
            
            if (!message.getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own messages"));
            }
            
            chatService.deleteMessage(id);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Message deleted"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete message: " + e.getMessage()));
        }
    }
}

// Made with Bob