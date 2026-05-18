package com.login.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * HR Profile Entity - Represents HR profile information
 *
 * @author Bob
 */
@Entity
@Table(name = "hr_profiles")
public class HRProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"password", "mfaSecret", "createdAt", "updatedAt", "active", "mfaEnabled"})
    private User user; // User account with HR role

    // Personal Information
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String location;

    @Column(length = 200)
    private String address;

    // Professional Details
    @Column(length = 100)
    private String designation;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String employeeId;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 100)
    private String company;

    @Column(length = 1000)
    private String bio;

    // Contact Information
    @Column(length = 100)
    private String linkedinUrl;

    @Column(length = 100)
    private String slackHandle;

    @Column(length = 100)
    private String emergencyContact;

    @Column(length = 20)
    private String emergencyPhone;

    // Skills and Certifications
    @Column(length = 500)
    private String skills;

    @Column(length = 500)
    private String certifications;

    @Column(length = 500)
    private String education;

    // Business Information
    @Column(length = 50)
    private String workType; // Remote, Hybrid, On-site

    @Column(length = 100)
    private String teamName;

    @Column(length = 100)
    private String reportingManager;

    @Column(length = 100)
    private String hrSpecialization; // Recruitment, Training, Payroll, etc.

    // Additional HR-specific fields
    @Column(length = 100)
    private String region; // Geographic region managed

    @Column(name = "total_candidates_managed")
    private Integer totalCandidatesManaged = 0;

    @Column(name = "total_panelists_managed")
    private Integer totalPanelistsManaged = 0;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public HRProfile() {
    }

    public HRProfile(User user, String fullName) {
        this.user = user;
        this.fullName = fullName;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
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

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getSlackHandle() {
        return slackHandle;
    }

    public void setSlackHandle(String slackHandle) {
        this.slackHandle = slackHandle;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
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

    public String getHrSpecialization() {
        return hrSpecialization;
    }

    public void setHrSpecialization(String hrSpecialization) {
        this.hrSpecialization = hrSpecialization;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getTotalCandidatesManaged() {
        return totalCandidatesManaged;
    }

    public void setTotalCandidatesManaged(Integer totalCandidatesManaged) {
        this.totalCandidatesManaged = totalCandidatesManaged;
    }

    public Integer getTotalPanelistsManaged() {
        return totalPanelistsManaged;
    }

    public void setTotalPanelistsManaged(Integer totalPanelistsManaged) {
        this.totalPanelistsManaged = totalPanelistsManaged;
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

    @Override
    public String toString() {
        return "HRProfile{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", designation='" + designation + '\'' +
                ", department='" + department + '\'' +
                ", experienceYears=" + experienceYears +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}

// Made with Bob