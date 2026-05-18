package com.login.service;

import com.login.model.EmailOtp;
import com.login.repository.EmailOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * OTP Service - Handles OTP generation, validation, and cleanup
 * 
 * @author Bob
 */
@Service
public class OtpService {

    @Autowired
    private EmailOtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_OTP_ATTEMPTS_PER_HOUR = 5;

    /**
     * Generate and send OTP for login
     * Returns the OTP for development/testing purposes
     */
    @Transactional
    public String generateAndSendLoginOtp(String email, String username) {
        // Check rate limiting
        checkRateLimit(email);

        // Invalidate any existing unused OTPs for this email and purpose to prevent duplicates
        List<EmailOtp> existingOtps = otpRepository.findByEmailAndPurposeOrderByCreatedAtDesc(email, "LOGIN");
        for (EmailOtp existingOtp : existingOtps) {
            if (!existingOtp.isUsed()) {
                existingOtp.markAsUsed();
                otpRepository.save(existingOtp);
            }
        }

        // Generate OTP
        String otp = generateOtp();

        // Calculate expiry time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        // Save OTP to database
        EmailOtp emailOtp = new EmailOtp(email, otp, expiresAt, "LOGIN");
        otpRepository.save(emailOtp);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp, username);

        System.out.println("OTP generated and sent to: " + email + " | OTP: " + otp);
        
        // Return OTP for backend response (useful for development/testing)
        return otp;
    }

    /**
     * Verify OTP
     */
    @Transactional
    public boolean verifyOtp(String email, String otp, String purpose) {
        Optional<EmailOtp> otpOptional = otpRepository.findLatestValidOtp(
            email, 
            purpose, 
            LocalDateTime.now()
        );

        if (otpOptional.isEmpty()) {
            System.out.println("No valid OTP found for email: " + email);
            return false;
        }

        EmailOtp emailOtp = otpOptional.get();

        // Check if OTP matches
        if (!emailOtp.getOtp().equals(otp)) {
            System.out.println("OTP mismatch for email: " + email);
            return false;
        }

        // Check if OTP is still valid
        if (!emailOtp.isValid()) {
            System.out.println("OTP expired or already used for email: " + email);
            return false;
        }

        // Mark OTP as used
        emailOtp.markAsUsed();
        otpRepository.save(emailOtp);

        System.out.println("OTP verified successfully for email: " + email);
        return true;
    }

    /**
     * Generate random 6-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit number
        return String.valueOf(otp);
    }

    /**
     * Check rate limiting (max 5 OTPs per hour)
     */
    private void checkRateLimit(String email) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentOtpCount = otpRepository.countRecentOtpsByEmail(email, oneHourAgo);

        if (recentOtpCount >= MAX_OTP_ATTEMPTS_PER_HOUR) {
            throw new RuntimeException(
                "Too many OTP requests. Please try again after some time."
            );
        }
    }

    /**
     * Clean up expired OTPs (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredOtps() {
        try {
            otpRepository.deleteExpiredOtps(LocalDateTime.now());
            System.out.println("Expired OTPs cleaned up successfully");
        } catch (Exception e) {
            System.err.println("Failed to cleanup expired OTPs: " + e.getMessage());
        }
    }

    /**
     * Invalidate all OTPs for an email
     */
    @Transactional
    public void invalidateAllOtps(String email) {
        otpRepository.deleteByEmail(email);
        System.out.println("All OTPs invalidated for email: " + email);
    }

    /**
     * Check if OTP exists and is valid
     */
    public boolean hasValidOtp(String email, String purpose) {
        Optional<EmailOtp> otpOptional = otpRepository.findLatestValidOtp(
            email,
            purpose,
            LocalDateTime.now()
        );
        return otpOptional.isPresent();
    }
}

// Made with Bob