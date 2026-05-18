package com.login.repository;

import com.login.model.InterviewFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * InterviewFeedbackRepository - Repository for InterviewFeedback entity
 * 
 * @author Bob
 */
@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedback, Long> {
    
    /**
     * Find feedback by interview ID
     */
    Optional<InterviewFeedback> findByInterviewId(Long interviewId);
    
    /**
     * Find all feedback by panelist ID
     */
    List<InterviewFeedback> findByPanelistId(Long panelistId);
    
    /**
     * Find all feedback by candidate ID
     */
    List<InterviewFeedback> findByCandidateId(Long candidateId);
    
    /**
     * Find all feedback that needs to be sent to HR
     */
    List<InterviewFeedback> findBySentToHRFalse();
    
    /**
     * Find all feedback by status
     */
    List<InterviewFeedback> findByStatus(String status);
    
    /**
     * Find all feedback by tech panel recommendation
     */
    List<InterviewFeedback> findByTechPanelRecommendation(String recommendation);
    
    /**
     * Check if feedback exists for an interview
     */
    boolean existsByInterviewId(Long interviewId);
}

// Made with Bob