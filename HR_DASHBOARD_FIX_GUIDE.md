# HR Dashboard Candidate Details Fix Guide

## Problem
The HR Dashboard "Candidate Details" tab is not showing any data.

## Root Cause Analysis
After analyzing the code, I found that:
1. The backend implementation is correct - [`HRService.java`](backend/src/main/java/com/login/service/HRService.java:359) has the `getLoggedInCandidates()` method
2. The frontend [`HRDashboard.js`](frontend/src/components/HRDashboard.js:117) correctly fetches data from the API
3. The issue is likely that **no candidate data exists in the database** or candidates are not marked as logged in

## Solution

### Option 1: Use SQL Script (Recommended - Fastest)

I've created a SQL script that will insert 8 sample candidates directly into your database.

**Steps:**

1. **Open MySQL Command Line or MySQL Workbench**

2. **Run the SQL script:**
   ```bash
   mysql -u root -p logindb < INSERT_SAMPLE_CANDIDATES.sql
   ```
   
   Or in MySQL Workbench:
   - Open the file `INSERT_SAMPLE_CANDIDATES.sql`
   - Execute the entire script

3. **Verify the data:**
   ```sql
   USE logindb;
   SELECT name, email, status, is_logged_in, hr_mail_id 
   FROM candidates 
   WHERE hr_mail_id = 'admin@example.com';
   ```

4. **Refresh your HR Dashboard** - You should now see 8 candidates!

### Option 2: Restart Backend (If DataInitializer hasn't run)

If your database is completely empty, the [`DataInitializer.java`](backend/src/main/java/com/login/config/DataInitializer.java:1) should create sample data automatically.

**Steps:**

1. **Stop the backend server** (if running)

2. **Clear the database** (optional - only if you want fresh data):
   ```sql
   DROP DATABASE logindb;
   CREATE DATABASE logindb;
   ```

3. **Start the backend server:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **Check the console output** - You should see:
   ```
   =================================================
   Creating comprehensive test users for all roles...
   =================================================
   ```

5. **Login to HR Dashboard** with:
   - Username: `admin`
   - Password: `admin123`

### Option 3: Manual Database Insert (Alternative)

If you prefer to insert data manually:

```sql
USE logindb;

-- Insert a sample candidate
INSERT INTO candidates (
    name, email, phone, position, status, experience_years, skills,
    jd_details, joining_date, old_ctc, new_ctc, current_ctc,
    employment_type, location, hr_mail_id, is_logged_in, last_login_at,
    created_at, updated_at, hr_id
) VALUES (
    'Test Candidate',
    'test.candidate@example.com',
    '+91-9999999999',
    'Software Developer',
    'APPLIED',
    3,
    'Java, Spring Boot, React',
    'Looking for a skilled developer',
    DATE_ADD(CURDATE(), INTERVAL 1 MONTH),
    1000000.00,
    1500000.00,
    1000000.00,
    'FULL_TIME',
    'Bangalore, India',
    'admin@example.com',
    TRUE,
    NOW(),
    NOW(),
    NOW(),
    1
);
```

## Verification Steps

After inserting data, verify it's working:

1. **Check Database:**
   ```sql
   SELECT COUNT(*) FROM candidates WHERE hr_mail_id = 'admin@example.com' AND is_logged_in = TRUE;
   ```
   Should return a number > 0

2. **Check Backend API:**
   - Start backend server
   - Login as HR to get a token
   - Test the API endpoint:
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        http://localhost:8081/api/hr/1/logged-in-candidates
   ```

3. **Check Frontend:**
   - Login as `admin` / `admin123`
   - Navigate to "📋 Candidate Details" tab
   - You should see candidate cards with complete information

## Sample Data Provided

The SQL script creates 8 candidates:

| Name | Position | Status | CTC (New) | Location |
|------|----------|--------|-----------|----------|
| John Doe | Senior Java Developer | INTERVIEW | ₹18,00,000 | Bangalore |
| Jane Smith | React Developer | SCREENING | ₹12,00,000 | Hyderabad |
| Bob Johnson | Full Stack Developer | SELECTED | ₹22,00,000 | Pune |
| Alice Williams | DevOps Engineer | APPLIED | ₹15,00,000 | Mumbai |
| Michael Brown | Python Developer | SCREENING | ₹14,00,000 | Chennai |
| Sarah Davis | UI/UX Designer | INTERVIEW | ₹11,00,000 | Bangalore |
| David Wilson | Data Scientist | APPLIED | ₹19,00,000 | Bangalore |
| Emma Martinez | QA Engineer | SCREENING | ₹12,50,000 | Hyderabad |

All candidates are:
- ✅ Marked as logged in (`is_logged_in = TRUE`)
- ✅ Assigned to HR email `admin@example.com`
- ✅ Have complete profile information
- ✅ Have recent login timestamps

## Troubleshooting

### Issue: Still no data showing

**Check 1: Database Connection**
```sql
-- Verify MySQL is running
SHOW DATABASES;

-- Verify logindb exists
USE logindb;

-- Check if candidates table exists
SHOW TABLES;

-- Check candidate count
SELECT COUNT(*) FROM candidates;
```

**Check 2: Backend Logs**
Look for errors in backend console when accessing the dashboard.

**Check 3: Frontend Console**
Open browser DevTools (F12) and check for:
- Network errors when calling `/api/hr/1/logged-in-candidates`
- JavaScript errors in Console tab

**Check 4: HR Email Match**
```sql
-- Verify HR email
SELECT id, email FROM users WHERE role = 'HR';

-- Verify candidates have matching hr_mail_id
SELECT name, email, hr_mail_id FROM candidates;
```

### Issue: Backend not starting

**Check MySQL Connection:**
- Verify MySQL is running on port 3306
- Check credentials in [`application.properties`](backend/src/main/resources/application.properties:6):
  - Username: `root`
  - Password: `root`
  - Database: `logindb`

**Fix:**
```bash
# Start MySQL service
# Windows:
net start MySQL80

# Linux/Mac:
sudo systemctl start mysql
```

### Issue: Authentication errors

**Solution:**
1. Clear browser localStorage
2. Login again as `admin` / `admin123`
3. Check that JWT token is being sent in requests

## Expected Result

After following these steps, your HR Dashboard should display:

1. **Home Tab**: Table with all candidate information
2. **Candidate Details Tab**: 
   - Grid of candidate cards
   - Each card showing complete profile
   - Green online indicator (🟢)
   - All CTC details
   - Contact information
   - Skills and experience

## API Endpoints Used

- **Dashboard Data**: `GET /api/hr/{hrId}/dashboard`
- **Logged-in Candidates**: `GET /api/hr/{hrId}/logged-in-candidates`

Both endpoints require:
- Valid JWT token in Authorization header
- HR role permissions

## Files Modified/Created

1. ✅ [`INSERT_SAMPLE_CANDIDATES.sql`](INSERT_SAMPLE_CANDIDATES.sql:1) - SQL script to insert sample data
2. ✅ [`HR_DASHBOARD_FIX_GUIDE.md`](HR_DASHBOARD_FIX_GUIDE.md:1) - This comprehensive guide

## Next Steps

After data is visible:

1. **Test Real-time Updates**: 
   - Auto-refresh is enabled (every 10 seconds)
   - New logins will show notification

2. **Test Filtering**:
   - Only candidates with matching HR email are shown
   - Only logged-in candidates appear in "Candidate Details" tab

3. **Add More Candidates**:
   - Use the SQL script as a template
   - Modify values and insert more records

## Support

If you still face issues:

1. Check backend console for errors
2. Check browser console (F12) for frontend errors
3. Verify database connection and data
4. Ensure both backend (port 8081) and frontend (port 3000) are running

---

**Made with ❤️ by Bob**