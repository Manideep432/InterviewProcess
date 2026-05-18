package com.login.repository;

import com.login.model.PasswordHistory;
import com.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Password History Repository
 * Manages password history records for users
 * 
 * @author Bob
 */
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    /**
     * Find the last N password hashes for a user, ordered by creation date (newest first)
     */
    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.user = :user ORDER BY ph.createdAt DESC")
    List<PasswordHistory> findByUserOrderByCreatedAtDesc(@Param("user") User user);

    /**
     * Count password history entries for a user
     */
    long countByUser(User user);

    /**
     * Delete oldest password history entries for a user beyond the limit
     */
    @Query("DELETE FROM PasswordHistory ph WHERE ph.user = :user AND ph.id NOT IN " +
           "(SELECT ph2.id FROM PasswordHistory ph2 WHERE ph2.user = :user ORDER BY ph2.createdAt DESC LIMIT :limit)")
    void deleteOldestEntriesBeyondLimit(@Param("user") User user, @Param("limit") int limit);
}

// Made with Bob
