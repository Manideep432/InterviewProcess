# Interview Time Column Fix Guide

## Problem
The MySQL database has an `interview_time` column in the `interviews` table that doesn't exist in the JPA entity model. This causes the error:
```
Field 'interview_time' doesn't have a default value
```

## Root Cause
- The database schema has an extra column `interview_time` that is NOT NULL
- The JPA entity [`Interview.java`](backend/src/main/java/com/login/model/Interview.java) uses `interview_time_from` and `interview_time_to` instead
- When Hibernate tries to insert a record, it doesn't provide a value for `interview_time`, causing the error

## ✅ Automatic Fix (Recommended)

**The fix is now automated!** Simply restart your Spring Boot application:

```bash
# Stop the application if running (Ctrl+C)
# Then restart it
mvn spring-boot:run
```

The [`DatabaseSchemaFix.java`](backend/src/main/java/com/login/config/DatabaseSchemaFix.java) configuration will:
1. Check if the `interview_time` column exists
2. Automatically drop it on startup
3. Log the fix in the console

**Look for this log message:**
```
Successfully dropped 'interview_time' column from interviews table
```

## Manual Solutions (If Needed)

### Option 1: Drop the Column (Recommended)

Execute the SQL script to remove the unused column:

```bash
# Connect to MySQL
mysql -u root -p

# Select your database
USE login_db;

# Drop the problematic column
ALTER TABLE interviews DROP COLUMN interview_time;

# Verify the fix
DESCRIBE interviews;
```

Or use the provided SQL file:
```bash
mysql -u root -p login_db < fix_interview_table.sql
```

### Option 2: Make Column Nullable (Alternative)

If you want to keep the column for future use:

```sql
ALTER TABLE interviews MODIFY COLUMN interview_time TIME NULL;
```

## Verification

After applying the fix, verify the table structure:

```sql
DESCRIBE interviews;
```

Expected columns:
- `id` (Primary Key)
- `hr_id`
- `panelist_id`
- `candidate_id`
- `candidate_name`
- `candidate_email`
- `interview_date`
- `interview_time_from` ✓
- `interview_time_to` ✓
- `feedback`
- `position`
- `notes`
- `status`
- `meeting_room_id`
- `meeting_link`
- `meeting_start_time`
- `meeting_end_time`
- `duration_minutes`
- `recording_enabled`
- `created_at`
- `updated_at`

**Note:** `interview_time` should NOT be in the list.

## Testing

After fixing the database:

1. Restart your Spring Boot application
2. Try scheduling an interview through the HR dashboard
3. The error should be resolved

## Prevention

To prevent similar issues in the future:

1. **Use Hibernate DDL Auto**: Set in [`application.properties`](backend/src/main/resources/application.properties):
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```
   This keeps the database schema in sync with entity models.

2. **Use Database Migrations**: Consider using Flyway or Liquibase for version-controlled schema changes.

3. **Regular Schema Validation**: Periodically compare entity models with database schema.

## Related Files
- **Auto-Fix Configuration**: [`DatabaseSchemaFix.java`](backend/src/main/java/com/login/config/DatabaseSchemaFix.java)
- Entity Model: [`Interview.java`](backend/src/main/java/com/login/model/Interview.java:38-43)
- Service: [`HRService.java`](backend/src/main/java/com/login/service/HRService.java:988)
- Manual SQL Fix: [`fix_interview_table.sql`](fix_interview_table.sql)

## Additional Notes

The application correctly uses:
- `interview_time_from` - Start time of the interview
- `interview_time_to` - End time of the interview

These provide more flexibility than a single `interview_time` field.