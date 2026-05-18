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

    @Bean
    public CommandLineRunner createInterviewFeedbackTable(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                logger.info("Checking if interview_feedback table exists...");
                
                // Check if interview_feedback table exists
                String checkTableSql =
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_SCHEMA = DATABASE() " +
                    "AND TABLE_NAME = 'interview_feedback'";
                
                Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
                
                if (tableExists == null || tableExists == 0) {
                    logger.warn("interview_feedback table does not exist. Creating it now...");
                    
                    // Create the interview_feedback table
                    String createTableSql =
                        "CREATE TABLE interview_feedback (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "interview_id BIGINT NOT NULL, " +
                        "panelist_id BIGINT NOT NULL, " +
                        "candidate_id BIGINT NOT NULL, " +
                        "candidate_name VARCHAR(255) NOT NULL, " +
                        "source VARCHAR(255) NOT NULL, " +
                        "years_of_experience DOUBLE NOT NULL, " +
                        "years_of_experience_in_tech DOUBLE NOT NULL, " +
                        "evaluation_type VARCHAR(255) NOT NULL, " +
                        "evaluator_names VARCHAR(255) NOT NULL, " +
                        "evaluation_date DATE NOT NULL, " +
                        "job_role_specification VARCHAR(255) NOT NULL, " +
                        "job_description VARCHAR(1000), " +
                        "account_name VARCHAR(255) NOT NULL, " +
                        "job_level VARCHAR(255) NOT NULL, " +
                        "communication_rating DOUBLE, " +
                        "aws_native_services_rating DOUBLE, " +
                        "aws_integration_services_rating DOUBLE, " +
                        "aws_compute_services_rating DOUBLE, " +
                        "programming_language_rating DOUBLE, " +
                        "aws_dev_ops_services_rating DOUBLE, " +
                        "aws_storage_rating DOUBLE, " +
                        "agile_scrum_rating DOUBLE, " +
                        "aws_cli_rating DOUBLE, " +
                        "deployment_management_rating DOUBLE, " +
                        "container_orchestration_rating DOUBLE, " +
                        "microservices_design_patterns_rating DOUBLE, " +
                        "microservices_communication_rating DOUBLE, " +
                        "disaster_recovery_rating DOUBLE, " +
                        "containerization_rating DOUBLE, " +
                        "iac_rating DOUBLE, " +
                        "frontend_stack_rating DOUBLE, " +
                        "html_css_rating DOUBLE, " +
                        "spring_cloud_aws_rating DOUBLE, " +
                        "sql_tuning_rating DOUBLE, " +
                        "setup_packaging_rating DOUBLE, " +
                        "certifications VARCHAR(1000), " +
                        "estimation_rating DOUBLE, " +
                        "architecture_rating DOUBLE, " +
                        "solutioning_rating DOUBLE, " +
                        "delivery_methodologies_rating DOUBLE, " +
                        "operations_rating DOUBLE, " +
                        "customer_handling_rating DOUBLE, " +
                        "other_management_skills_rating DOUBLE, " +
                        "aws_native_services_notes VARCHAR(2000), " +
                        "technical_skills_notes VARCHAR(2000), " +
                        "overall_rating DOUBLE NOT NULL, " +
                        "tool_recommendation VARCHAR(255) NOT NULL, " +
                        "tech_panel_recommendation VARCHAR(255) NOT NULL, " +
                        "overall_feedback VARCHAR(5000) NOT NULL, " +
                        "suitability_for_requirement VARCHAR(2000), " +
                        "improvement_focus_area VARCHAR(2000), " +
                        "declaration_accepted BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "evaluator_signature VARCHAR(255) NOT NULL, " +
                        "status VARCHAR(255) NOT NULL DEFAULT 'SUBMITTED', " +
                        "sent_tohr BOOLEAN NOT NULL DEFAULT FALSE, " +
                        "sent_tohrat DATETIME, " +
                        "created_at DATETIME NOT NULL, " +
                        "updated_at DATETIME NOT NULL, " +
                        "INDEX idx_interview_id (interview_id), " +
                        "INDEX idx_panelist_id (panelist_id), " +
                        "INDEX idx_candidate_id (candidate_id), " +
                        "INDEX idx_status (status), " +
                        "INDEX idx_sent_tohr (sent_tohr)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
                    
                    jdbcTemplate.execute(createTableSql);
                    
                    logger.info("Successfully created interview_feedback table");
                } else {
                    logger.info("interview_feedback table already exists. No action needed.");
                }
                
            } catch (Exception e) {
                logger.error("Error while creating interview_feedback table: {}", e.getMessage());
                logger.error("Stack trace: ", e);
                // Don't throw exception to allow application to start
                // The error will be logged and admin can fix manually if needed
            }
        };
    }
}

// Made with Bob
