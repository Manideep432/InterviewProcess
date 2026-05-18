package com.login.dto;

/**
 * PanelistProfileDTO - Data Transfer Object for Panelist Profile
 * 
 * @author Bob
 */
public class PanelistProfileDTO {
    
    // Basic Info
    private Long id;
    private String username;
    private String email;
    private String specialization;
    private Integer experienceYears;
    private String expertise;
    
    // Contact Information
    private String phone;
    private String location;
    private String linkedinUrl;
    private String slackHandle;
    
    // Professional Details
    private String designation;
    private String company;
    private String bio;
    
    // Skills and Certifications
    private String skills;
    private String certifications;
    private String education;
    
    // Business Information
    private String department;
    private String employeeId;
    private String workType;
    
    // Team Details
    private String teamName;
    private String reportingManager;
    
    // Status
    private boolean active;
    private String assignedHrName;
    
    // Constructors
    public PanelistProfileDTO() {
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
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getAssignedHrName() {
        return assignedHrName;
    }
    
    public void setAssignedHrName(String assignedHrName) {
        this.assignedHrName = assignedHrName;
    }
    
    public String getSlackHandle() {
        return slackHandle;
    }
    
    public void setSlackHandle(String slackHandle) {
        this.slackHandle = slackHandle;
    }
}

// Made with Bob