# Quick Backend Restart Guide

## The Issue
You're getting "❌ Failed to fetch feedbacks (Status: 500)" error.

## The Fix
I've implemented automatic database schema validation and creation. Just restart your backend!

## Steps to Fix

### Windows (PowerShell/CMD)

1. **Stop the backend** (if running):
   - Press `Ctrl+C` in the terminal where backend is running

2. **Navigate to backend directory:**
   ```cmd
   cd backend
   ```

3. **Start the backend:**
   ```cmd
   mvnw spring-boot:run
   ```
   
   OR if you have Maven installed globally:
   ```cmd
   mvn spring-boot:run
   ```

### Linux/Mac (Terminal)

1. **Stop the backend** (if running):
   - Press `Ctrl+C` in the terminal where backend is running

2. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

3. **Start the backend:**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   OR if you have Maven installed globally:
   ```bash
   mvn spring-boot:run
   ```

## What to Look For

### ✅ Success Indicators

You should see these messages in the console:

```
=================================================
Validating Database Schema...
=================================================
✅ interview_feedback table exists
✅ Critical columns validated
=================================================
Database Schema Validation Complete
=================================================
```

OR if the table was missing:

```
=================================================
Validating Database Schema...
=================================================
⚠️  interview_feedback table not found. Creating table...
✅ interview_feedback table created successfully
=================================================
```

Then:

```
📋 Creating Interview Feedback Records...
✅ Created Feedback for Bob Johnson (SELECTED)
✅ Created Feedback for John Doe (SELECTED - Partial Data)
✅ Created Feedback for Jane Smith (HOLD - Minimal Data)
✅ Created Feedback for Sarah Davis (REJECTED)
```

### ❌ Error Indicators

If you see errors like:
```
❌ Error creating interview feedback records: ...
```

This means there's a database connection issue. See troubleshooting below.

## Test the Fix

1. **Open browser:** `http://localhost:3000`

2. **Login as HR:**
   - Username: `admin`
   - Password: `admin123`

3. **Click "📋 Candidate Feedback" tab**

4. **You should see 4 feedback records!**

## Troubleshooting

### Issue: MySQL Connection Error

**Error Message:**
```
Communications link failure
```

**Solution:**
1. Make sure MySQL is running:
   ```cmd
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl start mysql
   ```

2. Verify credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=root
   ```

### Issue: Database Doesn't Exist

**Error Message:**
```
Unknown database 'logindb'
```

**Solution:**
The database should be created automatically, but if not:
```sql
mysql -u root -p
CREATE DATABASE logindb;
EXIT;
```

Then restart the backend.

### Issue: Port Already in Use

**Error Message:**
```
Port 8081 is already in use
```

**Solution:**
1. Find and kill the process using port 8081:
   ```cmd
   # Windows
   netstat -ano | findstr :8081
   taskkill /PID <PID> /F
   
   # Linux/Mac
   lsof -ti:8081 | xargs kill -9
   ```

2. Restart the backend

### Issue: Maven Not Found

**Error Message:**
```
'mvn' is not recognized as an internal or external command
```

**Solution:**
Use the Maven wrapper instead:
```cmd
# Windows
mvnw spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

## Alternative: Clean Start

If issues persist, try a clean start:

```cmd
# Stop backend
# Navigate to backend directory
cd backend

# Clean and rebuild
mvn clean install

# Start backend
mvn spring-boot:run
```

## What Was Fixed

1. ✅ Created `DatabaseSchemaValidator.java` - Automatically creates missing tables
2. ✅ Enhanced error logging in `InterviewFeedbackController.java`
3. ✅ Added error handling in `InterviewFeedbackService.java`
4. ✅ Improved `DataInitializer.java` with try-catch blocks
5. ✅ Added execution order to ensure proper initialization

## Expected Result

After restart, you should be able to:
- ✅ View all 4 candidate feedbacks
- ✅ Download PDF for each feedback
- ✅ See different statuses (SELECTED, HOLD, REJECTED)
- ✅ No more 500 errors!

---

**Need more help?** Check [`CANDIDATE_FEEDBACK_ERROR_FIX.md`](CANDIDATE_FEEDBACK_ERROR_FIX.md) for detailed troubleshooting.

**Made with Bob** 🤖