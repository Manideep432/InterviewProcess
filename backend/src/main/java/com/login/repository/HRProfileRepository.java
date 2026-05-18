package com.login.repository;

import com.login.model.HRProfile;
import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * HR Profile Repository - Database operations for HR Profile
 *
 * @author Bob
 */
@Repository
public interface HRProfileRepository extends JpaRepository<HRProfile, Long> {
    
    /**
     * Find HR profile by user
     */
    Optional<HRProfile> findByUser(User user);
    
    /**
     * Find HR profile by user ID
     */
    Optional<HRProfile> findByUserId(Long userId);
    
    /**
     * Check if HR profile exists for user
     */
    boolean existsByUser(User user);
    
    /**
     * Check if HR profile exists for user ID
     */
    boolean existsByUserId(Long userId);
}

// Made with Bob