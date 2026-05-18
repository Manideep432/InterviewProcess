package com.login.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Interview Entity - Represents an interview scheduled by HR with video meeting capabilities
 *
 * @author Bob
 */
@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hrId;

    @Column(nullable = false)
    private Long panelistId;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String candidateEmail;

    @Column(nullable = false)
    private LocalDate interviewDate;

    @Column(nullable = false)
    private LocalTime interviewTimeFrom;

    @Column(nullable = false)
    private LocalTime interviewTimeTo;

    @Column(length = 2000)
    private String feedback;

    @Column
    private Long technicalFeedbackId; // Link to InterviewFeedback entity

    @Column
    private Boolean hasTechnicalFeedback = false;

    @Column(nullable = false)
    private String position;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    // Video Meeting Fields
    @Column(unique = true)
    private String meetingRoomId;

    @Column
    private String meetingLink;

    @Column
    private LocalDateTime meetingStartTime;

    @Column
    private LocalDateTime meetingEndTime;

    @Column
    private Integer durationMinutes;

    @Column
    private Boolean recordingEnabled = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum InterviewStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        RESCHEDULED
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
    public Interview() {
    }

    public Interview(Long hrId, Long panelistId, Long candidateId, String candidateName, String candidateEmail,
                    LocalDate interviewDate, LocalTime interviewTimeFrom, LocalTime interviewTimeTo, String position, String notes) {
        this.hrId = hrId;
        this.panelistId = panelistId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.interviewDate = interviewDate;
        this.interviewTimeFrom = interviewTimeFrom;
        this.interviewTimeTo = interviewTimeTo;
        this.position = position;
        this.notes = notes;
        this.status = InterviewStatus.SCHEDULED;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public LocalTime getInterviewTimeFrom() {
        return interviewTimeFrom;
    }

    public void setInterviewTimeFrom(LocalTime interviewTimeFrom) {
        this.interviewTimeFrom = interviewTimeFrom;
    }

    public LocalTime getInterviewTimeTo() {
        return interviewTimeTo;
    }

    public void setInterviewTimeTo(LocalTime interviewTimeTo) {
        this.interviewTimeTo = interviewTimeTo;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public LocalDateTime getMeetingStartTime() {
        return meetingStartTime;
    }

    public void setMeetingStartTime(LocalDateTime meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public LocalDateTime getMeetingEndTime() {
        return meetingEndTime;
    }

    public void setMeetingEndTime(LocalDateTime meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
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

    public Long getTechnicalFeedbackId() {
        return technicalFeedbackId;
    }

    public void setTechnicalFeedbackId(Long technicalFeedbackId) {
        this.technicalFeedbackId = technicalFeedbackId;
    }

    public Boolean getHasTechnicalFeedback() {
        return hasTechnicalFeedback;
    }

    public void setHasTechnicalFeedback(Boolean hasTechnicalFeedback) {
        this.hasTechnicalFeedback = hasTechnicalFeedback;
    }
}

// Made with Bob