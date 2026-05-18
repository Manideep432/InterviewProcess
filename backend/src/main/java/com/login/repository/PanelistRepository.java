package com.login.repository;

import com.login.model.Panelist;
import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Panelist Repository - Data access layer for Panelist entity
 * 
 * @author Bob
 */
@Repository
public interface PanelistRepository extends JpaRepository<Panelist, Long> {

    /**
     * Find panelist by user
     */
    Optional<Panelist> findByUser(User user);

    /**
     * Find panelist by user ID
     */
    Optional<Panelist> findByUserId(Long userId);

    /**
     * Find all panelists assigned by a specific HR
     */
    List<Panelist> findByAssignedHr(User hr);

    /**
     * Find active panelists assigned by a specific HR
     */
    List<Panelist> findByAssignedHrAndIsActive(User hr, boolean isActive);

    /**
     * Find all active panelists
     */
    List<Panelist> findByIsActive(boolean isActive);

    /**
     * Find panelists by specialization
     */
    List<Panelist> findBySpecialization(String specialization);

    /**
     * Check if user is already a panelist
     */
    boolean existsByUser(User user);

    /**
     * Check if user ID is already a panelist
     */
    boolean existsByUserId(Long userId);
}

// Made with Bob