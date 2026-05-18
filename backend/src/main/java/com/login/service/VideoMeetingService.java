package com.login.service;

import com.login.model.VideoMeeting;
import com.login.model.VideoMeeting.MeetingStatus;
import com.login.repository.VideoMeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * VideoMeeting Service - Business logic for video meeting management
 * 
 * @author Bob
 */
@Service
@Transactional
public class VideoMeetingService {

    @Autowired
    private VideoMeetingRepository videoMeetingRepository;

    /**
     * Create a new video meeting
     */
    public VideoMeeting createMeeting(Long interviewId, Long hrId, Long panelistId, Long candidateId, 
                                     LocalDateTime scheduledStartTime, Integer durationMinutes) {
        String roomId = generateUniqueRoomId();
        
        VideoMeeting meeting = new VideoMeeting(roomId, interviewId, hrId, panelistId, candidateId);
        meeting.setScheduledStartTime(scheduledStartTime);
        meeting.setDurationMinutes(durationMinutes);
        meeting.setStatus(MeetingStatus.SCHEDULED);
        
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Generate unique room ID
     */
    private String generateUniqueRoomId() {
        String roomId;
        do {
            roomId = "ROOM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (videoMeetingRepository.existsByRoomId(roomId));
        return roomId;
    }

    /**
     * Get meeting by ID
     */
    public Optional<VideoMeeting> getMeetingById(Long id) {
        return videoMeetingRepository.findById(id);
    }

    /**
     * Get meeting by room ID
     */
    public Optional<VideoMeeting> getMeetingByRoomId(String roomId) {
        return videoMeetingRepository.findByRoomId(roomId);
    }

    /**
     * Get meeting by interview ID
     */
    public Optional<VideoMeeting> getMeetingByInterviewId(Long interviewId) {
        return videoMeetingRepository.findByInterviewId(interviewId);
    }

    /**
     * Get all meetings by HR
     */
    public List<VideoMeeting> getMeetingsByHr(Long hrId) {
        return videoMeetingRepository.findByHrId(hrId);
    }

    /**
     * Get all meetings by panelist
     */
    public List<VideoMeeting> getMeetingsByPanelist(Long panelistId) {
        return videoMeetingRepository.findByPanelistId(panelistId);
    }

    /**
     * Get all meetings by candidate
     */
    public List<VideoMeeting> getMeetingsByCandidate(Long candidateId) {
        return videoMeetingRepository.findByCandidateId(candidateId);
    }

    /**
     * Start meeting
     */
    public VideoMeeting startMeeting(String roomId, Long userId) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        if (meeting.getStatus() == MeetingStatus.SCHEDULED) {
            meeting.setStatus(MeetingStatus.WAITING);
        }
        
        if (meeting.getActualStartTime() == null) {
            meeting.setActualStartTime(LocalDateTime.now());
            meeting.setStatus(MeetingStatus.IN_PROGRESS);
        }
        
        meeting.addParticipant(userId);
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Join meeting
     */
    public VideoMeeting joinMeeting(String roomId, Long userId) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        // Verify user is authorized to join
        if (!isUserAuthorized(meeting, userId)) {
            throw new RuntimeException("User not authorized to join this meeting");
        }
        
        meeting.addParticipant(userId);
        
        // If this is the first participant, start the meeting
        if (meeting.getStatus() == MeetingStatus.SCHEDULED && meeting.getParticipantCount() == 1) {
            meeting.setStatus(MeetingStatus.WAITING);
        }
        
        // If multiple participants, set to in progress
        if (meeting.getParticipantCount() > 1 && meeting.getStatus() == MeetingStatus.WAITING) {
            meeting.setStatus(MeetingStatus.IN_PROGRESS);
            if (meeting.getActualStartTime() == null) {
                meeting.setActualStartTime(LocalDateTime.now());
            }
        }
        
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Leave meeting
     */
    public VideoMeeting leaveMeeting(String roomId, Long userId) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        meeting.removeParticipant(userId);
        
        // If no participants left, end the meeting
        if (meeting.getParticipantCount() == 0 && meeting.getStatus() == MeetingStatus.IN_PROGRESS) {
            meeting.setStatus(MeetingStatus.COMPLETED);
            meeting.setActualEndTime(LocalDateTime.now());
        }
        
        return videoMeetingRepository.save(meeting);
    }

    /**
     * End meeting
     */
    public VideoMeeting endMeeting(String roomId, Long userId) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        // Only HR or panelist can end the meeting
        if (!userId.equals(meeting.getHrId()) && !userId.equals(meeting.getPanelistId())) {
            throw new RuntimeException("Only HR or Panelist can end the meeting");
        }
        
        meeting.setStatus(MeetingStatus.COMPLETED);
        meeting.setActualEndTime(LocalDateTime.now());
        meeting.getActiveParticipants().clear();
        
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Cancel meeting
     */
    public VideoMeeting cancelMeeting(Long meetingId, Long userId) {
        VideoMeeting meeting = videoMeetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with ID: " + meetingId));
        
        // Only HR can cancel
        if (!userId.equals(meeting.getHrId())) {
            throw new RuntimeException("Only HR can cancel the meeting");
        }
        
        meeting.setStatus(MeetingStatus.CANCELLED);
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Toggle recording
     */
    public VideoMeeting toggleRecording(String roomId, Long userId, boolean enable) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        // Only HR or panelist can control recording
        if (!userId.equals(meeting.getHrId()) && !userId.equals(meeting.getPanelistId())) {
            throw new RuntimeException("Only HR or Panelist can control recording");
        }
        
        meeting.setRecordingEnabled(enable);
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Update meeting settings
     */
    public VideoMeeting updateMeetingSettings(String roomId, Long userId, 
                                             Boolean screenSharing, Boolean chat, Boolean annotation) {
        VideoMeeting meeting = videoMeetingRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RuntimeException("Meeting not found with room ID: " + roomId));
        
        // Only HR or panelist can update settings
        if (!userId.equals(meeting.getHrId()) && !userId.equals(meeting.getPanelistId())) {
            throw new RuntimeException("Only HR or Panelist can update meeting settings");
        }
        
        if (screenSharing != null) meeting.setScreenSharingEnabled(screenSharing);
        if (chat != null) meeting.setChatEnabled(chat);
        if (annotation != null) meeting.setAnnotationEnabled(annotation);
        
        return videoMeetingRepository.save(meeting);
    }

    /**
     * Get active meetings
     */
    public List<VideoMeeting> getActiveMeetings() {
        return videoMeetingRepository.findByStatusIn(
            List.of(MeetingStatus.WAITING, MeetingStatus.IN_PROGRESS)
        );
    }

    /**
     * Get meetings scheduled for today
     */
    public List<VideoMeeting> getTodaysMeetings() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return videoMeetingRepository.findByScheduledStartTimeBetween(startOfDay, endOfDay);
    }

    /**
     * Check if user is authorized to join meeting
     */
    private boolean isUserAuthorized(VideoMeeting meeting, Long userId) {
        return userId.equals(meeting.getHrId()) || 
               userId.equals(meeting.getPanelistId()) || 
               userId.equals(meeting.getCandidateId());
    }

    /**
     * Get meeting statistics
     */
    public long getMeetingCountByStatus(MeetingStatus status) {
        return videoMeetingRepository.countByStatus(status);
    }

    /**
     * Get HR meeting statistics
     */
    public long getHrMeetingCountByStatus(Long hrId, MeetingStatus status) {
        return videoMeetingRepository.countByHrIdAndStatus(hrId, status);
    }
}

// Made with Bob