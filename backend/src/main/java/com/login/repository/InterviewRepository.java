package com.login.repository;

import com.login.model.Interview;
import com.login.model.Interview.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Interview Repository - Data access layer for Interview entity
 * 
 * @author Bob
 */
@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    /**
     * Find all interviews by panelist ID
     */
    List<Interview> findByPanelistId(Long panelistId);

    /**
     * Find interviews by panelist ID and status
     */
    List<Interview> findByPanelistIdAndStatus(Long panelistId, InterviewStatus status);

    /**
     * Find interviews by panelist ID and date
     */
    List<Interview> findByPanelistIdAndInterviewDate(Long panelistId, LocalDate interviewDate);

    /**
     * Find interviews by candidate email
     */
    List<Interview> findByCandidateEmail(String candidateEmail);

    /**
     * Find interviews by status
     */
    List<Interview> findByStatus(InterviewStatus status);

    /**
     * Count interviews by panelist ID
     */
    long countByPanelistId(Long panelistId);

    /**
     * Count interviews by panelist ID and status
     */
    long countByPanelistIdAndStatus(Long panelistId, InterviewStatus status);
}

// Made with Bob