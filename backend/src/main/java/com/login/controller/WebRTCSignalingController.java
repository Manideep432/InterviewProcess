package com.login.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

/**
 * WebRTC Signaling Controller - Handles WebRTC signaling for video calls
 * Uses WebSocket for real-time communication
 * 
 * @author Bob
 */
@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class WebRTCSignalingController {

    /**
     * Handle WebRTC offer
     */
    @MessageMapping("/meeting/{roomId}/offer")
    @SendTo("/topic/meeting/{roomId}/offer")
    public Map<String, Object> handleOffer(@DestinationVariable String roomId, 
                                          Map<String, Object> offer,
                                          SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        offer.put("senderId", userId);
        offer.put("timestamp", System.currentTimeMillis());
        return offer;
    }

    /**
     * Handle WebRTC answer
     */
    @MessageMapping("/meeting/{roomId}/answer")
    @SendTo("/topic/meeting/{roomId}/answer")
    public Map<String, Object> handleAnswer(@DestinationVariable String roomId, 
                                           Map<String, Object> answer,
                                           SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        answer.put("senderId", userId);
        answer.put("timestamp", System.currentTimeMillis());
        return answer;
    }

    /**
     * Handle ICE candidate
     */
    @MessageMapping("/meeting/{roomId}/ice-candidate")
    @SendTo("/topic/meeting/{roomId}/ice-candidate")
    public Map<String, Object> handleIceCandidate(@DestinationVariable String roomId, 
                                                  Map<String, Object> candidate,
                                                  SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        candidate.put("senderId", userId);
        candidate.put("timestamp", System.currentTimeMillis());
        return candidate;
    }

    /**
     * Handle user joined
     */
    @MessageMapping("/meeting/{roomId}/join")
    @SendTo("/topic/meeting/{roomId}/participants")
    public Map<String, Object> handleUserJoined(@DestinationVariable String roomId, 
                                                Map<String, Object> joinInfo,
                                                SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "user-joined",
            "userId", userId,
            "username", joinInfo.getOrDefault("username", "Unknown"),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle user left
     */
    @MessageMapping("/meeting/{roomId}/leave")
    @SendTo("/topic/meeting/{roomId}/participants")
    public Map<String, Object> handleUserLeft(@DestinationVariable String roomId,
                                              SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "user-left",
            "userId", userId,
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle screen sharing start
     */
    @MessageMapping("/meeting/{roomId}/screen-share/start")
    @SendTo("/topic/meeting/{roomId}/screen-share")
    public Map<String, Object> handleScreenShareStart(@DestinationVariable String roomId,
                                                      SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "screen-share-started",
            "userId", userId,
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle screen sharing stop
     */
    @MessageMapping("/meeting/{roomId}/screen-share/stop")
    @SendTo("/topic/meeting/{roomId}/screen-share")
    public Map<String, Object> handleScreenShareStop(@DestinationVariable String roomId,
                                                     SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "screen-share-stopped",
            "userId", userId,
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle audio toggle
     */
    @MessageMapping("/meeting/{roomId}/audio/toggle")
    @SendTo("/topic/meeting/{roomId}/audio")
    public Map<String, Object> handleAudioToggle(@DestinationVariable String roomId,
                                                 Map<String, Object> audioState,
                                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "audio-toggled",
            "userId", userId,
            "muted", audioState.get("muted"),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle video toggle
     */
    @MessageMapping("/meeting/{roomId}/video/toggle")
    @SendTo("/topic/meeting/{roomId}/video")
    public Map<String, Object> handleVideoToggle(@DestinationVariable String roomId,
                                                 Map<String, Object> videoState,
                                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "type", "video-toggled",
            "userId", userId,
            "disabled", videoState.get("disabled"),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle annotation/drawing events
     */
    @MessageMapping("/meeting/{roomId}/annotation")
    @SendTo("/topic/meeting/{roomId}/annotation")
    public Map<String, Object> handleAnnotation(@DestinationVariable String roomId,
                                               Map<String, Object> annotation,
                                               SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        annotation.put("userId", userId);
        annotation.put("timestamp", System.currentTimeMillis());
        return annotation;
    }

    /**
     * Handle chat messages in meeting
     */
    @MessageMapping("/meeting/{roomId}/chat")
    @SendTo("/topic/meeting/{roomId}/chat")
    public Map<String, Object> handleMeetingChat(@DestinationVariable String roomId,
                                                 Map<String, Object> message,
                                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        return Map.of(
            "userId", userId,
            "username", message.getOrDefault("username", "Unknown"),
            "message", message.get("message"),
            "timestamp", System.currentTimeMillis()
        );
    }

    /**
     * Handle meeting control events (mute all, end meeting, etc.)
     */
    @MessageMapping("/meeting/{roomId}/control")
    @SendTo("/topic/meeting/{roomId}/control")
    public Map<String, Object> handleMeetingControl(@DestinationVariable String roomId,
                                                    Map<String, Object> control,
                                                    SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        control.put("controllerId", userId);
        control.put("timestamp", System.currentTimeMillis());
        return control;
    }
}

// Made with Bob