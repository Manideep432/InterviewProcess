package com.login.service;

import com.login.model.PasswordHistory;
import com.login.model.User;
import com.login.repository.PasswordHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Password History Service
 * Manages password history and validates against previous passwords
 * 
 * @author Bob
 */
@Service
public class PasswordHistoryService {

    private static final int MAX_PASSWORD_HISTORY = 24;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Check if the new password matches any of the last 24 passwords
     * 
     * @param user The user
     * @param newPassword The new password (plain text)
     * @return true if password was used before, false otherwise
     */
    public boolean isPasswordReused(User user, String newPassword) {
        List<PasswordHistory> history = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user);
        
        // Check against last 24 passwords
        int checkCount = Math.min(history.size(), MAX_PASSWORD_HISTORY);
        for (int i = 0; i < checkCount; i++) {
            PasswordHistory entry = history.get(i);
            if (passwordEncoder.matches(newPassword, entry.getPasswordHash())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Add a new password to the history
     * Automatically maintains only the last 24 passwords
     * 
     * @param user The user
     * @param passwordHash The encoded password hash
     */
    @Transactional
    public void addPasswordToHistory(User user, String passwordHash) {
        // Create new password history entry
        PasswordHistory newEntry = new PasswordHistory(user, passwordHash);
        passwordHistoryRepository.save(newEntry);
        
        // Clean up old entries (keep only last 24)
        List<PasswordHistory> allHistory = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user);
        if (allHistory.size() > MAX_PASSWORD_HISTORY) {
            // Delete entries beyond the 24th
            for (int i = MAX_PASSWORD_HISTORY; i < allHistory.size(); i++) {
                passwordHistoryRepository.delete(allHistory.get(i));
            }
        }
    }

    /**
     * Get the count of password history entries for a user
     * 
     * @param user The user
     * @return The count of password history entries
     */
    public long getPasswordHistoryCount(User user) {
        return passwordHistoryRepository.countByUser(user);
    }

    /**
     * Clear all password history for a user (use with caution)
     * 
     * @param user The user
     */
    @Transactional
    public void clearPasswordHistory(User user) {
        List<PasswordHistory> history = passwordHistoryRepository.findByUserOrderByCreatedAtDesc(user);
        passwordHistoryRepository.deleteAll(history);
    }
}

// Made with Bob
