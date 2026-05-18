# Interview Scheduling Feature - Complete Guide

## Overview
This feature allows HR to schedule interviews for candidates directly from the HR Dashboard. When an interview is scheduled, both the candidate and the assigned panelist receive email notifications with all the interview details.

## Features Implemented

### 1. Backend Components

#### Email Service Enhancement
**File:** `backend/src/main/java/com/login/service/EmailService.java`

Added two new email notification methods:
- `sendInterviewScheduleToCandidate()` - Sends interview details to the candidate
- `sendInterviewScheduleToPanelist()` - Sends interview assignment notification to the panelist

**Email Content Includes:**
- Interview date and time
- Position being interviewed for
- Interviewer/Candidate name
- Preparation guidelines
- Contact information

#### HR Controller Endpoint
**File:** `backend/src/main/java/com/login/controller/HRController.java`

**New Endpoint:**
```
POST /api/hr/{hrId}/schedule-interview
```

**Request Body:**
```json
{
  "candidateId": 123,
  "panelistEmail": "panelist@example.com",
  "interviewDate": "2026-05-20",
  "interviewTime": "14:30",
  "notes": "Optional notes for the interview"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Interview scheduled successfully. Notifications sent to candidate and panelist.",
  "interview": {
    "id": 456,
    "candidateName": "John Doe",
    "candidateEmail": "john@example.com",
    "panelistName": "Jane Smith",
    "panelistEmail": "jane@example.com",
    "interviewDate": "2026-05-20",
    "interviewTime": "14:30:00",
    "position": "Software Engineer",
    "status": "SCHEDULED",
    "notes": "Optional notes"
  }
}
```

#### HR Service Logic
**File:** `backend/src/main/java/com/login/service/HRService.java`

**New Method:** `scheduleInterview()`

**Functionality:**
1. Validates HR authorization
2. Validates candidate existence
3. Validates panelist by email
4. Creates interview record in database
5. Assigns panelist to candidate (if not already assigned)
6. Updates candidate status to "INTERVIEW"
7. Sends email notifications to both parties
8. Returns interview details

### 2. Frontend Components

#### HR Dashboard Enhancement
**File:** `frontend/src/components/HRDashboard.js`

**New State Variables:**
```javascript
const [schedulingInterview, setSchedulingInterview] = useState(null);
const [interviewFormData, setInterviewFormData] = useState({
  panelistEmail: '',
  interviewDate: '',
  interviewTime: '',
  notes: ''
});
const [interviewFormError, setInterviewFormError] = useState('');
const [interviewFormSuccess, setInterviewFormSuccess] = useState('');
const [submittingInterview, setSubmittingInterview] = useState(false);
```

**New Functions:**
- `handleScheduleInterview()` - Opens the scheduling form
- `handleCancelScheduleInterview()` - Closes the form
- `handleInterviewInputChange()` - Handles form input changes
- `handleSubmitInterview()` - Submits the interview schedule

**UI Changes:**
1. Added "📅 Schedule" button in the Manage Candidates table
2. Added interview scheduling form modal with fields:
   - Candidate Name (read-only)
   - Candidate Email (read-only)
   - Position (read-only)
   - Panelist Email (required)
   - Interview Date (required, minimum: today)
   - Interview Time (required)
   - Notes (optional)

## How to Use

### For HR Users

1. **Navigate to Manage Candidates Tab**
   - Login as HR
   - Click on "📝 Manage Candidates" tab

2. **Schedule an Interview**
   - Find the candidate you want to schedule an interview for
   - Click the "📅 Schedule" button in the Actions column
   - Fill in the interview scheduling form:
     - Enter the panelist's email address
     - Select the interview date (must be today or later)
     - Select the interview time
     - Optionally add notes for the interview
   - Click "✅ Schedule Interview"

3. **Confirmation**
   - Success message will appear
   - Both candidate and panelist will receive email notifications
   - The form will automatically close after 3 seconds

### Email Notifications

#### Candidate Email
```
Subject: Interview Scheduled - [Position]

Dear [Candidate Name],

Your interview has been scheduled!

Interview Details:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Position: [Position]
Date: [Date]
Time: [Time]
Interviewer: [Panelist Name]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Please be prepared and join on time.
Make sure you have:
  • A stable internet connection
  • Your resume and relevant documents
  • A quiet environment for the interview

If you need to reschedule, please contact HR immediately.

Best of luck!
```

#### Panelist Email
```
Subject: New Interview Assigned - [Candidate Name]

Dear [Panelist Name],

A new interview has been assigned to you!

Interview Details:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Candidate Name: [Name]
Candidate Email: [Email]
Position: [Position]
Date: [Date]
Time: [Time]
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Please review the candidate's profile and prepare for the interview.
You can access the candidate's details from your Panelist Dashboard.

If you have any conflicts with this schedule, please contact HR immediately.
```

## Database Schema

The feature uses the existing [`Interview`](backend/src/main/java/com/login/model/Interview.java:1) entity which includes:
- `id` - Interview ID
- `hrId` - HR who scheduled the interview
- `panelistId` - Assigned panelist
- `candidateId` - Candidate being interviewed
- `candidateName` - Candidate's name
- `candidateEmail` - Candidate's email
- `interviewDate` - Date of interview
- `interviewTime` - Time of interview
- `position` - Position being interviewed for
- `notes` - Additional notes
- `status` - Interview status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED)
- `createdAt` - Creation timestamp
- `updatedAt` - Last update timestamp

## Testing the Feature

### Prerequisites
1. Backend server running on port 8081
2. Frontend server running on port 3000
3. Email service configured in `application.properties`
4. At least one HR user, one candidate, and one panelist in the system

### Test Steps

1. **Login as HR**
   ```
   Username: hr@example.com
   Password: [your HR password]
   ```

2. **Create a Candidate (if needed)**
   - Go to "➕ Add New Candidate" tab
   - Fill in candidate details
   - Submit the form

3. **Schedule Interview**
   - Go to "📝 Manage Candidates" tab
   - Click "📅 Schedule" for a candidate
   - Enter panelist email (must be a registered panelist)
   - Select date and time
   - Add optional notes
   - Click "✅ Schedule Interview"

4. **Verify Success**
   - Check for success message
   - Verify candidate email received notification
   - Verify panelist email received notification
   - Check database for interview record

### Expected Results
- ✅ Interview record created in database
- ✅ Candidate status updated to "INTERVIEW"
- ✅ Panelist assigned to candidate
- ✅ Email sent to candidate with interview details
- ✅ Email sent to panelist with interview assignment
- ✅ Success message displayed in UI

## Error Handling

The feature includes comprehensive error handling:

### Backend Errors
- Invalid HR ID
- Candidate not found
- Panelist not found with provided email
- Missing required fields
- Email sending failures (logged but doesn't fail the operation)

### Frontend Errors
- Network errors
- Validation errors
- Server errors
- Display user-friendly error messages

## API Error Responses

```json
{
  "success": false,
  "message": "Error description here"
}
```

Common error messages:
- "Candidate ID is required"
- "Panelist email is required"
- "Interview date is required"
- "Interview time is required"
- "Panelist not found with email: [email]"
- "Candidate not found"

## Configuration

### Email Configuration
Ensure your `application.properties` has email settings:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Future Enhancements

Potential improvements:
1. Interview rescheduling functionality
2. Interview cancellation with notifications
3. Calendar integration (Google Calendar, Outlook)
4. Video meeting link generation
5. Interview reminder notifications
6. Interview feedback collection
7. Bulk interview scheduling
8. Interview slot availability checking
9. Recurring interview schedules
10. Interview analytics and reporting

## Troubleshooting

### Emails Not Sending
1. Check email configuration in `application.properties`
2. Verify SMTP credentials
3. Check firewall/network settings
4. Review backend logs for email errors

### Interview Not Created
1. Verify panelist email exists in system
2. Check candidate exists
3. Verify HR has proper permissions
4. Check backend logs for errors

### UI Not Updating
1. Refresh the page
2. Check browser console for errors
3. Verify API endpoint is accessible
4. Check network tab for failed requests

## Support

For issues or questions:
1. Check backend logs: `backend/logs/`
2. Check browser console for frontend errors
3. Review this documentation
4. Contact development team

---

**Made with ❤️ by Bob**