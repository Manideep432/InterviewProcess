package com.login.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * VideoMeeting Entity - Represents a video meeting session with WebRTC capabilities
 * 
 * @author Bob
 */
@Entity
@Table(name = "video_meetings")
public class VideoMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private Long hrId;

    @Column(nullable = false)
    private Long panelistId;

    @Column(nullable = false)
    private Long candidateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status = MeetingStatus.SCHEDULED;

    @Column
    private LocalDateTime scheduledStartTime;

    @Column
    private LocalDateTime actualStartTime;

    @Column
    private LocalDateTime actualEndTime;

    @Column
    private Integer durationMinutes;

    @Column
    private Boolean recordingEnabled = false;

    @Column
    private String recordingUrl;

    // Participants who have joined
    @ElementCollection
    @CollectionTable(name = "meeting_participants", joinColumns = @JoinColumn(name = "meeting_id"))
    @Column(name = "user_id")
    private Set<Long> activeParticipants = new HashSet<>();

    // Meeting settings
    @Column
    private Boolean screenSharingEnabled = true;

    @Column
    private Boolean chatEnabled = true;

    @Column
    private Boolean annotationEnabled = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum MeetingStatus {
        SCHEDULED,
        WAITING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public VideoMeeting() {
    }

    public VideoMeeting(String roomId, Long interviewId, Long hrId, Long panelistId, Long candidateId) {
        this.roomId = roomId;
        this.interviewId = interviewId;
        this.hrId = hrId;
        this.panelistId = panelistId;
        this.candidateId = candidateId;
        this.status = MeetingStatus.SCHEDULED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getHrId() {
        return hrId;
    }

    public void setHrId(Long hrId) {
        this.hrId = hrId;
    }

    public Long getPanelistId() {
        return panelistId;
    }

    public void setPanelistId(Long panelistId) {
        this.panelistId = panelistId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public LocalDateTime getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(LocalDateTime scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public LocalDateTime getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(LocalDateTime actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(LocalDateTime actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getRecordingEnabled() {
        return recordingEnabled;
    }

    public void setRecordingEnabled(Boolean recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public Set<Long> getActiveParticipants() {
        return activeParticipants;
    }

    public void setActiveParticipants(Set<Long> activeParticipants) {
        this.activeParticipants = activeParticipants;
    }

    public Boolean getScreenSharingEnabled() {
        return screenSharingEnabled;
    }

    public void setScreenSharingEnabled(Boolean screenSharingEnabled) {
        this.screenSharingEnabled = screenSharingEnabled;
    }

    public Boolean getChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(Boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }

    public Boolean getAnnotationEnabled() {
        return annotationEnabled;
    }

    public void setAnnotationEnabled(Boolean annotationEnabled) {
        this.annotationEnabled = annotationEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public void addParticipant(Long userId) {
        this.activeParticipants.add(userId);
    }

    public void removeParticipant(Long userId) {
        this.activeParticipants.remove(userId);
    }

    public boolean isParticipantActive(Long userId) {
        return this.activeParticipants.contains(userId);
    }

    public int getParticipantCount() {
        return this.activeParticipants.size();
    }
}

// Made with Bob