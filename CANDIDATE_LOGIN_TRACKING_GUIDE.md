# Candidate Login Tracking Feature Guide

## Overview
This feature automatically tracks when candidates log in to the system and displays their details in real-time on the HR Dashboard's "Candidate Details" tab. **HR users will only see candidates who have entered their specific email address in the candidate info form.**

## Features Implemented

### 1. Backend Changes

#### Candidate Model Updates
- **File**: `backend/src/main/java/com/login/model/Candidate.java`
- **New Fields**:
  - `lastLoginAt` (LocalDateTime): Tracks the last login timestamp
  - `isLoggedIn` (Boolean): Indicates if the candidate is currently logged in

#### CandidateService Updates
- **File**: `backend/src/main/java/com/login/service/CandidateService.java`
- **New Methods**:
  - `trackCandidateLogin(String email)`: Updates login status when candidate logs in
  - `trackCandidateLogout(String email)`: Updates login status when candidate logs out
  - `getLoggedInCandidates()`: Returns list of all currently logged-in candidates

#### AuthService Updates
- **File**: `backend/src/main/java/com/login/service/AuthService.java`
- **Changes**:
  - Automatically tracks candidate login in both `login()` and `loginWithMfa()` methods
  - Calls `candidateService.trackCandidateLogin()` when a candidate successfully logs in

#### HRService Updates
- **File**: `backend/src/main/java/com/login/service/HRService.java`
- **Changes**:
  - Added `isLoggedIn` and `lastLoginAt` fields to dashboard records
  - New method: `getLoggedInCandidates(Long hrId)`: Returns detailed information about logged-in candidates **filtered by HR email ID**
  - Filters candidates based on `hrMailId` field matching the logged-in HR's email

#### HRController Updates
- **File**: `backend/src/main/java/com/login/controller/HRController.java`
- **New Endpoint**:
  - `GET /api/hr/{hrId}/logged-in-candidates`: Fetches all currently logged-in candidates

### 2. Frontend Changes

#### HRDashboard Component Updates
- **File**: `frontend/src/components/HRDashboard.js`
- **Changes**:
  - Added `loggedInCandidates` state to store logged-in candidate data
  - Added `newCandidatesCount` state for notification badge
  - New function: `fetchLoggedInCandidates()`: Fetches logged-in candidates from backend
  - Auto-refresh every 10 seconds to show real-time updates
  - **Notification badge** on "Candidate Details" tab when new candidates log in
  - Browser notifications for new candidate logins (if permission granted)
  - Updated "Candidate Details" tab to show only logged-in candidates **who entered this HR's email**
  - Added online indicator (🟢) for logged-in candidates
  - Displays last login timestamp for each candidate
  - Shows comprehensive candidate information including:
    - Email, Phone, Last Login Time
    - Assigned Panelist
    - JD Details, Interview Schedule
    - Joining Date, CTC Information
    - Employment Type, Location
    - Position, Experience, Skills

#### CSS Updates
- **File**: `frontend/src/components/HRDashboard.css`
- **New Styles**:
  - `.section-description`: Styling for section descriptions
  - `.hint-text`: Styling for hint messages
  - `.online-indicator`: Animated green dot indicator for online status
  - `.candidate-avatar`: Relative positioning for avatar container
  - `.notification-badge`: Red badge showing count of new candidates
  - Pulse animation for online indicator
  - Bounce animation for notification badge

## How It Works

### Candidate Info Submission Flow
1. Candidate fills out the Candidate Info form
2. Candidate enters HR's email address in the "HR Mail ID" field
3. Form data is saved with `hrMailId` field in the database
4. This links the candidate to the specific HR

### Login Flow
1. Candidate logs in through the login page
2. `AuthService.login()` or `AuthService.loginWithMfa()` is called
3. After successful authentication, `candidateService.trackCandidateLogin()` is triggered
4. Candidate's `lastLoginAt` is updated to current timestamp
5. Candidate's `isLoggedIn` flag is set to `true`
6. Changes are saved to the database

### HR Dashboard Display
1. HR logs in and navigates to the dashboard
2. Dashboard automatically fetches logged-in candidates via `/api/hr/{hrId}/logged-in-candidates`
3. **Backend filters candidates** to show only those where `hrMailId` matches the HR's email
4. Auto-refresh runs every 10 seconds to show real-time updates
5. "Candidate Details" tab displays only candidates with `isLoggedIn = true` **AND** `hrMailId = HR's email`
6. Each candidate card shows:
   - Green online indicator (🟢)
   - Last login timestamp
   - Complete candidate information

### Notification System
1. When new candidates log in, the count increases
2. A red notification badge appears on the "Candidate Details" tab
3. Browser notification is shown (if permission granted)
4. Badge clears when HR clicks on the "Candidate Details" tab

### Real-Time Updates
- Auto-refresh is enabled by default (can be toggled)
- Refreshes every 10 seconds
- Shows count of currently logged-in candidates
- Updates automatically when candidates log in/out

## API Endpoints

### Get Logged-In Candidates
```
GET /api/hr/{hrId}/logged-in-candidates
```

**Headers**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Response**:
```json
{
  "success": true,
  "candidates": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "1234567890",
      "position": "Software Engineer",
      "status": "INTERVIEW",
      "experienceYears": 5,
      "skills": "Java, Spring Boot, React",
      "jdDetails": "Full stack developer position...",
      "joiningDate": "2024-01-15",
      "oldCtc": 800000,
      "newCtc": 1200000,
      "currentCtc": 800000,
      "employmentType": "FULL_TIME",
      "location": "Bangalore",
      "isLoggedIn": true,
      "lastLoginAt": "2024-01-10T10:30:00",
      "assignedPanelist": {
        "id": 2,
        "username": "panelist1",
        "email": "panelist@example.com"
      },
      "interview": {
        "date": "2024-01-12",
        "time": "14:00",
        "status": "SCHEDULED"
      }
    }
  ],
  "count": 1
}
```

## Database Schema Changes

### Candidates Table
New columns added:
```sql
ALTER TABLE candidates ADD COLUMN last_login_at TIMESTAMP;
ALTER TABLE candidates ADD COLUMN is_logged_in BOOLEAN DEFAULT FALSE;
```

## Testing the Feature

### Step 1: Start the Backend
```bash
cd backend
mvn spring-boot:run
```

### Step 2: Start the Frontend
```bash
cd frontend
npm start
```

### Step 3: Test Login Tracking
1. Register a new candidate account (or use existing)
2. Log in as the candidate
3. Check backend console logs for "Tracked login for candidate: {email}"

### Step 4: View in HR Dashboard
1. Log in as HR user
2. Navigate to "Candidate Details" tab
3. You should see the logged-in candidate with:
   - Green online indicator (🟢)
   - Last login timestamp
   - All candidate details

### Step 5: Test Auto-Refresh
1. Keep HR dashboard open
2. Log in as another candidate in a different browser/incognito window
3. Within 10 seconds, the new candidate should appear in HR dashboard
4. Toggle auto-refresh button to test manual refresh mode

## Features

✅ Automatic login tracking for candidates
✅ Real-time display in HR Dashboard
✅ **HR-specific filtering** - Only shows candidates who entered that HR's email
✅ **Notification badge** for new candidate logins
✅ **Browser notifications** for new candidates
✅ Auto-refresh every 10 seconds
✅ Visual online indicator with pulse animation
✅ Last login timestamp
✅ Comprehensive candidate information
✅ Filter to show only logged-in candidates
✅ Count of online candidates
✅ Responsive design
✅ Error handling and logging

## Future Enhancements

- Add logout tracking (currently only tracks login)
- Add session timeout to automatically set `isLoggedIn = false`
- Add notification when new candidate logs in
- Add filter/search functionality for logged-in candidates
- Add export functionality for logged-in candidate data
- Add analytics dashboard for login patterns

## Troubleshooting

### Candidates not showing in HR Dashboard
1. **Verify candidate entered the correct HR email** in the Candidate Info form
2. Check if candidate has logged in successfully
3. Verify backend logs show "Tracked login for candidate"
4. Check backend logs for "Logged-in candidates for this HR" count
5. Verify the `hrMailId` field matches the HR's email (case-insensitive)
6. Check browser console for API errors
7. Verify auto-refresh is enabled
8. Try manual refresh by toggling auto-refresh button

### Login tracking not working
1. Verify database columns exist (`last_login_at`, `is_logged_in`)
2. Check backend logs for errors
3. Verify candidate exists in database
4. Check if `CandidateService` is properly autowired in `AuthService`

### Auto-refresh not working
1. Check if auto-refresh toggle is enabled (green)
2. Verify no console errors
3. Check network tab for API calls every 10 seconds
4. Try disabling and re-enabling auto-refresh

## Notes

- Login tracking is automatic and requires no manual intervention
- The feature works for both regular login and MFA login
- **HR can only see candidates who entered their email address** in the Candidate Info form
- The filtering is case-insensitive for email matching
- The green indicator pulses to show real-time status
- Last login time is displayed in local timezone (Asia/Calcutta)
- Notification badge shows count of new candidates since last view
- Browser notifications require user permission

## Important: HR Email Matching

For candidates to appear in a specific HR's dashboard:
1. Candidate must fill out the Candidate Info form
2. Candidate must enter the HR's email in the "HR Mail ID" field
3. The email must match exactly (case-insensitive)
4. Candidate must then log in to the system

**Example:**
- HR email: `hr@company.com`
- Candidate enters: `hr@company.com` in Candidate Info form
- Candidate logs in
- ✅ Candidate appears in that HR's "Candidate Details (Logged In)" tab

## Made with Bob ❤️