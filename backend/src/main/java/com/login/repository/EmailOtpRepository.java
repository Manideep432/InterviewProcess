package com.login.repository;

import com.login.model.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Email OTP Repository - Database operations for OTP management
 * 
 * @author Bob
 */
@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    /**
     * Find the latest valid OTP for an email and purpose
     * Returns only the first result to avoid "Query did not return a unique result" error
     */
    @Query("SELECT o FROM EmailOtp o WHERE o.email = ?1 AND o.purpose = ?2 " +
           "AND o.isUsed = false AND o.expiresAt > ?3 " +
           "ORDER BY o.createdAt DESC")
    List<EmailOtp> findLatestValidOtpList(String email, String purpose, LocalDateTime now);
    
    /**
     * Find the latest valid OTP for an email and purpose (returns Optional)
     */
    default Optional<EmailOtp> findLatestValidOtp(String email, String purpose, LocalDateTime now) {
        List<EmailOtp> otps = findLatestValidOtpList(email, purpose, now);
        return otps.isEmpty() ? Optional.empty() : Optional.of(otps.get(0));
    }

    /**
     * Find all OTPs for an email
     */
    List<EmailOtp> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Find OTPs by email and purpose
     */
    List<EmailOtp> findByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);

    /**
     * Delete expired OTPs (cleanup)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailOtp o WHERE o.expiresAt < ?1")
    void deleteExpiredOtps(LocalDateTime now);

    /**
     * Delete all OTPs for an email
     */
    @Modifying
    @Transactional
    void deleteByEmail(String email);

    /**
     * Count unused OTPs for an email in the last hour (rate limiting)
     */
    @Query("SELECT COUNT(o) FROM EmailOtp o WHERE o.email = ?1 " +
           "AND o.createdAt > ?2")
    long countRecentOtpsByEmail(String email, LocalDateTime since);
}

// Made with Bob