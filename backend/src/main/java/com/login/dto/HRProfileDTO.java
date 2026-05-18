package com.login.dto;

import java.time.LocalDateTime;

/**
 * HR Profile DTO - Data Transfer Object for HR Profile
 *
 * @author Bob
 */
public class HRProfileDTO {
    
    private Long id;
    private Long userId;
    private String username;
    private String email;
    
    // Personal Information
    private String fullName;
    private String phone;
    private String location;
    private String address;
    
    // Professional Details
    private String designation;
    private String department;
    private String employeeId;
    private Integer experienceYears;
    private String company;
    private String bio;
    
    // Contact Information
    private String linkedinUrl;
    private String slackHandle;
    private String emergencyContact;
    private String emergencyPhone;
    
    // Skills and Certifications
    private String skills;
    private String certifications;
    private String education;
    
    // Business Information
    private String workType;
    private String teamName;
    private String reportingManager;
    private String hrSpecialization;
    
    // Additional HR-specific fields
    private String region;
    private Integer totalCandidatesManaged;
    private Integer totalPanelistsManaged;
    
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public HRProfileDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
}

// Made with Bob