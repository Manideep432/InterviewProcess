# All Candidates Feature - Complete Guide

## Overview
The "All Candidates" page in the HR Dashboard displays all candidates who have saved their information through the Candidate Info form. This guide explains how the feature works and how to test it.

## How It Works

### 1. Candidate Saves Data
When a candidate (logged in with CANDIDATE role) fills and saves their information:
- Navigate to **Candidate Info** tab
- Fill in required fields:
  - Candidate Name
  - Mail ID
  - Phone Number
  - Location
- Optional fields:
  - Current CTC
  - HR Mail ID (for email notification)
  - Position
  - Experience Years
  - Skills (max 200 words)
  - Photo (JPG/PNG)
  - CV (PDF)
  - Government ID (JPG)
- Click **Save** button

### 2. Auto-Assignment to HR
The system automatically:
- Assigns the candidate to the **first available HR** in the database
- Saves candidate data to the database
- Sends candidate details as PDF to HR email (if HR Mail ID provided)
- Makes the candidate visible in HR Dashboard

### 3. HR Views Candidates
HR can view candidates in two places:

#### A. Dashboard Tab (Summary View)
- Shows candidate details with panelist assignments
- Displays interview information
- Shows CTC details and employment type

#### B. All Candidates Tab (Detailed View)
- Click **"👥 All Candidates"** tab
- Shows comprehensive candidate information:
  - ID, Name, Email, Phone
  - Position, Status, Experience
  - Skills, JD Details
  - Joining Date, Old CTC, New CTC
  - Employment Type, Location
  - Assigned Panelist
  - Registration Date
- **NEW** badge for candidates registered in last 24 hours
- Auto-refresh every 30 seconds (if enabled)

## Testing Steps

### Step 1: Create Test Users
Ensure you have these users in your system:

```sql
-- Check if HR user exists
SELECT * FROM users WHERE role = 'HR';

-- Check if CANDIDATE user exists
SELECT * FROM users WHERE role = 'CANDIDATE';
```

If not, register them through the registration page.

### Step 2: Login as Candidate
1. Login with CANDIDATE credentials
2. Navigate to **Candidate Info** tab
3. Fill in the form with test data:
   ```
   Candidate Name: John Doe
   Mail ID: john.doe@example.com
   Phone Number: 9876543210
   Location: Bangalore
   Current CTC: 5.5
   HR Mail ID: hr@company.com
   Position: Software Engineer
   Experience Years: 3
   Skills: Java, Spring Boot, React, MySQL
   ```
4. Upload files (optional but recommended)
5. Click **Save**
6. Wait for success message: "✓ Saved successfully!"

### Step 3: Login as HR
1. Logout from candidate account
2. Login with HR credentials
3. You should see the HR Dashboard

### Step 4: View Candidates
1. Click on **"👥 All Candidates"** tab
2. You should see the candidate you just created
3. The candidate will have a **NEW** badge if created within last 24 hours
4. All candidate details will be displayed in the table

### Step 5: Verify Auto-Refresh
1. Keep HR Dashboard open
2. In another browser/incognito window, login as another candidate
3. Save candidate information
4. Within 30 seconds, the HR Dashboard should show a notification:
   - "🔔 1 new candidate(s) registered!"
5. The dashboard will auto-reload and show the new candidate

## API Endpoints

### Save Candidate Info (Candidate)
```
POST http://localhost:8081/api/candidates/save-info
Headers: Authorization: Bearer <token>
Content-Type: multipart/form-data

Body (FormData):
- candidateName: string (required)
- mailId: string (required)
- phoneNumber: string (required)
- location: string (required)
- currentCtc: string (optional)
- hrMailId: string (optional)
- position: string (optional)
- experienceYears: number (optional)
- skills: string (optional, max 200 words)
- photo: file (optional, JPG/PNG)
- cv: file (optional, PDF)
- gvtId: file (optional, JPG)
```

### Get All Candidates (HR)
```
GET http://localhost:8081/api/hr/{hrId}/all-candidates
Headers: Authorization: Bearer <token>

Response:
{
  "success": true,
  "candidates": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "9876543210",
      "position": "Software Engineer",
      "status": "APPLIED",
      "experienceYears": 3,
      "skills": "Java, Spring Boot, React, MySQL",
      "jdDetails": null,
      "joiningDate": null,
      "oldCtc": null,
      "newCtc": null,
      "employmentType": null,
      "location": "Bangalore",
      "assignedPanelist": null,
      "interview": null,
      "createdAt": "2026-05-14T17:21:33.907"
    }
  ]
}
```

## Troubleshooting

### Issue: "No candidates found" message

**Possible Causes:**
1. No candidates have saved their data yet
2. Candidates are assigned to a different HR
3. Database connection issue

**Solutions:**

#### Solution 1: Check Database
```sql
-- Check all candidates
SELECT * FROM candidates;

-- Check candidates assigned to specific HR
SELECT c.*, u.username as hr_username 
FROM candidates c 
LEFT JOIN users u ON c.hr_id = u.id 
WHERE u.role = 'HR';

-- Check if candidates exist but not assigned to HR
SELECT * FROM candidates WHERE hr_id IS NULL;
```

#### Solution 2: Manually Assign Candidates to HR
If candidates exist but aren't assigned:
```sql
-- Get HR user ID
SELECT id, username FROM users WHERE role = 'HR';

-- Assign all unassigned candidates to HR (replace 2 with actual HR ID)
UPDATE candidates SET hr_id = 2 WHERE hr_id IS NULL;
```

#### Solution 3: Create Test Candidate
```sql
-- Insert test candidate (replace hr_id with actual HR user ID)
INSERT INTO candidates (name, email, phone, location, status, hr_id, created_at, updated_at)
VALUES ('Test Candidate', 'test@example.com', '1234567890', 'Mumbai', 'APPLIED', 2, NOW(), NOW());
```

### Issue: Auto-refresh not working

**Solution:**
1. Check if "Auto-refresh" checkbox is enabled in HR Dashboard
2. Verify browser console for any errors
3. Ensure backend is running on port 8081

### Issue: Candidate data not saving

**Solution:**
1. Check browser console for errors
2. Verify backend logs for exceptions
3. Ensure all required fields are filled
4. Check if candidate email already exists in database

## Features

### Real-time Updates
- Auto-refresh every 30 seconds
- Notification banner for new candidates
- Automatic dashboard reload when new users register

### Candidate Status Badges
- **APPLIED**: Blue badge - Initial status
- **SCREENING**: Orange badge - Under review
- **INTERVIEW**: Purple badge - Interview scheduled
- **SELECTED**: Green badge - Candidate selected
- **REJECTED**: Red badge - Candidate rejected

### Visual Indicators
- **NEW** badge for candidates registered in last 24 hours
- Highlighted rows for new candidates
- Color-coded status badges
- Responsive table with horizontal scroll

### Data Display
- Comprehensive candidate information
- Truncated text with tooltips for long content
- Formatted currency (₹) for CTC values
- Formatted dates in Indian format (DD/MM/YYYY)
- Assigned panelist information with email

## Database Schema

### Candidates Table
```sql
CREATE TABLE candidates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    position VARCHAR(100),
    status VARCHAR(50),
    experience_years INT,
    skills VARCHAR(500),
    hr_id BIGINT,
    assigned_panelist_id BIGINT,
    jd_details VARCHAR(2000),
    joining_date DATE,
    old_ctc DECIMAL(10,2),
    new_ctc DECIMAL(10,2),
    current_ctc DECIMAL(10,2),
    hr_mail_id VARCHAR(100),
    employment_type VARCHAR(50),
    location VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (hr_id) REFERENCES users(id),
    FOREIGN KEY (assigned_panelist_id) REFERENCES users(id)
);
```

## Success Criteria

✅ Candidate can save their information
✅ Data is automatically assigned to HR
✅ HR can view all candidates in "All Candidates" tab
✅ Real-time updates work (auto-refresh)
✅ New candidate notifications appear
✅ All candidate details are displayed correctly
✅ Status badges show correct colors
✅ NEW badge appears for recent candidates

## Next Steps

After candidates appear in the "All Candidates" page:
1. HR can assign panelists to candidates
2. Panelists can schedule interviews
3. HR can track candidate status
4. HR can view statistics and reports

## Support

If you encounter any issues:
1. Check browser console for JavaScript errors
2. Check backend logs for Java exceptions
3. Verify database connectivity
4. Ensure all services are running on correct ports
5. Clear browser cache and reload

---

**Made with ❤️ by Bob**