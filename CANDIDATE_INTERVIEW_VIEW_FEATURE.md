# Candidate Interview View Feature

## Overview
This feature allows candidates to view all their scheduled interviews with complete details including who scheduled the interview (HR information), date, time, and panelist details.

## Features Implemented

### 1. Backend Components

#### New DTO Created
- **File**: `backend/src/main/java/com/login/dto/CandidateInterviewDTO.java`
- **Purpose**: Data Transfer Object containing interview details with HR and panelist information
- **Fields**:
  - Interview details (ID, position, date, time, status, notes, feedback)
  - HR details (ID, name, email, phone, designation)
  - Panelist details (ID, name, email)
  - Meeting details (link, room ID)

#### Repository Updates
- **File**: `backend/src/main/java/com/login/repository/InterviewRepository.java`
- **New Method**: `findByCandidateId(Long candidateId)` - Fetches interviews by candidate ID

#### Service Layer Updates
- **File**: `backend/src/main/java/com/login/service/InterviewService.java`
- **New Dependencies**: Added UserRepository, HRProfileRepository, PanelistRepository
- **New Methods**:
  1. `getCandidateInterviewsWithDetails(Long candidateId)` - Get interviews by candidate ID with full details
  2. `getCandidateInterviewsByEmailWithDetails(String candidateEmail)` - Get interviews by email with full details

#### Controller Updates
- **File**: `backend/src/main/java/com/login/controller/InterviewController.java`
- **New Endpoints**:
  1. `GET /api/interviews/candidate/id/{candidateId}/details` - Fetch interviews by candidate ID
  2. `GET /api/interviews/candidate/email/{candidateEmail}/details` - Fetch interviews by candidate email

### 2. Frontend Components

#### UI Updates
- **File**: `frontend/src/components/CandidateInfo.js`
- **New Tab**: "My Interviews" tab added to candidate navigation
- **Features**:
  - Automatic interview fetching when tab is activated
  - Beautiful card-based interview display
  - Status badges with color coding
  - Detailed HR information display
  - Panelist information display
  - Meeting link (if available)
  - Interview notes and feedback display
  - Date and time formatting

#### Styling Updates
- **File**: `frontend/src/components/CandidateInfo.css`
- **New Styles Added**:
  - Interview card styling with hover effects
  - Status badge colors for different interview statuses
  - Responsive design for mobile devices
  - Section-based information display
  - Meeting link button styling

## API Endpoints

### Get Candidate Interviews by ID
```
GET /api/interviews/candidate/id/{candidateId}/details
Authorization: Bearer {token}
```

**Response**:
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
      "notes": "Technical interview",
      "hrId": 1,
      "hrName": "John Doe",
      "hrEmail": "hr@company.com",
      "hrPhone": "+1234567890",
      "hrDesignation": "Senior HR Manager",
      "panelistId": 2,
      "panelistName": "Jane Smith",
      "panelistEmail": "panelist@company.com",
      "meetingLink": "https://meet.example.com/abc123"
    }
  ],
  "count": 1
}
```

### Get Candidate Interviews by Email
```
GET /api/interviews/candidate/email/{candidateEmail}/details
Authorization: Bearer {token}
```

**Response**: Same as above

## Interview Status Types

The system supports the following interview statuses:
- **SCHEDULED** - Interview is scheduled (Blue badge)
- **IN_PROGRESS** - Interview is currently ongoing (Orange badge)
- **COMPLETED** - Interview has been completed (Green badge)
- **CANCELLED** - Interview was cancelled (Red badge)
- **RESCHEDULED** - Interview was rescheduled (Purple badge)

## User Interface

### My Interviews Tab
When a candidate clicks on the "My Interviews" tab, they will see:

1. **Loading State**: Shows "Loading interviews..." while fetching data
2. **Empty State**: Shows "No interviews scheduled yet" if no interviews exist
3. **Interview Cards**: Each interview is displayed in a card with:
   - Interview position as the header
   - Status badge with color coding
   - Date and time information
   - HR details section showing:
     - HR name
     - HR designation (if available)
     - HR email
     - HR phone (if available)
   - Panelist details section showing:
     - Panelist name
     - Panelist email
   - Notes section (if available)
   - Meeting link button (if available)
   - Feedback section (if available)

### Visual Features
- **Hover Effects**: Cards lift up slightly on hover
- **Color Coding**: Status badges use different colors for easy identification
- **Responsive Design**: Works perfectly on mobile and desktop
- **Clean Layout**: Information is organized in clear sections
- **Professional Styling**: Modern gradient backgrounds and smooth transitions

## How to Use

### For Candidates
1. Log in to your candidate account
2. Navigate to the "My Interviews" tab in the navigation bar
3. View all your scheduled interviews with complete details
4. See who scheduled your interview (HR name and contact)
5. Check the date and time of your interview
6. View panelist information
7. Click on meeting links to join virtual interviews

### For HR
When HR schedules an interview:
1. The interview is automatically linked to the HR who created it
2. HR profile information is fetched and displayed to candidates
3. Candidates can see HR name, designation, email, and phone

## Testing the Feature

### Prerequisites
1. Backend server running on `http://localhost:8081`
2. Frontend server running on `http://localhost:3000`
3. Database with:
   - Users table with HR users
   - HR_profiles table with HR profile data
   - Interviews table with scheduled interviews
   - Panelists table with panelist data

### Test Steps
1. **Create Test Data**:
   - Create an HR user and HR profile
   - Create a panelist
   - Create a candidate
   - Schedule an interview for the candidate

2. **Login as Candidate**:
   - Use candidate credentials to log in
   - Navigate to "My Interviews" tab

3. **Verify Display**:
   - Check that interviews are displayed
   - Verify HR name and details are shown
   - Verify panelist information is shown
   - Check date and time formatting
   - Verify status badge color
   - Test meeting link (if available)

4. **Test Responsive Design**:
   - Resize browser window
   - Check mobile view
   - Verify all information is accessible

## Database Schema Requirements

### Interviews Table
Must have columns:
- `id`, `hr_id`, `panelist_id`, `candidate_id`
- `candidate_name`, `candidate_email`
- `interview_date`, `interview_time_from`, `interview_time_to`
- `position`, `status`, `notes`, `feedback`
- `meeting_link`, `meeting_room_id`

### Users Table
Must have columns:
- `id`, `username`, `email`, `role`

### HR_Profiles Table
Must have columns:
- `id`, `user_id`, `full_name`, `phone`, `designation`

### Panelists Table
Must have columns:
- `id`, `full_name`, `email`

## Error Handling

The feature includes comprehensive error handling:
- **Network Errors**: Displays empty state if API call fails
- **No Data**: Shows friendly message when no interviews exist
- **Missing Data**: Gracefully handles missing HR profile or panelist data
- **Authentication**: Requires valid JWT token for API access

## Future Enhancements

Potential improvements for future versions:
1. Add calendar view for interviews
2. Add ability to request reschedule
3. Add interview preparation materials
4. Add countdown timer for upcoming interviews
5. Add notification system for interview reminders
6. Add ability to download interview details as PDF
7. Add interview history with past feedback

## Troubleshooting

### Interviews Not Showing
- Check if candidate email matches the email in interviews table
- Verify JWT token is valid
- Check browser console for API errors
- Verify backend server is running

### HR Details Not Showing
- Ensure HR profile exists for the HR user
- Check if `hr_id` in interviews table matches user ID
- Verify HR profile has `full_name` field populated

### Styling Issues
- Clear browser cache
- Check if CandidateInfo.css is loaded
- Verify no CSS conflicts with other components

## Made with Bob