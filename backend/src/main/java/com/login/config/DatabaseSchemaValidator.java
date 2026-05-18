package com.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * DatabaseSchemaValidator - Validates and creates missing database tables
 * 
 * @author Bob
 */
@Component
@Order(1) // Run before DataInitializer
public class DatabaseSchemaValidator implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("Validating Database Schema...");
        System.out.println("=================================================");
        
        try {
            // Check if interview_feedback table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables " +
                                   "WHERE table_schema = DATABASE() " +
                                   "AND table_name = 'interview_feedback'";
            
            Integer count = jdbcTemplate.queryForObject(checkTableQuery, Integer.class);
            
            if (count == null || count == 0) {
                System.out.println("⚠️  interview_feedback table not found. Creating table...");
                createInterviewFeedbackTable();
                System.out.println("✅ interview_feedback table created successfully");
            } else {
                System.out.println("✅ interview_feedback table exists");
                
                // Verify critical columns exist
                validateTableColumns();
            }
            
            System.out.println("=================================================");
            System.out.println("Database Schema Validation Complete");
            System.out.println("=================================================\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error validating database schema: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception - let Hibernate handle schema creation
        }
    }
    
    private void createInterviewFeedbackTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS interview_feedback (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                interview_id BIGINT NOT NULL,
                panelist_id BIGINT NOT NULL,
                candidate_id BIGINT NOT NULL,
                candidate_name VARCHAR(255) NOT NULL,
                source VARCHAR(100) NOT NULL,
                years_of_experience DOUBLE NOT NULL,
                years_of_experience_in_tech DOUBLE NOT NULL,
                evaluation_type VARCHAR(100) NOT NULL,
                evaluator_names VARCHAR(255) NOT NULL,
                evaluation_date DATE NOT NULL,
                job_role_specification VARCHAR(255) NOT NULL,
                job_description VARCHAR(1000),
                account_name VARCHAR(255) NOT NULL,
                job_level VARCHAR(50) NOT NULL,
                communication_rating DOUBLE,
                aws_native_services_rating DOUBLE,
                aws_integration_services_rating DOUBLE,
                aws_compute_services_rating DOUBLE,
                programming_language_rating DOUBLE,
                aws_dev_ops_services_rating DOUBLE,
                aws_storage_rating DOUBLE,
                agile_scrum_rating DOUBLE,
                aws_cli_rating DOUBLE,
                deployment_management_rating DOUBLE,
                container_orchestration_rating DOUBLE,
                microservices_design_patterns_rating DOUBLE,
                microservices_communication_rating DOUBLE,
                disaster_recovery_rating DOUBLE,
                containerization_rating DOUBLE,
                iac_rating DOUBLE,
                frontend_stack_rating DOUBLE,
                html_css_rating DOUBLE,
                spring_cloud_aws_rating DOUBLE,
                sql_tuning_rating DOUBLE,
                setup_packaging_rating DOUBLE,
                certifications VARCHAR(1000),
                estimation_rating DOUBLE,
                architecture_rating DOUBLE,
                solutioning_rating DOUBLE,
                delivery_methodologies_rating DOUBLE,
                operations_rating DOUBLE,
                customer_handling_rating DOUBLE,
                other_management_skills_rating DOUBLE,
                aws_native_services_notes VARCHAR(2000),
                technical_skills_notes VARCHAR(2000),
                overall_rating DOUBLE NOT NULL,
                tool_recommendation VARCHAR(50) NOT NULL,
                tech_panel_recommendation VARCHAR(50) NOT NULL,
                overall_feedback VARCHAR(5000) NOT NULL,
                suitability_for_requirement VARCHAR(2000),
                improvement_focus_area VARCHAR(2000),
                declaration_accepted BOOLEAN NOT NULL DEFAULT FALSE,
                evaluator_signature VARCHAR(255) NOT NULL,
                status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
                sent_to_hr BOOLEAN NOT NULL DEFAULT FALSE,
                sent_to_hr_at DATETIME,
                created_at DATETIME NOT NULL,
                updated_at DATETIME NOT NULL,
                INDEX idx_interview_id (interview_id),
                INDEX idx_panelist_id (panelist_id),
                INDEX idx_candidate_id (candidate_id),
                INDEX idx_status (status),
                INDEX idx_tech_panel_recommendation (tech_panel_recommendation)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
        
        jdbcTemplate.execute(createTableSQL);
    }
    
    private void validateTableColumns() {
        try {
            // Check if critical columns exist
            String checkColumnsQuery = "SELECT COUNT(*) FROM information_schema.columns " +
                                     "WHERE table_schema = DATABASE() " +
                                     "AND table_name = 'interview_feedback' " +
                                     "AND column_name IN ('id', 'interview_id', 'candidate_name', 'overall_rating')";
            
            Integer columnCount = jdbcTemplate.queryForObject(checkColumnsQuery, Integer.class);
            
            if (columnCount != null && columnCount >= 4) {
                System.out.println("✅ Critical columns validated");
            } else {
                System.out.println("⚠️  Some columns may be missing. Hibernate will handle schema updates.");
            }
        } catch (Exception e) {
            System.err.println("⚠️  Could not validate columns: " + e.getMessage());
        }
    }
}

// Made with Bob