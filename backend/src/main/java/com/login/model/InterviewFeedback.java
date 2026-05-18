package com.login.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * InterviewFeedback Entity - Technical Interview Assessment Form
 * Stores comprehensive feedback from panelists about candidate interviews
 *
 * @author Bob
 */
@Entity
@Table(name = "interview_feedback")
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long interviewId;

    @Column(nullable = false)
    private Long panelistId;

    @Column(nullable = false)
    private Long candidateId;

    // Basic Information
    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false)
    private String source; // External Hire, Internal, etc.

    @Column(nullable = false)
    private Double yearsOfExperience;

    @Column(nullable = false)
    private Double yearsOfExperienceInTech; // e.g., AWS

    @Column(nullable = false)
    private String evaluationType; // VENDOR / SELF / IBM REFERAL / OTHERS

    @Column(nullable = false)
    private String evaluatorNames;

    @Column(nullable = false)
    private LocalDate evaluationDate;

    @Column(nullable = false)
    private String jobRoleSpecification;

    @Column(length = 1000)
    private String jobDescription;

    @Column(nullable = false)
    private String accountName;

    // Job Level (SE, SSE, TL/ML, Architect)
    @Column(nullable = false)
    private String jobLevel; // SE, SSE, TL, ML, Architect

    // Soft Skills
    private Double communicationRating;

    // Technical Skills Ratings (0-10 scale)
    private Double awsNativeServicesRating;
    private Double awsIntegrationServicesRating;
    private Double awsComputeServicesRating;
    private Double programmingLanguageRating; // .NET/Java/Python/Node.js
    private Double awsDevOpsServicesRating;
    private Double awsStorageRating;
    private Double agileScrumRating;
    private Double awsCliRating;
    private Double deploymentManagementRating;
    private Double containerOrchestrationRating; // EKS/ECS/Kubernetes
    private Double microservicesDesignPatternsRating;
    private Double microservicesCommunicationRating;
    private Double disasterRecoveryRating;
    private Double containerizationRating; // Docker/Podman
    private Double iacRating; // Terraform/CloudFormation
    private Double frontendStackRating; // Angular/React/Node
    private Double htmlCssRating;
    private Double springCloudAwsRating;
    private Double sqlTuningRating;
    private Double setupPackagingRating;

    // Certifications
    @Column(length = 1000)
    private String certifications;

    // Architecting and Solutioning (for TL/ML/Architect)
    private Double estimationRating;
    private Double architectureRating;
    private Double solutioningRating;
    private Double deliveryMethodologiesRating;
    private Double operationsRating;
    private Double customerHandlingRating;
    private Double otherManagementSkillsRating;

    // Panel Notes for each skill
    @Column(length = 2000)
    private String awsNativeServicesNotes;

    @Column(length = 2000)
    private String technicalSkillsNotes;

    // Overall Rating
    @Column(nullable = false)
    private Double overallRating;

    // Tool Recommendation
    @Column(nullable = false)
    private String toolRecommendation; // Selected/Rejected

    // Tech Panel Recommendation
    @Column(nullable = false)
    private String techPanelRecommendation; // Selected/Rejected

    // Feedback
    @Column(length = 5000, nullable = false)
    private String overallFeedback;

    @Column(length = 2000)
    private String suitabilityForRequirement;

    @Column(length = 2000)
    private String improvementFocusArea;

    // Declaration
    @Column(nullable = false)
    private Boolean declarationAccepted = false;

    @Column(nullable = false)
    private String evaluatorSignature;

    // Status
    @Column(nullable = false)
    private String status = "SUBMITTED"; // SUBMITTED, REVIEWED, APPROVED, REJECTED

    @Column(nullable = false)
    private Boolean sentToHR = false;

    @Column
    private LocalDateTime sentToHRAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
    public InterviewFeedback() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Double getYearsOfExperienceInTech() {
        return yearsOfExperienceInTech;
    }

    public void setYearsOfExperienceInTech(Double yearsOfExperienceInTech) {
        this.yearsOfExperienceInTech = yearsOfExperienceInTech;
    }

    public String getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(String evaluationType) {
        this.evaluationType = evaluationType;
    }

    public String getEvaluatorNames() {
        return evaluatorNames;
    }

    public void setEvaluatorNames(String evaluatorNames) {
        this.evaluatorNames = evaluatorNames;
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getJobRoleSpecification() {
        return jobRoleSpecification;
    }

    public void setJobRoleSpecification(String jobRoleSpecification) {
        this.jobRoleSpecification = jobRoleSpecification;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Double getCommunicationRating() {
        return communicationRating;
    }

    public void setCommunicationRating(Double communicationRating) {
        this.communicationRating = communicationRating;
    }

    public Double getAwsNativeServicesRating() {
        return awsNativeServicesRating;
    }

    public void setAwsNativeServicesRating(Double awsNativeServicesRating) {
        this.awsNativeServicesRating = awsNativeServicesRating;
    }

    public Double getAwsIntegrationServicesRating() {
        return awsIntegrationServicesRating;
    }

    public void setAwsIntegrationServicesRating(Double awsIntegrationServicesRating) {
        this.awsIntegrationServicesRating = awsIntegrationServicesRating;
    }

    public Double getAwsComputeServicesRating() {
        return awsComputeServicesRating;
    }

    public void setAwsComputeServicesRating(Double awsComputeServicesRating) {
        this.awsComputeServicesRating = awsComputeServicesRating;
    }

    public Double getProgrammingLanguageRating() {
        return programmingLanguageRating;
    }

    public void setProgrammingLanguageRating(Double programmingLanguageRating) {
        this.programmingLanguageRating = programmingLanguageRating;
    }

    public Double getAwsDevOpsServicesRating() {
        return awsDevOpsServicesRating;
    }

    public void setAwsDevOpsServicesRating(Double awsDevOpsServicesRating) {
        this.awsDevOpsServicesRating = awsDevOpsServicesRating;
    }

    public Double getAwsStorageRating() {
        return awsStorageRating;
    }

    public void setAwsStorageRating(Double awsStorageRating) {
        this.awsStorageRating = awsStorageRating;
    }

    public Double getAgileScrumRating() {
        return agileScrumRating;
    }

    public void setAgileScrumRating(Double agileScrumRating) {
        this.agileScrumRating = agileScrumRating;
    }

    public Double getAwsCliRating() {
        return awsCliRating;
    }

    public void setAwsCliRating(Double awsCliRating) {
        this.awsCliRating = awsCliRating;
    }

    public Double getDeploymentManagementRating() {
        return deploymentManagementRating;
    }

    public void setDeploymentManagementRating(Double deploymentManagementRating) {
        this.deploymentManagementRating = deploymentManagementRating;
    }

    public Double getContainerOrchestrationRating() {
        return containerOrchestrationRating;
    }

    public void setContainerOrchestrationRating(Double containerOrchestrationRating) {
        this.containerOrchestrationRating = containerOrchestrationRating;
    }

    public Double getMicroservicesDesignPatternsRating() {
        return microservicesDesignPatternsRating;
    }

    public void setMicroservicesDesignPatternsRating(Double microservicesDesignPatternsRating) {
        this.microservicesDesignPatternsRating = microservicesDesignPatternsRating;
    }

    public Double getMicroservicesCommunicationRating() {
        return microservicesCommunicationRating;
    }

    public void setMicroservicesCommunicationRating(Double microservicesCommunicationRating) {
        this.microservicesCommunicationRating = microservicesCommunicationRating;
    }

    public Double getDisasterRecoveryRating() {
        return disasterRecoveryRating;
    }

    public void setDisasterRecoveryRating(Double disasterRecoveryRating) {
        this.disasterRecoveryRating = disasterRecoveryRating;
    }

    public Double getContainerizationRating() {
        return containerizationRating;
    }

    public void setContainerizationRating(Double containerizationRating) {
        this.containerizationRating = containerizationRating;
    }

    public Double getIacRating() {
        return iacRating;
    }

    public void setIacRating(Double iacRating) {
        this.iacRating = iacRating;
    }

    public Double getFrontendStackRating() {
        return frontendStackRating;
    }

    public void setFrontendStackRating(Double frontendStackRating) {
        this.frontendStackRating = frontendStackRating;
    }

    public Double getHtmlCssRating() {
        return htmlCssRating;
    }

    public void setHtmlCssRating(Double htmlCssRating) {
        this.htmlCssRating = htmlCssRating;
    }

    public Double getSpringCloudAwsRating() {
        return springCloudAwsRating;
    }

    public void setSpringCloudAwsRating(Double springCloudAwsRating) {
        this.springCloudAwsRating = springCloudAwsRating;
    }

    public Double getSqlTuningRating() {
        return sqlTuningRating;
    }

    public void setSqlTuningRating(Double sqlTuningRating) {
        this.sqlTuningRating = sqlTuningRating;
    }

    public Double getSetupPackagingRating() {
        return setupPackagingRating;
    }

    public void setSetupPackagingRating(Double setupPackagingRating) {
        this.setupPackagingRating = setupPackagingRating;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public Double getEstimationRating() {
        return estimationRating;
    }

    public void setEstimationRating(Double estimationRating) {
        this.estimationRating = estimationRating;
    }

    public Double getArchitectureRating() {
        return architectureRating;
    }

    public void setArchitectureRating(Double architectureRating) {
        this.architectureRating = architectureRating;
    }

    public Double getSolutioningRating() {
        return solutioningRating;
    }

    public void setSolutioningRating(Double solutioningRating) {
        this.solutioningRating = solutioningRating;
    }

    public Double getDeliveryMethodologiesRating() {
        return deliveryMethodologiesRating;
    }

    public void setDeliveryMethodologiesRating(Double deliveryMethodologiesRating) {
        this.deliveryMethodologiesRating = deliveryMethodologiesRating;
    }

    public Double getOperationsRating() {
        return operationsRating;
    }

    public void setOperationsRating(Double operationsRating) {
        this.operationsRating = operationsRating;
    }

    public Double getCustomerHandlingRating() {
        return customerHandlingRating;
    }

    public void setCustomerHandlingRating(Double customerHandlingRating) {
        this.customerHandlingRating = customerHandlingRating;
    }

    public Double getOtherManagementSkillsRating() {
        return otherManagementSkillsRating;
    }

    public void setOtherManagementSkillsRating(Double otherManagementSkillsRating) {
        this.otherManagementSkillsRating = otherManagementSkillsRating;
    }

    public String getAwsNativeServicesNotes() {
        return awsNativeServicesNotes;
    }

    public void setAwsNativeServicesNotes(String awsNativeServicesNotes) {
        this.awsNativeServicesNotes = awsNativeServicesNotes;
    }

    public String getTechnicalSkillsNotes() {
        return technicalSkillsNotes;
    }

    public void setTechnicalSkillsNotes(String technicalSkillsNotes) {
        this.technicalSkillsNotes = technicalSkillsNotes;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getToolRecommendation() {
        return toolRecommendation;
    }

    public void setToolRecommendation(String toolRecommendation) {
        this.toolRecommendation = toolRecommendation;
    }

    public String getTechPanelRecommendation() {
        return techPanelRecommendation;
    }

    public void setTechPanelRecommendation(String techPanelRecommendation) {
        this.techPanelRecommendation = techPanelRecommendation;
    }

    public String getOverallFeedback() {
        return overallFeedback;
    }

    public void setOverallFeedback(String overallFeedback) {
        this.overallFeedback = overallFeedback;
    }

    public String getSuitabilityForRequirement() {
        return suitabilityForRequirement;
    }

    public void setSuitabilityForRequirement(String suitabilityForRequirement) {
        this.suitabilityForRequirement = suitabilityForRequirement;
    }

    public String getImprovementFocusArea() {
        return improvementFocusArea;
    }

    public void setImprovementFocusArea(String improvementFocusArea) {
        this.improvementFocusArea = improvementFocusArea;
    }

    public Boolean getDeclarationAccepted() {
        return declarationAccepted;
    }

    public void setDeclarationAccepted(Boolean declarationAccepted) {
        this.declarationAccepted = declarationAccepted;
    }

    public String getEvaluatorSignature() {
        return evaluatorSignature;
    }

    public void setEvaluatorSignature(String evaluatorSignature) {
        this.evaluatorSignature = evaluatorSignature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSentToHR() {
        return sentToHR;
    }

    public void setSentToHR(Boolean sentToHR) {
        this.sentToHR = sentToHR;
    }

    public LocalDateTime getSentToHRAt() {
        return sentToHRAt;
    }

    public void setSentToHRAt(LocalDateTime sentToHRAt) {
        this.sentToHRAt = sentToHRAt;
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