# Interview Feedback Table - Complete Fix Guide

## Problem Description
The application was throwing an error:
```
Table 'logindb.interview_feedback' doesn't exist
```

This occurred because the `interview_feedback` table was not created in the MySQL database.

## Solution Implemented

### 1. Automatic Table Creation (Recommended)
The application now automatically creates the `interview_feedback` table on startup.

**File Modified:** `backend/src/main/java/com/login/config/DatabaseSchemaFix.java`

A new `CommandLineRunner` bean was added that:
- Checks if the `interview_feedback` table exists
- Creates it automatically if missing
- Logs the process for debugging

### 2. Manual Table Creation (Backup Option)
If automatic creation fails, you can manually create the table.

**SQL Script Location:** `backend/src/main/resources/sql/create_interview_feedback_table.sql`

## How to Fix

### Option A: Automatic Fix (Restart Backend)

1. **Stop the backend** if it's running (Ctrl+C in the terminal)

2. **Restart the backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Check the logs** for this message:
   ```
   Successfully created interview_feedback table
   ```

4. **Test the application** - The error should be gone!

### Option B: Manual Fix (If Automatic Fails)

1. **Connect to MySQL:**
   ```bash
   mysql -u root -p
   ```
   Enter password: `root`

2. **Select the database:**
   ```sql
   USE logindb;
   ```

3. **Run the SQL script:**
   ```bash
   source backend/src/main/resources/sql/create_interview_feedback_table.sql
   ```
   
   OR copy and paste the SQL from the file directly into MySQL.

4. **Verify table creation:**
   ```sql
   SHOW TABLES;
   DESCRIBE interview_feedback;
   ```

5. **Restart the backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

## Table Structure

The `interview_feedback` table includes:

### Basic Information
- `id` - Primary key
- `interview_id` - Reference to interview
- `panelist_id` - Reference to panelist
- `candidate_id` - Reference to candidate
- `candidate_name` - Candidate's name
- `source` - External Hire, Internal, etc.
- `years_of_experience` - Total experience
- `years_of_experience_in_tech` - Tech-specific experience
- `evaluation_type` - VENDOR/SELF/IBM REFERAL/OTHERS
- `evaluator_names` - Names of evaluators
- `evaluation_date` - Date of evaluation
- `job_role_specification` - Job role details
- `job_description` - Job description
- `account_name` - Account name
- `job_level` - SE, SSE, TL, ML, Architect

### Technical Skills Ratings (0-10 scale)
- `communication_rating`
- `aws_native_services_rating`
- `aws_integration_services_rating`
- `aws_compute_services_rating`
- `programming_language_rating`
- `aws_dev_ops_services_rating`
- `aws_storage_rating`
- `agile_scrum_rating`
- `aws_cli_rating`
- `deployment_management_rating`
- `container_orchestration_rating`
- `microservices_design_patterns_rating`
- `microservices_communication_rating`
- `disaster_recovery_rating`
- `containerization_rating`
- `iac_rating`
- `frontend_stack_rating`
- `html_css_rating`
- `spring_cloud_aws_rating`
- `sql_tuning_rating`
- `setup_packaging_rating`

### Certifications & Management Skills
- `certifications`
- `estimation_rating`
- `architecture_rating`
- `solutioning_rating`
- `delivery_methodologies_rating`
- `operations_rating`
- `customer_handling_rating`
- `other_management_skills_rating`

### Notes & Feedback
- `aws_native_services_notes`
- `technical_skills_notes`
- `overall_rating`
- `tool_recommendation` - Selected/Rejected
- `tech_panel_recommendation` - Selected/Rejected
- `overall_feedback`
- `suitability_for_requirement`
- `improvement_focus_area`

### Declaration & Status
- `declaration_accepted`
- `evaluator_signature`
- `status` - SUBMITTED, REVIEWED, APPROVED, REJECTED
- `sent_tohr` - Boolean flag
- `sent_tohrat` - Timestamp when sent to HR
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp

### Indexes
The table includes indexes on:
- `interview_id`
- `panelist_id`
- `candidate_id`
- `status`
- `sent_tohr`
- `evaluation_date`
- `created_at`

## Verification Steps

1. **Check if table exists:**
   ```sql
   USE logindb;
   SHOW TABLES LIKE 'interview_feedback';
   ```

2. **View table structure:**
   ```sql
   DESCRIBE interview_feedback;
   ```

3. **Check table is empty (initially):**
   ```sql
   SELECT COUNT(*) FROM interview_feedback;
   ```

4. **Test the application:**
   - Login as a panelist
   - Navigate to feedback section
   - The error should be gone

## Troubleshooting

### Issue: Table still not created after restart

**Solution:**
1. Check backend logs for errors
2. Verify MySQL is running: `mysql -u root -p`
3. Verify database exists: `SHOW DATABASES;`
4. Manually run the SQL script (Option B above)

### Issue: Permission denied errors

**Solution:**
1. Verify MySQL user has CREATE TABLE permissions:
   ```sql
   GRANT ALL PRIVILEGES ON logindb.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

### Issue: Column name mismatch errors

**Solution:**
The table uses snake_case (e.g., `sent_tohr`) while JPA entity uses camelCase (e.g., `sentToHR`).
This is handled automatically by Hibernate's naming strategy.

If issues persist, check `application.properties`:
```properties
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

## Configuration Details

### Current Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logindb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

### Important Notes
- `spring.jpa.hibernate.ddl-auto=update` - Hibernate will update schema automatically
- The `DatabaseSchemaFix` bean runs BEFORE Hibernate schema update
- This ensures the table exists before any queries are executed

## Testing the Fix

1. **Restart the backend:**
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```

2. **Watch for success message in logs:**
   ```
   Successfully created interview_feedback table
   ```

3. **Test the feedback feature:**
   - Login as panelist
   - Navigate to interview feedback section
   - Try to view/create feedback
   - Error should be resolved

## Related Files

- **Entity:** `backend/src/main/java/com/login/model/InterviewFeedback.java`
- **Repository:** `backend/src/main/java/com/login/repository/InterviewFeedbackRepository.java`
- **Service:** `backend/src/main/java/com/login/service/InterviewFeedbackService.java`
- **Controller:** `backend/src/main/java/com/login/controller/InterviewFeedbackController.java`
- **Schema Fix:** `backend/src/main/java/com/login/config/DatabaseSchemaFix.java`
- **SQL Script:** `backend/src/main/resources/sql/create_interview_feedback_table.sql`

## Success Indicators

✅ Backend starts without errors
✅ Log shows "Successfully created interview_feedback table"
✅ Table exists in MySQL: `SHOW TABLES;`
✅ Feedback feature works without errors
✅ No "Table doesn't exist" errors in logs

## Next Steps

After fixing:
1. Test creating interview feedback
2. Test viewing feedback
3. Test updating feedback
4. Test sending feedback to HR
5. Verify all CRUD operations work

---

**Made with ❤️ by Bob**