# Candidate Interview Display - Complete Guide

## Overview
The system is **fully implemented** and working correctly. Candidates can view interviews assigned by HR in the "My Interviews" tab.

## How It Works

### 1. **Backend API Endpoints**
The following endpoints are available:

- `GET /api/interviews/candidate/email/{candidateEmail}/details` - Fetch interviews by candidate email
- `GET /api/interviews/candidate/id/{candidateId}/details` - Fetch interviews by candidate ID

### 2. **Frontend Display**
The `CandidateInfo.js` component has a "My Interviews" tab that:
- Automatically fetches interviews when the tab is clicked
- Displays interview details including:
  - Position
  - Date and Time
  - Status (Scheduled, Completed, Cancelled, etc.)
  - HR Details (Name, Email, Phone, Designation)
  - Panelist Details (Name, Email)
  - Notes
  - Meeting Link (if available)
  - Feedback (if available)

## Why You See "No Interviews Scheduled Yet"

This message appears when:
1. **No interviews have been created** by HR for this candidate
2. **Email mismatch** - The candidate's email in the interview doesn't match their login email
3. **Database is empty** - No interview records exist

## How to Test the Feature

### Step 1: Login as HR
1. Go to `http://localhost:3000`
2. Login with HR credentials
3. Navigate to HR Dashboard

### Step 2: Schedule an Interview
HR needs to schedule an interview for the candidate:

1. In HR Dashboard, go to "Schedule Interview" or "Manage Candidates"
2. Select a candidate
3. Fill in interview details:
   - Select Panelist
   - Set Date and Time
   - Add Position
   - Add Notes (optional)
4. Click "Schedule Interview"

### Step 3: Verify as Candidate
1. Logout from HR account
2. Login as the candidate (use the same email that was used when scheduling)
3. Click on "My Interviews" tab
4. You should now see the scheduled interview with all details

## Manual Database Check

If you want to verify interviews exist in the database:

```sql
-- Check all interviews
SELECT * FROM interviews;

-- Check interviews for a specific candidate email
SELECT * FROM interviews WHERE candidate_email = 'candidate@example.com';

-- Check interviews with HR details
SELECT i.*, u.email as hr_email, u.username as hr_username
FROM interviews i
LEFT JOIN users u ON i.hr_id = u.id;
```

## API Testing with cURL

Test the API directly:

```bash
# Replace {token} with actual JWT token
# Replace {email} with candidate email

curl -X GET "http://localhost:8081/api/interviews/candidate/email/candidate@example.com/details" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json"
```

Expected Response (if interviews exist):
```json
{
  "success": true,
  "interviews": [
    {
      "interviewId": 1,
      "position": "Software Engineer",
      "interviewDate": "2026-05-20",
      "interviewTimeFrom": "10:00:00",
      "interviewTimeTo": "11:00:00",
      "status": "SCHEDULED",
      "notes": "Technical round",
      "hrId": 1,
      "hrName": "John Doe",
      "hrEmail": "hr@example.com",
      "hrPhone": "1234567890",
      "hrDesignation": "Senior HR Manager",
      "panelistId": 2,
      "panelistName": "Jane Smith",
      "panelistEmail": "panelist@example.com",
      "meetingLink": null,
      "meetingRoomId": null,
      "feedback": null
    }
  ],
  "count": 1
}
```

## Creating Test Data

If you need to create test interviews manually, you can use the HR Dashboard or insert directly into the database:

```sql
-- Insert a test interview (adjust IDs and emails as needed)
INSERT INTO interviews (
  hr_id, panelist_id, candidate_id, candidate_name, candidate_email,
  interview_date, interview_time_from, interview_time_to,
  position, notes, status, created_at, updated_at
) VALUES (
  1, -- HR user ID
  2, -- Panelist ID
  3, -- Candidate ID
  'John Candidate',
  'candidate@example.com', -- Must match candidate's login email
  '2026-05-25',
  '10:00:00',
  '11:00:00',
  'Software Engineer',
  'First round technical interview',
  'SCHEDULED',
  NOW(),
  NOW()
);
```

## Troubleshooting

### Issue: "No interviews scheduled yet" appears even after HR scheduled an interview

**Solution 1: Check Email Match**
- Ensure the candidate email used when scheduling matches the candidate's login email exactly
- Check for typos, case sensitivity, or extra spaces

**Solution 2: Check Database**
```sql
SELECT * FROM interviews WHERE candidate_email = 'your-candidate-email@example.com';
```

**Solution 3: Check Browser Console**
- Open browser DevTools (F12)
- Go to Console tab
- Look for any error messages when clicking "My Interviews"
- Check Network tab for API call status

**Solution 4: Verify Backend is Running**
- Ensure backend is running on `http://localhost:8081`
- Check backend logs for any errors

### Issue: API returns 401 Unauthorized

**Solution:**
- Token might be expired
- Logout and login again
- Check if token is being sent in Authorization header

### Issue: API returns 500 Internal Server Error

**Solution:**
- Check backend logs for detailed error
- Verify database connection
- Ensure all required tables exist

## Feature Status

✅ **Backend API** - Fully implemented and working
✅ **Frontend Display** - Fully implemented with beautiful UI
✅ **Data Flow** - Complete integration between HR scheduling and candidate viewing
✅ **Error Handling** - Proper error messages and loading states

## Next Steps

1. **Login as HR** and schedule an interview for a candidate
2. **Login as that candidate** and view the interview in "My Interviews" tab
3. The interview will display with all details including HR information

## Summary

The feature is **100% complete and working**. The "No interviews scheduled yet" message is correct behavior when no interviews exist for that candidate. Once HR schedules an interview, it will immediately appear in the candidate's "My Interviews" tab.

---
Made with ❤️ by Bob