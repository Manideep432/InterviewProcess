package com.login.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Candidate Interview Details
 * Contains interview information along with HR details who scheduled it
 * 
 * @author Bob
 */
public class CandidateInterviewDTO {
    
    private Long interviewId;
    private String jrs;
    private LocalDate interviewDate;
    private LocalTime interviewTimeFrom;
    private LocalTime interviewTimeTo;
    private String status;
    private String feedback;
    
    // HR Details
    private Long hrId;
    private String hrName;
    private String hrEmail;
    private String hrPhone;
    private String hrDesignation;
    
    // Panelist Details
    private Long panelistId;
    private String panelistName;
    private String panelistEmail;
    
    // Meeting Details
    private String meetingLink;
    private String meetingRoomId;
    
    // Constructors
    public CandidateInterviewDTO() {
    }
    
    public CandidateInterviewDTO(Long interviewId, String jrs, LocalDate interviewDate,
                                LocalTime interviewTimeFrom, LocalTime interviewTimeTo,
                                String status, String feedback,
                                Long hrId, String hrName, String hrEmail, String hrPhone, String hrDesignation,
                                Long panelistId, String panelistName, String panelistEmail,
                                String meetingLink, String meetingRoomId) {
        this.interviewId = interviewId;
        this.jrs = jrs;
        this.interviewDate = interviewDate;
        this.interviewTimeFrom = interviewTimeFrom;
        this.interviewTimeTo = interviewTimeTo;
        this.status = status;
        this.feedback = feedback;
        this.hrId = hrId;
        this.hrName = hrName;
        this.hrEmail = hrEmail;
        this.hrPhone = hrPhone;
        this.hrDesignation = hrDesignation;
        this.panelistId = panelistId;
        this.panelistName = panelistName;
        this.panelistEmail = panelistEmail;
        this.meetingLink = meetingLink;
        this.meetingRoomId = meetingRoomId;
    }
    
    // Getters and Setters
    public Long getInterviewId() {
        return interviewId;
    }
    
    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }
    
    public String getJrs() {
        return jrs;
    }
    
    public void setJrs(String jrs) {
        this.jrs = jrs;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public Long getHrId() {
        return hrId;
    }
    
    public void setHrId(Long hrId) {
        this.hrId = hrId;
    }
    
    public String getHrName() {
        return hrName;
    }
    
    public void setHrName(String hrName) {
        this.hrName = hrName;
    }
    
    public String getHrEmail() {
        return hrEmail;
    }
    
    public void setHrEmail(String hrEmail) {
        this.hrEmail = hrEmail;
    }
    
    public String getHrPhone() {
        return hrPhone;
    }
    
    public void setHrPhone(String hrPhone) {
        this.hrPhone = hrPhone;
    }
    
    public String getHrDesignation() {
        return hrDesignation;
    }
    
    public void setHrDesignation(String hrDesignation) {
        this.hrDesignation = hrDesignation;
    }
    
    public Long getPanelistId() {
        return panelistId;
    }
    
    public void setPanelistId(Long panelistId) {
        this.panelistId = panelistId;
    }
    
    public String getPanelistName() {
        return panelistName;
    }
    
    public void setPanelistName(String panelistName) {
        this.panelistName = panelistName;
    }
    
    public String getPanelistEmail() {
        return panelistEmail;
    }
    
    public void setPanelistEmail(String panelistEmail) {
        this.panelistEmail = panelistEmail;
    }
    
    public String getMeetingLink() {
        return meetingLink;
    }
    
    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }
    
    public String getMeetingRoomId() {
        return meetingRoomId;
    }
    
    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }
}

// Made with Bob