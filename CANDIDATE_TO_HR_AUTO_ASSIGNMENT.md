# Candidate to HR Auto-Assignment Feature

## Overview
This feature automatically assigns candidates to HR accounts when they save their information. The candidate details are immediately visible in the HR Dashboard under "Total Candidates" and "Candidate Details" sections.

## How It Works

### Backend Implementation

#### 1. **CandidateService.java** - Auto HR Assignment
When a candidate saves their information through the `/api/candidates/save-info` endpoint:
- The system automatically finds the first available HR user in the database
- Assigns the candidate to that HR
- Sets the candidate status to "APPLIED"
- Stores all candidate information including:
  - Name, Email, Phone, Location
  - Uploaded files (Photo, CV, Government ID)

**Key Changes:**
```java
// Auto-assign to HR if not already assigned
if (candidate.getHr() == null) {
    // Find first available HR user
    List<User> hrUsers = userRepository.findByRole("HR");
    if (!hrUsers.isEmpty()) {
        candidate.setHr(hrUsers.get(0)); // Assign to first HR
    }
}
```

#### 2. **UserRepository.java** - New Method
Added `findByRole(String role)` method to find all users with a specific role:
```java
List<User> findByRole(String role);
```

#### 3. **Candidate Model** - Location Field
The candidate's location is now properly stored in the `location` field instead of the `skills` field.

### Frontend Implementation

#### 1. **CandidateInfo.js** - Enhanced Success Message
Updated the success message to inform candidates that their data has been sent to HR:
```javascript
✓ Saved successfully! Your details have been sent to HR and will appear in their dashboard.
```

#### 2. **HRDashboard.js** - Already Configured
The HR Dashboard already displays all candidate information including:
- **Dashboard Tab**: Shows total candidates count and comprehensive candidate details table
- **Candidate Details Tab**: Shows all candidates with full information
- Real-time updates when new candidates are added

## Features

### For Candidates
1. **Easy Information Submission**
   - Fill in personal details (Name, Email, Phone, Location)
   - Upload Photo (JPG/PNG)
   - Upload CV (PDF)
   - Upload Government ID (JPG)
   - Drag-and-drop file upload support

2. **Automatic HR Assignment**
   - No need to select HR manually
   - System automatically assigns to available HR
   - Instant confirmation message

3. **Status Tracking**
   - Initial status: "APPLIED"
   - Can be updated by HR/Panelist later

### For HR
1. **Automatic Candidate Reception**
   - New candidates automatically appear in dashboard
   - Total candidate count updates in real-time
   - All candidate details visible immediately

2. **Comprehensive Dashboard**
   - **Dashboard Tab**: Overview with statistics and candidate table
   - **Candidate Details Tab**: Detailed view of all candidates
   - **Statistics Tab**: Candidate status distribution
   - **My Info Tab**: HR personal information

3. **Candidate Information Display**
   - Candidate Name & Email
   - Phone Number
   - Location
   - Status (APPLIED, SCREENING, INTERVIEW, SELECTED, REJECTED)
   - Assigned Panelist (if any)
   - JD Details
   - Interview Date/Time
   - Joining Date
   - Old CTC & New CTC
   - Employment Type
   - Experience & Skills
   - Created Date

## API Endpoints

### Save Candidate Information
```
POST /api/candidates/save-info
Authorization: Bearer <token>
Content-Type: multipart/form-data

Parameters:
- candidateName: string (required)
- mailId: string (required)
- phoneNumber: string (required)
- location: string (required)
- photo: file (optional, JPG/PNG)
- cv: file (optional, PDF)
- gvtId: file (optional, JPG)

Response:
{
  "success": true,
  "message": "Candidate information saved successfully",
  "data": {
    "candidateId": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "uploadedFiles": {
      "photo": "photo_uuid.png",
      "cv": "cv_uuid.pdf",
      "gvtId": "gvtId_uuid.jpg"
    }
  }
}
```

### Get HR Dashboard
```
GET /api/hr/{hrId}/dashboard
Authorization: Bearer <token>

Response:
{
  "success": true,
  "dashboard": {
    "totalCandidates": 10,
    "totalPanelists": 5,
    "totalInterviews": 8,
    "activePanelists": 4,
    "candidates": [...],
    "panelists": [...],
    "dashboardRecords": [...]
  }
}
```

### Get Candidates by HR
```
GET /api/candidates/hr/{hrId}
Authorization: Bearer <token>

Response:
{
  "success": true,
  "candidates": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "phone": "1234567890",
      "location": "New York",
      "status": "APPLIED",
      "assignedPanelist": null,
      ...
    }
  ]
}
```

## Database Schema

### Candidate Table
```sql
CREATE TABLE candidates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    location VARCHAR(100),
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
    employment_type VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (hr_id) REFERENCES users(id),
    FOREIGN KEY (assigned_panelist_id) REFERENCES users(id)
);
```

## Testing the Feature

### Step 1: Login as Candidate
1. Register a new user with role "CANDIDATE"
2. Login with candidate credentials
3. Navigate to "Candidate Info" tab

### Step 2: Fill Candidate Information
1. Enter your name
2. Enter your email
3. Enter your phone number
4. Enter your location
5. Upload photo (optional)
6. Upload CV (optional)
7. Upload Government ID (optional)
8. Click "Save"

### Step 3: Verify Success
- You should see: "✓ Saved successfully! Your details have been sent to HR and will appear in their dashboard."
- The message will display for 5 seconds

### Step 4: Login as HR
1. Logout from candidate account
2. Login with HR credentials
3. Navigate to HR Dashboard

### Step 5: Verify in HR Dashboard
1. Check "Total Candidates" count - should be incremented
2. Go to "Dashboard" tab - see candidate in the table
3. Go to "Candidate Details" tab - see full candidate information
4. Verify all fields are populated correctly

## File Upload Storage

Uploaded files are stored in:
```
backend/uploads/candidates/{userId}/
├── photo_uuid.png
├── cv_uuid.pdf
└── gvtId_uuid.jpg
```

## Status Flow

```
APPLIED → SCREENING → INTERVIEW → SELECTED/REJECTED
```

1. **APPLIED**: Initial status when candidate saves information
2. **SCREENING**: Set when panelist is assigned
3. **INTERVIEW**: Set when interview is scheduled
4. **SELECTED**: Candidate is selected for the position
5. **REJECTED**: Candidate is not selected

## Security

- All endpoints require JWT authentication
- Candidates can only save their own information
- HR can only view candidates assigned to them
- File uploads are validated for type and size
- Uploaded files are stored securely with unique names

## Error Handling

### Common Errors
1. **No HR Available**: If no HR users exist in the system
   - Solution: Create at least one HR user first

2. **Duplicate Email**: If candidate email already exists
   - Solution: Use a different email or update existing record

3. **File Upload Failed**: If file upload fails
   - Solution: Check file size and format, ensure upload directory exists

4. **Authentication Failed**: If token is invalid or expired
   - Solution: Login again to get a new token

## Future Enhancements

1. **Smart HR Assignment**
   - Load balancing: Assign to HR with least candidates
   - Department-based assignment
   - Location-based assignment

2. **Candidate Notifications**
   - Email notification when assigned to HR
   - SMS notification for interview scheduling

3. **HR Preferences**
   - HR can set preferences for candidate types
   - Automatic filtering based on skills/experience

4. **Bulk Operations**
   - Import multiple candidates from CSV
   - Export candidate data to Excel

## Troubleshooting

### Candidate not appearing in HR Dashboard
1. Check if HR user exists in database
2. Verify candidate was saved successfully
3. Refresh HR Dashboard
4. Check browser console for errors
5. Verify backend is running on port 8081

### File upload not working
1. Check file size (should be < 10MB)
2. Verify file format (JPG/PNG for images, PDF for CV)
3. Check backend logs for errors
4. Ensure uploads directory exists and has write permissions

### HR Dashboard not loading
1. Verify HR user ID is correct
2. Check authentication token is valid
3. Ensure backend API is accessible
4. Check browser console for network errors

## Support

For issues or questions:
1. Check the logs in `backend/logs/`
2. Review browser console for frontend errors
3. Verify database connections
4. Ensure all services are running

---

**Made with Bob** 🤖