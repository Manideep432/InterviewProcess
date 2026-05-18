package com.login.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Database Schema Fix Configuration
 * Automatically fixes schema mismatches on application startup
 * 
 * @author Bob
 */
@Configuration
public class DatabaseSchemaFix {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaFix.class);

    @Bean
    public CommandLineRunner fixInterviewTableSchema(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                logger.info("Checking and fixing interview table schema...");
                
                // Check if interview_time column exists
                String checkColumnSql = 
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() " +
                    "AND TABLE_NAME = 'interviews' " +
                    "AND COLUMN_NAME = 'interview_time'";
                
                Integer columnExists = jdbcTemplate.queryForObject(checkColumnSql, Integer.class);
                
                if (columnExists != null && columnExists > 0) {
                    logger.warn("Found unused 'interview_time' column in interviews table. Dropping it...");
                    
                    // Drop the interview_time column
                    String dropColumnSql = "ALTER TABLE interviews DROP COLUMN interview_time";
                    jdbcTemplate.execute(dropColumnSql);
                    
                    logger.info("Successfully dropped 'interview_time' column from interviews table");
                } else {
                    logger.info("Interview table schema is correct. No fixes needed.");
                }
                
            } catch (Exception e) {
                logger.error("Error while fixing interview table schema: {}", e.getMessage());
                // Don't throw exception to allow application to start
                // The error will be logged and admin can fix manually if needed
            }
        };
    }
}

// Made with Bob
