package com.login.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Candidate Entity - Represents a candidate in the system
 *
 * @author Bob
 */
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String position;

    @Column(length = 50)
    private String status; // APPLIED, SCREENING, INTERVIEW, SELECTED, REJECTED

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 500)
    private String skills;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User hr; // HR who manages this candidate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_panelist_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User assignedPanelist; // Panelist assigned to interview this candidate

    // New fields for HR Dashboard
    @Column(name = "jd_details", length = 2000)
    private String jdDetails; // Job Description Details

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "old_ctc", precision = 10, scale = 2)
    private BigDecimal oldCtc; // Old CTC (Cost to Company)

    @Column(name = "new_ctc", precision = 10, scale = 2)
    private BigDecimal newCtc; // New CTC (Cost to Company)
    
    @Column(name = "current_ctc", precision = 10, scale = 2)
    private BigDecimal currentCtc; // Current CTC provided by candidate

    @Column(name = "hr_mail_id", length = 100)
    private String hrMailId; // HR email ID to send candidate details

    @Column(name = "employment_type", length = 50)
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERN

    @Column(length = 100)
    private String jrs; // Job Requisition System or Job Reference System

    @Column(name = "candidate_type", length = 50)
    private String candidateType; // INTERNAL, EXTERNAL, REFERRAL, etc.

    @Column(length = 100)
    private String location;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_logged_in")
    private Boolean isLoggedIn = false;

    // Constructors
    public Candidate() {
    }

    public Candidate(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public User getHr() {
        return hr;
    }

    public void setHr(User hr) {
        this.hr = hr;
    }

    public User getAssignedPanelist() {
        return assignedPanelist;
    }

    public void setAssignedPanelist(User assignedPanelist) {
        this.assignedPanelist = assignedPanelist;
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

    public BigDecimal getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(BigDecimal currentCtc) {
        this.currentCtc = currentCtc;
    }

    public String getHrMailId() {
        return hrMailId;
    }

    public void setHrMailId(String hrMailId) {
        this.hrMailId = hrMailId;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(Boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

// Made with Bob