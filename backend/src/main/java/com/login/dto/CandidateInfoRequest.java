package com.login.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for Candidate Information Request
 * Used when candidates submit their personal information
 */
public class CandidateInfoRequest {
    private String candidateName;
    private String mailId;
    private String phoneNumber;
    private String location;
    private String currentCtc;
    private String hrMailId;
    private String position;
    private Integer experienceYears;
    private String skills;
    private MultipartFile photo;
    private MultipartFile cv;
    private MultipartFile gvtId;

    // Constructors
    public CandidateInfoRequest() {
    }

    public CandidateInfoRequest(String candidateName, String mailId, String phoneNumber, String location,
                                String currentCtc, String hrMailId, String position, Integer experienceYears, String skills) {
        this.candidateName = candidateName;
        this.mailId = mailId;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.currentCtc = currentCtc;
        this.hrMailId = hrMailId;
        this.position = position;
        this.experienceYears = experienceYears;
        this.skills = skills;
    }

    // Getters and Setters
    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCurrentCtc() {
        return currentCtc;
    }

    public void setCurrentCtc(String currentCtc) {
        this.currentCtc = currentCtc;
    }

    public String getHrMailId() {
        return hrMailId;
    }

    public void setHrMailId(String hrMailId) {
        this.hrMailId = hrMailId;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }

    public MultipartFile getCv() {
        return cv;
    }

    public void setCv(MultipartFile cv) {
        this.cv = cv;
    }

    public MultipartFile getGvtId() {
        return gvtId;
    }

    public void setGvtId(MultipartFile gvtId) {
        this.gvtId = gvtId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    @Override
    public String toString() {
        return "CandidateInfoRequest{" +
                "candidateName='" + candidateName + '\'' +
                ", mailId='" + mailId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                ", currentCtc='" + currentCtc + '\'' +
                ", hrMailId='" + hrMailId + '\'' +
                ", position='" + position + '\'' +
                ", experienceYears=" + experienceYears +
                ", skills='" + skills + '\'' +
                ", photo=" + (photo != null ? photo.getOriginalFilename() : "null") +
                ", cv=" + (cv != null ? cv.getOriginalFilename() : "null") +
                ", gvtId=" + (gvtId != null ? gvtId.getOriginalFilename() : "null") +
                '}';
    }
}

// Made with Bob
