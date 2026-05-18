package com.login.repository;

import com.login.model.Candidate;
import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Candidate Repository - Data access layer for Candidate entity
 * 
 * @author Bob
 */
@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    /**
     * Find candidate by email
     */
    Optional<Candidate> findByEmail(String email);

    /**
     * Find all candidates managed by a specific HR
     */
    List<Candidate> findByHr(User hr);

    /**
     * Find all candidates assigned to a specific panelist
     */
    List<Candidate> findByAssignedPanelist(User panelist);

    /**
     * Find candidates by status
     */
    List<Candidate> findByStatus(String status);

    /**
     * Find candidates by HR and status
     */
    List<Candidate> findByHrAndStatus(User hr, String status);

    /**
     * Find candidates by assigned panelist and status
     */
    List<Candidate> findByAssignedPanelistAndStatus(User panelist, String status);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}

// Made with Bob