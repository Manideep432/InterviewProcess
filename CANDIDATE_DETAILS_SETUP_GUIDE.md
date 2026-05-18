# Candidate Details Setup Guide

## Problem
You're seeing "No candidates are currently logged in" in the HR Dashboard because there are no candidates with:
1. `isLoggedIn = true` (currently logged in)
2. `hrMailId` matching your HR email

## Solution Options

### Option 1: Reset Database (Recommended for Testing)

1. **Stop your backend server** (if running)

2. **Delete the database file** (H2 database):
   ```bash
   # Navigate to your project directory
   cd backend
   
   # Delete the H2 database file
   rm -f logindb.mv.db
   # Or on Windows:
   del logindb.mv.db
   ```

3. **Restart the backend server**:
   ```bash
   mvn spring-boot:run
   ```

4. **The DataInitializer will create sample data** including:
   - HR user: `admin` / `admin123` (admin@example.com)
   - 2 Logged-in candidates with complete details:
     - John Doe (john.doe@example.com) - INTERVIEW status
     - Jane Smith (jane.smith@example.com) - SCREENING status

5. **Login as HR**:
   - Username: `admin`
   - Password: `admin123`

6. **Navigate to "Candidate Details" tab** - You should see 2 candidates online!

### Option 2: Add Candidates Manually

If you want to keep your existing data:

1. **Login as a candidate** (e.g., `manideep@example.com` / `password123`)

2. **Fill out the Candidate Info form** with:
   - Name: Your name
   - Phone: Your phone number
   - Position: e.g., "Java Developer"
   - Experience: e.g., 5 years
   - Skills: e.g., "Java, Spring Boot, React"
   - Current CTC: e.g., 1200000
   - HR Email: **admin@example.com** (or your HR's email)
   - JD Details: Job description
   - Employment Type: FULL_TIME
   - Location: Your location

3. **Submit the form** - This will:
   - Create a candidate record
   - Set `hrMailId` to the HR email you provided
   - Mark you as logged in (`isLoggedIn = true`)

4. **Login as HR** (admin@example.com)

5. **Check the "Candidate Details" tab** - You should see your candidate profile!

## Pre-configured Test Data

After database reset, you'll have these users:

### HR Users
- `admin` / `admin123` (admin@example.com)
- `hr_manager` / `hr123` (hr.manager@example.com)
- `recruiter1` / `recruiter123` (recruiter1@example.com)

### Candidate Users (with complete profiles)
- `john_doe` / `john123` - **Logged In, HR: admin@example.com**
- `jane_smith` / `jane123` - **Logged In, HR: admin@example.com**
- `bob_johnson` / `bob123` - Logged In, HR: hr.manager@example.com
- `Manideep` / `password123` - Profile incomplete
- `Manideep1` / `password123` - Profile incomplete
- `testuser` / `test123` - Profile incomplete

### Panelist Users
- `panelist1` / `panelist123` (Java Development)
- `panelist2` / `panelist123` (Frontend Development)
- `tech_expert` / `expert123` (Cloud Architecture)
- `senior_dev` / `senior123` (Database & Backend)

## How the System Works

1. **Candidate logs in** → `isLoggedIn` set to `true`, `lastLoginAt` updated
2. **Candidate fills CandidateInfo form** → `hrMailId` is set
3. **HR Dashboard filters candidates** by:
   - `isLoggedIn = true` (currently online)
   - `hrMailId` matches HR's email
4. **Auto-refresh every 10 seconds** to show new logins

## Troubleshooting

### Still seeing "No candidates"?

1. **Check backend logs** for:
   ```
   === Fetching Logged-In Candidates for HR ===
   HR Email: admin@example.com
   Total candidates: X
   Logged-in candidates: Y
   Logged-in candidates for this HR: Z
   ```

2. **Verify candidate data** in database:
   - Open H2 Console: http://localhost:8081/h2-console
   - JDBC URL: `jdbc:h2:./logindb`
   - Username: `sa`
   - Password: (leave empty)
   - Query: `SELECT name, email, hr_mail_id, is_logged_in FROM candidates;`

3. **Check HR email matches**:
   - HR email in database
   - Candidate's `hr_mail_id` field
   - They must match exactly (case-insensitive)

## Quick Test

1. Reset database (Option 1 above)
2. Start backend
3. Login as `admin` / `admin123`
4. Go to "Candidate Details" tab
5. You should see **2 candidates online** with full details!

---

**Made with Bob** 🤖