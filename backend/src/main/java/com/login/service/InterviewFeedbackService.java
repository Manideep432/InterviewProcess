package com.login.service;

import com.login.dto.InterviewFeedbackDTO;
import com.login.model.Candidate;
import com.login.model.Interview;
import com.login.model.InterviewFeedback;
import com.login.model.Panelist;
import com.login.model.User;
import com.login.repository.CandidateRepository;
import com.login.repository.InterviewFeedbackRepository;
import com.login.repository.InterviewRepository;
import com.login.repository.PanelistRepository;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * InterviewFeedbackService - Business logic for interview feedback management
 * 
 * @author Bob
 */
@Service
public class InterviewFeedbackService {

    @Autowired
    private InterviewFeedbackRepository feedbackRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private PanelistRepository panelistRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private EmailService emailService;

    /**
     * Submit technical interview feedback
     */
    @Transactional
    public InterviewFeedback submitFeedback(Long panelistUserId, InterviewFeedbackDTO feedbackDTO) {
        // Validate interview exists
        Interview interview = interviewRepository.findById(feedbackDTO.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        // Validate panelist by user ID
        Panelist panelist = panelistRepository.findByUserId(panelistUserId)
                .orElseThrow(() -> new RuntimeException("Panelist not found for user ID: " + panelistUserId));

        // Check if panelist is assigned to this interview
        // interview.getPanelistId() is the Panelist entity ID, not the User ID
        if (!interview.getPanelistId().equals(panelist.getId())) {
            throw new RuntimeException("❌ You are not authorized to submit feedback for this interview. " +
                    "Interview is assigned to panelist ID: " + interview.getPanelistId() +
                    ", but you are panelist ID: " + panelist.getId());
        }

        // Check if feedback already exists
        if (feedbackRepository.existsByInterviewId(interview.getId())) {
            throw new RuntimeException("Feedback already submitted for this interview");
        }

        // Create feedback entity
        InterviewFeedback feedback = new InterviewFeedback();
        feedback.setInterviewId(interview.getId());
        feedback.setPanelistId(panelist.getId());
        feedback.setCandidateId(interview.getCandidateId());
        
        // Map DTO to entity
        mapDtoToEntity(feedbackDTO, feedback);

        // Save feedback
        InterviewFeedback savedFeedback = feedbackRepository.save(feedback);

        // Update interview with feedback link and completion status
        interview.setTechnicalFeedbackId(savedFeedback.getId());
        interview.setHasTechnicalFeedback(true);
        interview.setFeedback(savedFeedback.getOverallFeedback());
        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        // Update candidate status based on panelist decision
        Candidate candidate = candidateRepository.findById(interview.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        String recommendation = savedFeedback.getTechPanelRecommendation() != null
                ? savedFeedback.getTechPanelRecommendation().trim().toUpperCase()
                : "";

        if ("SELECTED".equals(recommendation)) {
            candidate.setStatus("SELECTED");
        } else if ("REJECTED".equals(recommendation)) {
            candidate.setStatus("REJECTED");
        } else {
            candidate.setStatus("INTERVIEW_COMPLETED");
        }

        candidateRepository.save(candidate);

        // Generate PDF
        byte[] pdfContent = pdfGenerationService.generateInterviewFeedbackPdf(savedFeedback);

        // Get HR email
        User hrUser = userRepository.findById(interview.getHrId())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        // Get panelist name
        User panelistUser = userRepository.findById(panelistUserId)
                .orElseThrow(() -> new RuntimeException("Panelist user not found"));

        // Send email to HR with PDF
        try {
            emailService.sendInterviewFeedbackToHR(
                hrUser.getEmail(),
                feedback.getCandidateName(),
                panelistUser.getUsername(),
                interview.getJrs(),
                feedback.getTechPanelRecommendation(),
                pdfContent
            );

            // Mark as sent to HR
            savedFeedback.setSentToHR(true);
            savedFeedback.setSentToHRAt(LocalDateTime.now());
            feedbackRepository.save(savedFeedback);
        } catch (Exception e) {
            System.err.println("Failed to send email to HR: " + e.getMessage());
            // Don't fail the entire operation if email fails
        }

        return savedFeedback;
    }

    /**
     * Get feedback by interview ID
     */
    public Optional<InterviewFeedback> getFeedbackByInterviewId(Long interviewId) {
        return feedbackRepository.findByInterviewId(interviewId);
    }

    /**
     * Get all feedback by panelist
     */
    public List<InterviewFeedback> getFeedbackByPanelist(Long panelistId) {
        return feedbackRepository.findByPanelistId(panelistId);
    }

    /**
     * Get all feedback by candidate
     */
    public List<InterviewFeedback> getFeedbackByCandidate(Long candidateId) {
        return feedbackRepository.findByCandidateId(candidateId);
    }

    /**
     * Get all feedback
     */
    public List<InterviewFeedback> getAllFeedback() {
        try {
            List<InterviewFeedback> feedbacks = feedbackRepository.findAll();
            System.out.println("Successfully fetched " + feedbacks.size() + " feedback records");
            return feedbacks;
        } catch (Exception e) {
            System.err.println("Error in getAllFeedback: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch feedback records: " + e.getMessage(), e);
        }
    }

    /**
     * Get feedback by status
     */
    public List<InterviewFeedback> getFeedbackByStatus(String status) {
        return feedbackRepository.findByStatus(status);
    }

    /**
     * Get feedback by recommendation
     */
    public List<InterviewFeedback> getFeedbackByRecommendation(String recommendation) {
        return feedbackRepository.findByTechPanelRecommendation(recommendation);
    }

    /**
     * Regenerate and resend PDF to HR
     */
    @Transactional
    public void resendFeedbackToHR(Long feedbackId) {
        InterviewFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));

        Interview interview = interviewRepository.findById(feedback.getInterviewId())
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        User hrUser = userRepository.findById(interview.getHrId())
                .orElseThrow(() -> new RuntimeException("HR not found"));

        Panelist panelist = panelistRepository.findById(feedback.getPanelistId())
                .orElseThrow(() -> new RuntimeException("Panelist not found"));

        User panelistUser = userRepository.findById(panelist.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Panelist user not found"));

        // Generate PDF
        byte[] pdfContent = pdfGenerationService.generateInterviewFeedbackPdf(feedback);

        // Send email
        emailService.sendInterviewFeedbackToHR(
            hrUser.getEmail(),
            feedback.getCandidateName(),
            panelistUser.getUsername(),
            interview.getJrs(),
            feedback.getTechPanelRecommendation(),
            pdfContent
        );

        // Update sent status
        feedback.setSentToHR(true);
        feedback.setSentToHRAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
    }

    /**
     * Generate PDF for a feedback by ID
     */
    public byte[] generateFeedbackPdf(Long feedbackId) {
        InterviewFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));
        
        return pdfGenerationService.generateInterviewFeedbackPdf(feedback);
    }

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + username));
    }

    /**
     * Map DTO to Entity
     */
    private void mapDtoToEntity(InterviewFeedbackDTO dto, InterviewFeedback entity) {
        entity.setCandidateName(dto.getCandidateName());
        entity.setSource(dto.getSource());
        entity.setYearsOfExperience(dto.getYearsOfExperience());
        entity.setYearsOfExperienceInTech(dto.getYearsOfExperienceInTech());
        entity.setEvaluationType(dto.getEvaluationType());
        entity.setEvaluatorNames(dto.getEvaluatorNames());
        entity.setEvaluationDate(dto.getEvaluationDate());
        entity.setJobRoleSpecification(dto.getJobRoleSpecification());
        entity.setJobDescription(dto.getJobDescription());
        entity.setAccountName(dto.getAccountName());
        entity.setJobLevel(dto.getJobLevel());
        
        // Ratings
        entity.setCommunicationRating(dto.getCommunicationRating());
        entity.setAwsNativeServicesRating(dto.getAwsNativeServicesRating());
        entity.setAwsIntegrationServicesRating(dto.getAwsIntegrationServicesRating());
        entity.setAwsComputeServicesRating(dto.getAwsComputeServicesRating());
        entity.setProgrammingLanguageRating(dto.getProgrammingLanguageRating());
        entity.setAwsDevOpsServicesRating(dto.getAwsDevOpsServicesRating());
        entity.setAwsStorageRating(dto.getAwsStorageRating());
        entity.setAgileScrumRating(dto.getAgileScrumRating());
        entity.setAwsCliRating(dto.getAwsCliRating());
        entity.setDeploymentManagementRating(dto.getDeploymentManagementRating());
        entity.setContainerOrchestrationRating(dto.getContainerOrchestrationRating());
        entity.setMicroservicesDesignPatternsRating(dto.getMicroservicesDesignPatternsRating());
        entity.setMicroservicesCommunicationRating(dto.getMicroservicesCommunicationRating());
        entity.setDisasterRecoveryRating(dto.getDisasterRecoveryRating());
        entity.setContainerizationRating(dto.getContainerizationRating());
        entity.setIacRating(dto.getIacRating());
        entity.setFrontendStackRating(dto.getFrontendStackRating());
        entity.setHtmlCssRating(dto.getHtmlCssRating());
        entity.setSpringCloudAwsRating(dto.getSpringCloudAwsRating());
        entity.setSqlTuningRating(dto.getSqlTuningRating());
        entity.setSetupPackagingRating(dto.getSetupPackagingRating());
        
        entity.setCertifications(dto.getCertifications());
        
        // Architecting ratings
        entity.setEstimationRating(dto.getEstimationRating());
        entity.setArchitectureRating(dto.getArchitectureRating());
        entity.setSolutioningRating(dto.getSolutioningRating());
        entity.setDeliveryMethodologiesRating(dto.getDeliveryMethodologiesRating());
        entity.setOperationsRating(dto.getOperationsRating());
        entity.setCustomerHandlingRating(dto.getCustomerHandlingRating());
        entity.setOtherManagementSkillsRating(dto.getOtherManagementSkillsRating());
        
        // Notes
        entity.setAwsNativeServicesNotes(dto.getAwsNativeServicesNotes());
        entity.setTechnicalSkillsNotes(dto.getTechnicalSkillsNotes());
        
        // Overall
        entity.setOverallRating(dto.getOverallRating());
        entity.setToolRecommendation(dto.getToolRecommendation());
        entity.setTechPanelRecommendation(dto.getTechPanelRecommendation());
        entity.setOverallFeedback(dto.getOverallFeedback());
        entity.setSuitabilityForRequirement(dto.getSuitabilityForRequirement());
        entity.setImprovementFocusArea(dto.getImprovementFocusArea());
        entity.setDeclarationAccepted(dto.getDeclarationAccepted());
        entity.setEvaluatorSignature(dto.getEvaluatorSignature());
    }
}

// Made with Bob