package com.login.repository;

import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository - Data access layer for User entity
 * 
 * @author Bob
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (case-sensitive)
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by username (case-insensitive)
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists (case-sensitive)
     */
    boolean existsByUsername(String username);

    /**
     * Check if username exists (case-insensitive)
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by role
     */
    List<User> findByRole(String role);
}

// Made with Bob
