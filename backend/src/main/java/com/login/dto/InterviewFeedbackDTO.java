package com.login.dto;

import java.time.LocalDate;

/**
 * InterviewFeedbackDTO - Data Transfer Object for Interview Feedback
 * 
 * @author Bob
 */
public class InterviewFeedbackDTO {
    
    private Long interviewId;
    private String candidateName;
    private String source;
    private Double yearsOfExperience;
    private Double yearsOfExperienceInTech;
    private String evaluationType;
    private String evaluatorNames;
    private LocalDate evaluationDate;
    private String jobRoleSpecification;
    private String jobDescription;
    private String accountName;
    private String jobLevel;
    
    // Ratings
    private Double communicationRating;
    private Double awsNativeServicesRating;
    private Double awsIntegrationServicesRating;
    private Double awsComputeServicesRating;
    private Double programmingLanguageRating;
    private Double awsDevOpsServicesRating;
    private Double awsStorageRating;
    private Double agileScrumRating;
    private Double awsCliRating;
    private Double deploymentManagementRating;
    private Double containerOrchestrationRating;
    private Double microservicesDesignPatternsRating;
    private Double microservicesCommunicationRating;
    private Double disasterRecoveryRating;
    private Double containerizationRating;
    private Double iacRating;
    private Double frontendStackRating;
    private Double htmlCssRating;
    private Double springCloudAwsRating;
    private Double sqlTuningRating;
    private Double setupPackagingRating;
    
    private String certifications;
    
    // Architecting ratings
    private Double estimationRating;
    private Double architectureRating;
    private Double solutioningRating;
    private Double deliveryMethodologiesRating;
    private Double operationsRating;
    private Double customerHandlingRating;
    private Double otherManagementSkillsRating;
    
    // Notes
    private String awsNativeServicesNotes;
    private String technicalSkillsNotes;
    
    // Overall
    private Double overallRating;
    private String toolRecommendation;
    private String techPanelRecommendation;
    private String overallFeedback;
    private String suitabilityForRequirement;
    private String improvementFocusArea;
    private Boolean declarationAccepted;
    private String evaluatorSignature;

    // Constructors
    public InterviewFeedbackDTO() {
    }

    // Getters and Setters
    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
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
}

// Made with Bob