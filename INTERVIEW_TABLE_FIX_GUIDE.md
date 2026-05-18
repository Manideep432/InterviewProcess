# Interview Table Schema Fix Guide

## Problem Description

The application is failing when scheduling interviews with the following error:
```
SQL Error: 1364, SQLState: HY000
Field 'interview_time' doesn't have a default value
```

## Root Cause

The MySQL database has an old column `interview_time` that is no longer used by the application. The [`Interview`](backend/src/main/java/com/login/model/Interview.java) entity model was updated to use:
- `interview_time_from` (LocalTime)
- `interview_time_to` (LocalTime)

However, the old `interview_time` column still exists in the database and requires a value during INSERT operations.

## Solution

You need to drop the obsolete `interview_time` column from the `interviews` table.

### Option 1: Using MySQL Workbench (Recommended)

1. Open MySQL Workbench
2. Connect to your MySQL server (localhost:3306)
3. Select the `logindb` database
4. Run the following SQL command:
   ```sql
   ALTER TABLE interviews DROP COLUMN IF EXISTS interview_time;
   ```
5. Verify the change by running:
   ```sql
   DESCRIBE interviews;
   ```

### Option 2: Using Command Line

If you have MySQL CLI in your PATH:
```bash
mysql -u root -p
```
Then enter your password and run:
```sql
USE logindb;
ALTER TABLE interviews DROP COLUMN IF EXISTS interview_time;
DESCRIBE interviews;
```

### Option 3: Using the SQL Script File

A SQL script has been created at [`backend/fix_interview_table.sql`](backend/fix_interview_table.sql). You can:

1. Open MySQL Workbench
2. Go to File → Open SQL Script
3. Select `backend/fix_interview_table.sql`
4. Click Execute (⚡ icon)

## Verification

After running the fix, the `interviews` table should have these columns:
- `id` (bigint, primary key)
- `hr_id` (bigint, not null)
- `panelist_id` (bigint, not null)
- `candidate_id` (bigint, not null)
- `candidate_name` (varchar, not null)
- `candidate_email` (varchar, not null)
- `interview_date` (date, not null)
- `interview_time_from` (time, not null) ✅
- `interview_time_to` (time, not null) ✅
- `feedback` (varchar)
- `position` (varchar, not null)
- `notes` (varchar)
- `status` (varchar, not null)
- `meeting_room_id` (varchar)
- `meeting_link` (varchar)
- `meeting_start_time` (datetime)
- `meeting_end_time` (datetime)
- `duration_minutes` (int)
- `recording_enabled` (tinyint)
- `created_at` (datetime, not null)
- `updated_at` (datetime, not null)

**Note:** The old `interview_time` column should NOT be present.

## Testing

After applying the fix:

1. Restart your Spring Boot application
2. Try scheduling an interview through the HR Dashboard
3. The interview should be created successfully without the SQL error

## Prevention

To prevent similar issues in the future:

1. **Use Flyway or Liquibase** for database migrations instead of relying on `spring.jpa.hibernate.ddl-auto=update`
2. **Version control your database schema** with migration scripts
3. **Test schema changes** in a development environment before production

## Alternative: Change DDL Strategy

If you want Hibernate to automatically handle schema changes (including dropping columns), you can temporarily change in [`application.properties`](backend/src/main/resources/application.properties):

```properties
# WARNING: This will drop and recreate tables, losing all data!
spring.jpa.hibernate.ddl-auto=create-drop
```

**⚠️ CAUTION:** This will delete all existing data. Only use in development!

For production, always use `update` or `validate` and manage schema changes manually.

---

Made with Bob