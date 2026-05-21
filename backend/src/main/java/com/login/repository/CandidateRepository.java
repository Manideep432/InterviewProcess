package com.login.repository;

import com.login.model.Candidate;
import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Search candidates by name or email (case-insensitive)
     */
    @Query("SELECT c FROM Candidate c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Candidate> searchByNameOrEmail(@Param("searchTerm") String searchTerm);

    /**
     * Search candidates by name or email for a specific HR
     */
    @Query("SELECT c FROM Candidate c WHERE c.hr = :hr AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Candidate> searchByNameOrEmailAndHr(@Param("searchTerm") String searchTerm, @Param("hr") User hr);

    /**
     * Advanced search with multiple filters
     */
    @Query("SELECT c FROM Candidate c WHERE " +
           "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:position IS NULL OR LOWER(c.position) LIKE LOWER(CONCAT('%', :position, '%'))) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Candidate> advancedSearch(
        @Param("searchTerm") String searchTerm,
        @Param("status") String status,
        @Param("position") String position,
        @Param("location") String location
    );

    /**
     * Advanced search with multiple filters for a specific HR
     */
    @Query("SELECT c FROM Candidate c WHERE c.hr = :hr AND " +
           "(:searchTerm IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:position IS NULL OR LOWER(c.position) LIKE LOWER(CONCAT('%', :position, '%'))) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Candidate> advancedSearchByHr(
        @Param("hr") User hr,
        @Param("searchTerm") String searchTerm,
        @Param("status") String status,
        @Param("position") String position,
        @Param("location") String location
    );

    /**
     * Find candidates by position (case-insensitive)
     */
    @Query("SELECT c FROM Candidate c WHERE LOWER(c.position) LIKE LOWER(CONCAT('%', :position, '%'))")
    List<Candidate> findByPositionContainingIgnoreCase(@Param("position") String position);

    /**
     * Find candidates by location (case-insensitive)
     */
    @Query("SELECT c FROM Candidate c WHERE LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Candidate> findByLocationContainingIgnoreCase(@Param("location") String location);
}

// Made with Bob