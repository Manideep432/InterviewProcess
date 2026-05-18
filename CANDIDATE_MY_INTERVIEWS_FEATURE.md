# Candidate "My Interviews" Feature - Complete Guide

## Overview
This feature allows candidates to view all their scheduled interviews in the "My Interviews" tab of their dashboard. When HR schedules an interview, all details including HR information, panelist details, date, time, and status are displayed to the candidate.

## Feature Components

### Backend Components

#### 1. Interview Entity
**File**: [`backend/src/main/java/com/login/model/Interview.java`](backend/src/main/java/com/login/model/Interview.java)

Key fields:
- `hrId` - ID of the HR who scheduled the interview
- `candidateId` - ID of the candidate
- `panelistId` - ID of the assigned panelist
- `candidateName` and `candidateEmail` - Candidate details
- `interviewDate` - Date of the interview
- `interviewTimeFrom` and `interviewTimeTo` - Time range
- `position` - Job position
- `status` - Interview status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED)
- `notes` - Additional notes from HR
- `feedback` - Interview feedback
- `meetingLink` and `meetingRoomId` - Video meeting details

#### 2. CandidateInterviewDTO
**File**: [`backend/src/main/java/com/login/dto/CandidateInterviewDTO.java`](backend/src/main/java/com/login/dto/CandidateInterviewDTO.java)

This DTO enriches interview data with:
- **HR Details**: name, email, phone, designation
- **Panelist Details**: name, email
- **Interview Details**: all interview fields
- **Meeting Details**: meeting link and room ID

#### 3. InterviewService
**File**: [`backend/src/main/java/com/login/service/InterviewService.java`](backend/src/main/java/com/login/service/InterviewService.java)

Key methods:
- `getCandidateInterviewsWithDetails(Long candidateId)` - Fetches interviews by candidate ID with HR/panelist details
- `getCandidateInterviewsByEmailWithDetails(String candidateEmail)` - Fetches interviews by candidate email with HR/panelist details

These methods:
1. Fetch all interviews for the candidate
2. For each interview, fetch HR user details from User table
3. Fetch HR profile details (name, phone, designation) from HRProfile table
4. Fetch panelist details from Panelist table
5. Return enriched DTOs with all information

#### 4. InterviewController
**File**: [`backend/src/main/java/com/login/controller/InterviewController.java`](backend/src/main/java/com/login/controller/InterviewController.java)

API Endpoints:
- `GET /api/interviews/candidate/id/{candidateId}/details` - Get interviews by candidate ID
- `GET /api/interviews/candidate/email/{candidateEmail}/details` - Get interviews by candidate email

### Frontend Components

#### CandidateInfo Component
**File**: [`frontend/src/components/CandidateInfo.js`](frontend/src/components/CandidateInfo.js)

**Navigation Tab** (Lines 266-271):
```javascript
<button
  className={`nav-link ${activeTab === 'myInterviews' ? 'active' : ''}`}
  onClick={() => setActiveTab('myInterviews')}
>
  My Interviews
</button>
```

**Data Fetching** (Lines 171-212):
- Automatically fetches interviews when "My Interviews" tab is activated
- Uses the candidate's email from the user object
- Calls: `GET /api/interviews/candidate/email/{email}/details`

**Interview Display** (Lines 529-634):
Shows for each interview:
1. **Header**: Position and status badge
2. **Date & Time**: Formatted interview date and time range
3. **HR Details Section**:
   - HR Name
   - HR Designation
   - HR Email
   - HR Phone
4. **Panelist Details Section**:
   - Panelist Name
   - Panelist Email
5. **Additional Information**:
   - Notes from HR
   - Meeting link (if available)
   - Feedback (if provided)

**Status Badges**:
- SCHEDULED - Blue badge
- COMPLETED - Green badge
- CANCELLED - Red badge
- IN_PROGRESS - Yellow badge
- RESCHEDULED - Orange badge

## How It Works

### Step 1: HR Schedules Interview
When HR schedules an interview through the HR Dashboard:
1. HR selects a candidate and panelist
2. Sets interview date, time, position, and notes
3. Interview is saved with HR ID, candidate ID, and panelist ID

### Step 2: Candidate Views Interviews
When candidate logs in and clicks "My Interviews":
1. Frontend fetches interviews using candidate's email
2. Backend retrieves all interviews for that candidate
3. Backend enriches each interview with:
   - HR details (from User and HRProfile tables)
   - Panelist details (from Panelist table)
4. Frontend displays all information in organized cards

### Step 3: Real-time Updates
- Interview status changes are reflected immediately
- New interviews appear automatically when the tab is refreshed
- Feedback and meeting links are shown when added

## API Request/Response Example

### Request
```
GET /api/interviews/candidate/email/candidate@example.com/details
Authorization: Bearer <JWT_TOKEN>
```

### Response
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
      "notes": "Technical interview - focus on Java and Spring Boot",
      "feedback": null,
      "hrId": 2,
      "hrName": "John Smith",
      "hrEmail": "hr@company.com",
      "hrPhone": "+1234567890",
      "hrDesignation": "Senior HR Manager",
      "panelistId": 3,
      "panelistName": "Jane Doe",
      "panelistEmail": "panelist@company.com",
      "meetingLink": "https://meet.company.com/interview-123",
      "meetingRoomId": "room-123"
    }
  ],
  "count": 1
}
```

## UI Features

### Interview Card Layout
Each interview is displayed in a card with:
- **Header**: Position title and status badge
- **Date/Time Section**: Formatted date and time range
- **HR Section**: Complete HR contact information
- **Panelist Section**: Interviewer details
- **Notes Section**: Any special instructions or requirements
- **Meeting Link**: Clickable link to join the interview
- **Feedback Section**: Post-interview feedback (if available)

### Status Indicators
Visual status badges help candidates quickly identify interview states:
- 🔵 SCHEDULED - Upcoming interview
- 🟢 COMPLETED - Interview finished
- 🔴 CANCELLED - Interview cancelled
- 🟡 IN_PROGRESS - Currently ongoing
- 🟠 RESCHEDULED - Date/time changed

## Testing the Feature

### As HR:
1. Log in as HR user
2. Navigate to "Schedule Interview" section
3. Select a candidate and panelist
4. Set interview date, time, and details
5. Save the interview

### As Candidate:
1. Log in as the candidate
2. Click on "My Interviews" tab in the navigation
3. View all scheduled interviews with complete details
4. Check HR contact information
5. Click meeting link when interview time arrives

## Database Schema

### interviews Table
```sql
CREATE TABLE interviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hr_id BIGINT NOT NULL,
    panelist_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    candidate_name VARCHAR(255) NOT NULL,
    candidate_email VARCHAR(255) NOT NULL,
    interview_date DATE NOT NULL,
    interview_time_from TIME NOT NULL,
    interview_time_to TIME NOT NULL,
    position VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    feedback TEXT,
    meeting_link VARCHAR(500),
    meeting_room_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Security

- All API endpoints require JWT authentication
- Candidates can only view their own interviews
- Email-based filtering ensures data privacy
- Authorization header required for all requests

## Future Enhancements

Potential improvements:
1. **Calendar Integration**: Add interviews to Google Calendar/Outlook
2. **Notifications**: Email/SMS reminders before interviews
3. **Rescheduling**: Allow candidates to request reschedule
4. **Preparation Materials**: Attach documents or links for interview prep
5. **Video Integration**: Direct video call integration
6. **Interview History**: Archive of past interviews with outcomes

## Troubleshooting

### No Interviews Showing
- Verify candidate email matches the one used during interview scheduling
- Check if interviews exist in the database for that candidate
- Ensure JWT token is valid and not expired
- Check browser console for API errors

### HR Details Not Showing
- Verify HR has completed their profile in HRProfile table
- Check if HR ID in interview matches a valid user
- Ensure HR profile includes name, phone, and designation

### Meeting Link Not Working
- Verify meeting link was set during interview creation
- Check if link format is valid (includes http:// or https://)
- Ensure meeting room is active and accessible

## Conclusion

The "My Interviews" feature provides candidates with complete visibility into their interview schedule, including all necessary contact information and meeting details. The feature is fully functional and ready to use.

---
**Made with Bob** 🤖