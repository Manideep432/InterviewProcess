# Interview Feedback Table Fix Guide

## Problem
The `interview_feedback` table doesn't exist in the MySQL database, causing the error:
```
Table 'logindb.interview_feedback' doesn't exist
```

## Solution Options

### Option 1: Run SQL Script Manually (Recommended - Fastest)

1. **Open MySQL Workbench or MySQL Command Line**

2. **Connect to your MySQL server**
   - Host: localhost
   - Port: 3306
   - Username: root
   - Password: root

3. **Run the SQL script**
   ```bash
   # Using MySQL command line
   mysql -u root -p logindb < CREATE_INTERVIEW_FEEDBACK_TABLE.sql
   ```
   
   OR in MySQL Workbench:
   - Open the file `CREATE_INTERVIEW_FEEDBACK_TABLE.sql`
   - Click the lightning bolt icon to execute
   - Verify the table was created

4. **Verify table creation**
   ```sql
   USE logindb;
   SHOW TABLES LIKE 'interview_feedback';
   DESCRIBE interview_feedback;
   ```

5. **Restart the backend application**

### Option 2: Let Application Auto-Create (Automatic)

The application has a `DatabaseSchemaValidator` that should automatically create the table on startup.

1. **Stop the backend if running** (Ctrl+C in the terminal)

2. **Start the backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Check the console output** for:
   ```
   =================================================
   Validating Database Schema...
   =================================================
   ⚠️  interview_feedback table not found. Creating table...
   ✅ interview_feedback table created successfully
   ```

4. **If the table is still not created**, use Option 1 (manual SQL script)

### Option 3: Force Hibernate to Recreate Schema (Use with Caution)

⚠️ **WARNING**: This will DROP ALL TABLES and recreate them, losing all data!

1. **Backup your database first!**
   ```bash
   mysqldump -u root -p logindb > logindb_backup.sql
   ```

2. **Edit `backend/src/main/resources/application.properties`**
   ```properties
   # Change this line:
   spring.jpa.hibernate.ddl-auto=update
   
   # To this (temporarily):
   spring.jpa.hibernate.ddl-auto=create
   ```

3. **Restart the backend**
   - All tables will be recreated
   - All data will be lost

4. **Change back to `update` after restart**
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

## Verification Steps

After applying any fix:

1. **Check if table exists**
   ```sql
   USE logindb;
   SHOW TABLES;
   ```

2. **Verify table structure**
   ```sql
   DESCRIBE interview_feedback;
   ```

3. **Test the API endpoint**
   ```bash
   curl http://localhost:8081/api/interview-feedback/all
   ```

4. **Check backend logs** for any errors

## Why This Happened

Possible reasons:
1. The backend was never started after adding the InterviewFeedback entity
2. The DatabaseSchemaValidator didn't run properly
3. MySQL permissions issue preventing table creation
4. The `spring.jpa.hibernate.ddl-auto=update` setting didn't trigger table creation

## Prevention

To prevent this in the future:

1. **Always check logs** when starting the backend for schema validation messages
2. **Verify tables exist** after adding new entities
3. **Use database migrations** (like Flyway or Liquibase) for production environments
4. **Keep backups** of your database regularly

## Quick Fix Command (MySQL CLI)

If you have MySQL command line access:

```bash
mysql -u root -p -e "USE logindb; CREATE TABLE IF NOT EXISTS interview_feedback (id BIGINT AUTO_INCREMENT PRIMARY KEY, interview_id BIGINT NOT NULL, panelist_id BIGINT NOT NULL, candidate_id BIGINT NOT NULL, candidate_name VARCHAR(255) NOT NULL, source VARCHAR(100) NOT NULL, years_of_experience DOUBLE NOT NULL, years_of_experience_in_tech DOUBLE NOT NULL, evaluation_type VARCHAR(100) NOT NULL, evaluator_names VARCHAR(255) NOT NULL, evaluation_date DATE NOT NULL, job_role_specification VARCHAR(255) NOT NULL, job_description VARCHAR(1000), account_name VARCHAR(255) NOT NULL, job_level VARCHAR(50) NOT NULL, communication_rating DOUBLE, aws_native_services_rating DOUBLE, aws_integration_services_rating DOUBLE, aws_compute_services_rating DOUBLE, programming_language_rating DOUBLE, aws_dev_ops_services_rating DOUBLE, aws_storage_rating DOUBLE, agile_scrum_rating DOUBLE, aws_cli_rating DOUBLE, deployment_management_rating DOUBLE, container_orchestration_rating DOUBLE, microservices_design_patterns_rating DOUBLE, microservices_communication_rating DOUBLE, disaster_recovery_rating DOUBLE, containerization_rating DOUBLE, iac_rating DOUBLE, frontend_stack_rating DOUBLE, html_css_rating DOUBLE, spring_cloud_aws_rating DOUBLE, sql_tuning_rating DOUBLE, setup_packaging_rating DOUBLE, certifications VARCHAR(1000), estimation_rating DOUBLE, architecture_rating DOUBLE, solutioning_rating DOUBLE, delivery_methodologies_rating DOUBLE, operations_rating DOUBLE, customer_handling_rating DOUBLE, other_management_skills_rating DOUBLE, aws_native_services_notes VARCHAR(2000), technical_skills_notes VARCHAR(2000), overall_rating DOUBLE NOT NULL, tool_recommendation VARCHAR(50) NOT NULL, tech_panel_recommendation VARCHAR(50) NOT NULL, overall_feedback VARCHAR(5000) NOT NULL, suitability_for_requirement VARCHAR(2000), improvement_focus_area VARCHAR(2000), declaration_accepted BOOLEAN NOT NULL DEFAULT FALSE, evaluator_signature VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED', sent_to_hr BOOLEAN NOT NULL DEFAULT FALSE, sent_to_hr_at DATETIME, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, INDEX idx_interview_id (interview_id), INDEX idx_panelist_id (panelist_id), INDEX idx_candidate_id (candidate_id), INDEX idx_status (status), INDEX idx_tech_panel_recommendation (tech_panel_recommendation)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;"
```

## Support

If you continue to have issues:
1. Check MySQL error logs
2. Verify MySQL user has CREATE TABLE permissions
3. Ensure the `logindb` database exists
4. Check if MySQL service is running

---
Made with Bob