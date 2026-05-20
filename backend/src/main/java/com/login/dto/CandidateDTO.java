package com.login.dto;

import com.login.model.Candidate;
import com.login.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Candidate Data Transfer Object
 * Used to send candidate information to frontend with all fields
 * 
 * @author Bob
 */
public class CandidateDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String position;
    private String status;
    private Integer experienceYears;
    private String skills;
    private String jdDetails;
    private LocalDate joiningDate;
    private BigDecimal oldCtc;
    private BigDecimal newCtc;
    private String employmentType;
    private String jrs;
    private String candidateType;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PanelistInfo assignedPanelist;

    // Inner class for panelist information
    public static class PanelistInfo {
        private Long id;
        private String username;
        private String email;

        public PanelistInfo(User user) {
            if (user != null) {
                this.id = user.getId();
                this.username = user.getUsername();
                this.email = user.getEmail();
            }
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Constructor from Candidate entity
    public CandidateDTO(Candidate candidate) {
        this.id = candidate.getId();
        this.name = candidate.getName();
        this.email = candidate.getEmail();
        this.phone = candidate.getPhone();
        this.position = candidate.getPosition();
        this.status = candidate.getStatus();
        this.experienceYears = candidate.getExperienceYears();
        this.skills = candidate.getSkills();
        this.jdDetails = candidate.getJdDetails();
        this.joiningDate = candidate.getJoiningDate();
        this.oldCtc = candidate.getOldCtc();
        this.newCtc = candidate.getNewCtc();
        this.employmentType = candidate.getEmploymentType();
        this.jrs = candidate.getJrs();
        this.candidateType = candidate.getCandidateType();
        this.location = candidate.getLocation();
        this.createdAt = candidate.getCreatedAt();
        this.updatedAt = candidate.getUpdatedAt();
        
        // Set assigned panelist if exists
        if (candidate.getAssignedPanelist() != null) {
            this.assignedPanelist = new PanelistInfo(candidate.getAssignedPanelist());
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getJdDetails() {
        return jdDetails;
    }

    public void setJdDetails(String jdDetails) {
        this.jdDetails = jdDetails;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public BigDecimal getOldCtc() {
        return oldCtc;
    }

    public void setOldCtc(BigDecimal oldCtc) {
        this.oldCtc = oldCtc;
    }

    public BigDecimal getNewCtc() {
        return newCtc;
    }

    public void setNewCtc(BigDecimal newCtc) {
        this.newCtc = newCtc;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJrs() {
        return jrs;
    }

    public void setJrs(String jrs) {
        this.jrs = jrs;
    }

    public String getCandidateType() {
        return candidateType;
    }

    public void setCandidateType(String candidateType) {
        this.candidateType = candidateType;
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

    public PanelistInfo getAssignedPanelist() {
        return assignedPanelist;
    }

    public void setAssignedPanelist(PanelistInfo assignedPanelist) {
        this.assignedPanelist = assignedPanelist;
    }
}

// Made with Bob