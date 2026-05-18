package com.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Panelist Entity - Represents a panelist assigned by HR
 *
 * @author Bob
 */
@Entity
@Table(name = "panelists")
public class Panelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"password", "mfaSecret", "createdAt", "updatedAt", "active", "mfaEnabled"})
    private User user; // User account with PANELIST role

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_hr_id", nullable = false)
    @JsonIgnoreProperties({"password", "mfaSecret", "createdAt", "updatedAt", "active", "mfaEnabled", "hibernateLazyInitializer", "handler"})
    private User assignedHr; // HR who assigned this panelist

    // Personal Information
    @Column(length = 100)
    private String fullName;

    @Column(length = 100)
    private String email;

    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 500)
    private String expertise;

    // Contact Information
    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String linkedinUrl;

    @Column(length = 100)
    private String slackHandle;

    // Professional Details
    @Column(length = 100)
    private String designation;

    @Column(length = 100)
    private String company;

    @Column(length = 1000)
    private String bio;

    // Skills and Certifications
    @Column(length = 500)
    private String skills;

    @Column(length = 500)
    private String certifications;

    @Column(length = 500)
    private String education;

    // Business Information
    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String employeeId;

    @Column(length = 50)
    private String workType; // Remote, Hybrid, On-site

    // Team Details
    @Column(length = 100)
    private String teamName;

    @Column(length = 100)
    private String reportingManager;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Panelist() {
    }

    public Panelist(User user, User assignedHr, String specialization) {
        this.user = user;
        this.assignedHr = assignedHr;
        this.specialization = specialization;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssignedHr() {
        return assignedHr;
    }

    public void setAssignedHr(User assignedHr) {
        this.assignedHr = assignedHr;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @JsonIgnore
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getReportingManager() {
        return reportingManager;
    }

    public void setReportingManager(String reportingManager) {
        this.reportingManager = reportingManager;
    }

    public String getSlackHandle() {
        return slackHandle;
    }

    public void setSlackHandle(String slackHandle) {
        this.slackHandle = slackHandle;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Panelist{" +
                "id=" + id +
                ", specialization='" + specialization + '\'' +
                ", experienceYears=" + experienceYears +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}

// Made with Bob