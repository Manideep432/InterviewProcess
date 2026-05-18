# HR Dashboard Feature Guide

## Overview
The HR Dashboard provides a comprehensive view of all candidates, panelists, and interviews managed by an HR user. It displays detailed information including candidate details, interview schedules, CTC information, and employment details.

## Features Implemented

### 1. Enhanced Candidate Model
Added the following fields to the Candidate entity:
- **JD Details** (Job Description): Text field up to 2000 characters
- **Joining Date**: Date when the candidate is expected to join
- **Old CTC**: Previous Cost to Company (salary) in decimal format
- **New CTC**: Offered Cost to Company (salary) in decimal format
- **Employment Type**: Type of employment (FULL_TIME, PART_TIME, CONTRACT, INTERN)
- **Location**: Work location for the candidate

### 2. HR Dashboard Backend

#### HRService Updates
- Enhanced `getHRDashboard()` method to include:
  - All candidates managed by the HR
  - All panelists assigned to the HR
  - All interviews scheduled by panelists under the HR
  - Comprehensive dashboard records with all required fields
  - Statistics and status distributions

#### Dashboard Data Structure
The dashboard returns:
```json
{
  "success": true,
  "dashboard": {
    "totalCandidates": 10,
    "totalPanelists": 5,
    "totalInterviews": 8,
    "activePanelists": 4,
    "candidatesByStatus": {
      "APPLIED": 3,
      "SCREENING": 2,
      "INTERVIEW": 2,
      "SELECTED": 2,
      "REJECTED": 1
    },
    "dashboardRecords": [
      {
        "candidateId": 1,
        "candidateName": "John Doe",
        "candidateEmail": "john@example.com",
        "panelistName": "Jane Smith",
        "panelistEmail": "jane@example.com",
        "jdDetails": "Full Stack Developer position...",
        "interviewDate": "2026-05-20",
        "interviewTime": "10:00",
        "interviewStatus": "SCHEDULED",
        "status": "INTERVIEW",
        "joiningDate": "2026-06-01",
        "oldCtc": 800000,
        "newCtc": 1200000,
        "employmentType": "FULL_TIME",
        "location": "Bangalore"
      }
    ]
  }
}
```

### 3. HR Dashboard Frontend

#### Components Created
- **HRDashboard.js**: Main dashboard component
- **HRDashboard.css**: Comprehensive styling

#### Dashboard Sections

##### a) Statistics Cards
Displays quick overview:
- Total Candidates
- Total Panelists
- Total Interviews
- Active Panelists

##### b) Candidate Details Table
Comprehensive table showing:
1. **Candidate Name** - Name and email
2. **Panelist Name** - Assigned panelist details
3. **JD Details** - Job description (truncated with tooltip)
4. **Interview Date/Time** - Scheduled interview information
5. **Status** - Current candidate status with color coding
6. **Joining Date** - Expected joining date
7. **Old CTC** - Previous salary in INR
8. **New CTC** - Offered salary in INR
9. **Employment Type** - Type of employment
10. **Location** - Work location

##### c) Statistics Tab
Shows detailed statistics:
- Candidate status distribution
- Panelist information
- Active/Inactive counts

##### d) My Info Tab
Displays HR user information:
- Username
- Email
- Role
- User ID

## How to Use

### 1. Register as HR
```
1. Go to registration page
2. Fill in details:
   - Username: your_username
   - Email: your_email@example.com
   - Password: Strong@Password123
   - Role: Select "HR"
3. Click Register
```

### 2. Login as HR
```
1. Enter your credentials
2. Complete MFA if enabled
3. You'll be automatically redirected to HR Dashboard
```

### 3. View Dashboard
After login, you'll see:
- Statistics cards at the top
- Comprehensive candidate table with all details
- Navigation tabs for different views

### 4. Understanding the Data

#### Status Color Coding
- **APPLIED** - Blue (New applications)
- **SCREENING** - Orange (Under review)
- **INTERVIEW** - Purple (Interview scheduled)
- **SELECTED** - Green (Candidate selected)
- **REJECTED** - Red (Candidate rejected)

#### Interview Status
- **SCHEDULED** - Interview is scheduled
- **COMPLETED** - Interview completed
- **CANCELLED** - Interview cancelled
- **RESCHEDULED** - Interview rescheduled
- **Not Scheduled** - No interview scheduled yet

#### CTC Display
- Displayed in Indian Rupee format (₹)
- Shows "N/A" if not provided
- Example: ₹12,00,000

## API Endpoints

### Get HR Dashboard
```
GET /api/hr/{hrId}/dashboard
Authorization: Bearer <token>

Response:
{
  "success": true,
  "dashboard": { ... }
}
```

### Get HR Panelists
```
GET /api/hr/{hrId}/panelists
Authorization: Bearer <token>
```

### Get HR Candidates
```
GET /api/hr/{hrId}/candidates
Authorization: Bearer <token>
```

### Get Candidates by Status
```
GET /api/hr/{hrId}/candidates/status/{status}
Authorization: Bearer <token>
```

### Assign Panelist to Candidate
```
POST /api/hr/{hrId}/assign-panelist
Authorization: Bearer <token>
Content-Type: application/json

{
  "candidateId": 1,
  "panelistUserId": 2
}
```

## Database Schema Updates

### Candidate Table - New Columns
```sql
ALTER TABLE candidates ADD COLUMN jd_details VARCHAR(2000);
ALTER TABLE candidates ADD COLUMN joining_date DATE;
ALTER TABLE candidates ADD COLUMN old_ctc DECIMAL(10,2);
ALTER TABLE candidates ADD COLUMN new_ctc DECIMAL(10,2);
ALTER TABLE candidates ADD COLUMN employment_type VARCHAR(50);
ALTER TABLE candidates ADD COLUMN location VARCHAR(100);
```

## Testing the Feature

### 1. Create Test Data
```sql
-- Create HR user
INSERT INTO users (username, email, password, role, is_active, created_at, updated_at)
VALUES ('hr_test', 'hr@test.com', '$2a$10$...', 'HR', true, NOW(), NOW());

-- Create Panelist
INSERT INTO users (username, email, password, role, is_active, created_at, updated_at)
VALUES ('panelist_test', 'panelist@test.com', '$2a$10$...', 'PANELIST', true, NOW(), NOW());

-- Create Candidate with all fields
INSERT INTO candidates (name, email, phone, position, status, experience_years, skills, 
                       hr_id, assigned_panelist_id, jd_details, joining_date, 
                       old_ctc, new_ctc, employment_type, location, created_at, updated_at)
VALUES ('John Doe', 'john@example.com', '9876543210', 'Full Stack Developer', 'INTERVIEW',
        5, 'Java, React, Spring Boot', 1, 2, 
        'Looking for experienced Full Stack Developer with 5+ years experience in Java and React',
        '2026-06-01', 800000.00, 1200000.00, 'FULL_TIME', 'Bangalore', NOW(), NOW());
```

### 2. Test Login Flow
1. Register as HR
2. Login with HR credentials
3. Verify redirect to HR Dashboard
4. Check all data is displayed correctly

### 3. Verify Dashboard Features
- [ ] Statistics cards show correct counts
- [ ] Candidate table displays all 10 required fields
- [ ] Status badges have correct colors
- [ ] CTC values formatted correctly
- [ ] Interview dates/times display properly
- [ ] Panelist information shows correctly
- [ ] Navigation tabs work properly

## Troubleshooting

### Issue: Dashboard shows "No candidates found"
**Solution**: 
- Ensure candidates are assigned to the logged-in HR user
- Check database: `SELECT * FROM candidates WHERE hr_id = <your_hr_id>;`

### Issue: Interview data not showing
**Solution**:
- Verify interviews are created by panelists under this HR
- Check candidate email matches interview candidate_email

### Issue: CTC not displaying
**Solution**:
- Ensure old_ctc and new_ctc columns exist in database
- Run migration script if needed
- Check values are not NULL

### Issue: "Invalid HR ID" error
**Solution**:
- Verify user role is exactly "HR" (case-sensitive)
- Check user ID in token matches the API call

## Security Considerations

1. **Authorization**: All endpoints require valid JWT token
2. **Role Verification**: Backend verifies user has HR role
3. **Data Isolation**: HR can only see their own candidates and panelists
4. **CORS**: Configured for localhost:3000 (update for production)

## Future Enhancements

Potential improvements:
1. Export dashboard data to Excel/PDF
2. Filter and search functionality
3. Bulk operations (assign multiple panelists)
4. Email notifications for status changes
5. Analytics and reporting charts
6. Candidate comparison feature
7. Interview scheduling from dashboard
8. Document upload for JD and resumes

## Made with Bob
This feature was implemented to provide HR users with a comprehensive view of their recruitment pipeline, including all essential candidate information, interview schedules, and employment details.