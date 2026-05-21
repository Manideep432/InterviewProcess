# Fix Interview JRS Field Error

## Problem
Error: `Field 'position' doesn't have a default value` when inserting into interviews table.

This occurs because the MySQL database has a column named 'position' but the Java entity uses 'jrs' (Job Requirement Specification).

## Solution Applied

### 1. Updated Interview Entity
Modified `backend/src/main/java/com/login/model/Interview.java`:
- Changed `jrs` field from `nullable = false` to `nullable = true`
- Added default value: `private String jrs = "";`

This allows the field to be optional and prevents null constraint violations.

### 2. Database Schema Fix Required

You need to update your MySQL database schema. Choose ONE of the following options:

#### Option A: Rename 'position' column to 'jrs' (Recommended)
```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE logindb;

-- Rename the column
ALTER TABLE interviews CHANGE COLUMN position jrs VARCHAR(255);

-- Make it nullable
ALTER TABLE interviews MODIFY COLUMN jrs VARCHAR(255) NULL;
```

#### Option B: Drop and recreate the table (if no important data)
```sql
-- Connect to MySQL
mysql -u root -p

-- Use the database
USE logindb;

-- Drop the interviews table
DROP TABLE IF EXISTS interviews;

-- Restart your Spring Boot application
-- Hibernate will recreate the table with the correct schema
```

#### Option C: Use the provided SQL script
```bash
# Navigate to backend directory
cd backend

# Run the SQL script
mysql -u root -p logindb < fix_interviews_schema.sql
```

## Steps to Fix

1. **Stop your Spring Boot application** if it's running

2. **Connect to MySQL** and run ONE of the SQL options above

3. **Verify the change**:
   ```sql
   USE logindb;
   DESCRIBE interviews;
   ```
   You should see a `jrs` column (not `position`)

4. **Restart your Spring Boot application**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

5. **Test creating an interview** - the error should be resolved

## Alternative: Fresh Database Start

If you don't have important data and want a clean start:

1. Drop the entire database:
   ```sql
   DROP DATABASE IF EXISTS logindb;
   CREATE DATABASE logindb;
   ```

2. Restart your Spring Boot application - Hibernate will create all tables with the correct schema

## Verification

After applying the fix, test by:
1. Login as HR
2. Try to schedule an interview
3. The interview should be created successfully without the "position" field error

## Files Modified
- `backend/src/main/java/com/login/model/Interview.java` - Made jrs field nullable with default value
- `backend/fix_interviews_schema.sql` - SQL script to fix database schema

---
Made with Bob