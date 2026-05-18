package com.login.controller;

import com.login.model.VideoMeeting;
import com.login.model.VideoMeeting.MeetingStatus;
import com.login.service.VideoMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * VideoMeeting Controller - REST API endpoints for video meeting management
 * 
 * @author Bob
 */
@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoMeetingController {

    @Autowired
    private VideoMeetingService videoMeetingService;

    /**
     * Create a new video meeting
     */
    @PostMapping("/create")
    public ResponseEntity<?> createMeeting(@RequestBody Map<String, Object> request) {
        try {
            Long interviewId = Long.valueOf(request.get("interviewId").toString());
            Long hrId = Long.valueOf(request.get("hrId").toString());
            Long panelistId = Long.valueOf(request.get("panelistId").toString());
            Long candidateId = Long.valueOf(request.get("candidateId").toString());
            LocalDateTime scheduledStartTime = LocalDateTime.parse(request.get("scheduledStartTime").toString());
            Integer durationMinutes = Integer.valueOf(request.get("durationMinutes").toString());

            VideoMeeting meeting = videoMeetingService.createMeeting(
                interviewId, hrId, panelistId, candidateId, scheduledStartTime, durationMinutes
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Meeting created successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get meeting by room ID
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getMeetingByRoomId(@PathVariable String roomId) {
        try {
            return videoMeetingService.getMeetingByRoomId(roomId)
                .map(meeting -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "meeting", meeting
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Meeting not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get meeting by interview ID
     */
    @GetMapping("/interview/{interviewId}")
    public ResponseEntity<?> getMeetingByInterviewId(@PathVariable Long interviewId) {
        try {
            return videoMeetingService.getMeetingByInterviewId(interviewId)
                .map(meeting -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "meeting", meeting
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Meeting not found"
                )));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all meetings by HR
     */
    @GetMapping("/hr/{hrId}")
    public ResponseEntity<?> getMeetingsByHr(@PathVariable Long hrId) {
        try {
            List<VideoMeeting> meetings = videoMeetingService.getMeetingsByHr(hrId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "meetings", meetings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all meetings by panelist
     */
    @GetMapping("/panelist/{panelistId}")
    public ResponseEntity<?> getMeetingsByPanelist(@PathVariable Long panelistId) {
        try {
            List<VideoMeeting> meetings = videoMeetingService.getMeetingsByPanelist(panelistId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "meetings", meetings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all meetings by candidate
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getMeetingsByCandidate(@PathVariable Long candidateId) {
        try {
            List<VideoMeeting> meetings = videoMeetingService.getMeetingsByCandidate(candidateId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "meetings", meetings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Join meeting
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinMeeting(@RequestBody Map<String, Object> request) {
        try {
            String roomId = request.get("roomId").toString();
            Long userId = Long.valueOf(request.get("userId").toString());

            VideoMeeting meeting = videoMeetingService.joinMeeting(roomId, userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Joined meeting successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Leave meeting
     */
    @PostMapping("/leave")
    public ResponseEntity<?> leaveMeeting(@RequestBody Map<String, Object> request) {
        try {
            String roomId = request.get("roomId").toString();
            Long userId = Long.valueOf(request.get("userId").toString());

            VideoMeeting meeting = videoMeetingService.leaveMeeting(roomId, userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Left meeting successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * End meeting
     */
    @PostMapping("/end")
    public ResponseEntity<?> endMeeting(@RequestBody Map<String, Object> request) {
        try {
            String roomId = request.get("roomId").toString();
            Long userId = Long.valueOf(request.get("userId").toString());

            VideoMeeting meeting = videoMeetingService.endMeeting(roomId, userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Meeting ended successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Cancel meeting
     */
    @PostMapping("/{meetingId}/cancel")
    public ResponseEntity<?> cancelMeeting(@PathVariable Long meetingId, @RequestParam Long userId) {
        try {
            VideoMeeting meeting = videoMeetingService.cancelMeeting(meetingId, userId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Meeting cancelled successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Toggle recording
     */
    @PostMapping("/recording")
    public ResponseEntity<?> toggleRecording(@RequestBody Map<String, Object> request) {
        try {
            String roomId = request.get("roomId").toString();
            Long userId = Long.valueOf(request.get("userId").toString());
            Boolean enable = Boolean.valueOf(request.get("enable").toString());

            VideoMeeting meeting = videoMeetingService.toggleRecording(roomId, userId, enable);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", enable ? "Recording started" : "Recording stopped",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Update meeting settings
     */
    @PutMapping("/settings")
    public ResponseEntity<?> updateMeetingSettings(@RequestBody Map<String, Object> request) {
        try {
            String roomId = request.get("roomId").toString();
            Long userId = Long.valueOf(request.get("userId").toString());
            Boolean screenSharing = request.containsKey("screenSharing") ? 
                Boolean.valueOf(request.get("screenSharing").toString()) : null;
            Boolean chat = request.containsKey("chat") ? 
                Boolean.valueOf(request.get("chat").toString()) : null;
            Boolean annotation = request.containsKey("annotation") ? 
                Boolean.valueOf(request.get("annotation").toString()) : null;

            VideoMeeting meeting = videoMeetingService.updateMeetingSettings(
                roomId, userId, screenSharing, chat, annotation
            );

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Meeting settings updated successfully",
                "meeting", meeting
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get active meetings
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveMeetings() {
        try {
            List<VideoMeeting> meetings = videoMeetingService.getActiveMeetings();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "meetings", meetings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get today's meetings
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodaysMeetings() {
        try {
            List<VideoMeeting> meetings = videoMeetingService.getTodaysMeetings();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "meetings", meetings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get meeting statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getMeetingStats(@RequestParam(required = false) Long hrId) {
        try {
            Map<String, Object> stats;
            
            if (hrId != null) {
                stats = Map.of(
                    "scheduled", videoMeetingService.getHrMeetingCountByStatus(hrId, MeetingStatus.SCHEDULED),
                    "inProgress", videoMeetingService.getHrMeetingCountByStatus(hrId, MeetingStatus.IN_PROGRESS),
                    "completed", videoMeetingService.getHrMeetingCountByStatus(hrId, MeetingStatus.COMPLETED),
                    "cancelled", videoMeetingService.getHrMeetingCountByStatus(hrId, MeetingStatus.CANCELLED)
                );
            } else {
                stats = Map.of(
                    "scheduled", videoMeetingService.getMeetingCountByStatus(MeetingStatus.SCHEDULED),
                    "inProgress", videoMeetingService.getMeetingCountByStatus(MeetingStatus.IN_PROGRESS),
                    "completed", videoMeetingService.getMeetingCountByStatus(MeetingStatus.COMPLETED),
                    "cancelled", videoMeetingService.getMeetingCountByStatus(MeetingStatus.CANCELLED)
                );
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}

// Made with Bob