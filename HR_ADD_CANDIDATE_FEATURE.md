# HR Add New Candidate Feature

## Overview
This feature allows HR users to create new candidate profiles directly from the HR Dashboard, including automatic creation of login credentials for the candidate.

## Features

### 1. Add New Candidate Tab
- New tab in HR Dashboard: "➕ Add New Candidate"
- Comprehensive form with all required candidate information
- Automatic login credential creation
- Real-time form validation
- Success/Error feedback messages

### 2. Form Fields

#### Personal Information
- **Name*** (Required): Candidate's full name
- **Phone*** (Required): Contact phone number
- **Position*** (Required): Job position (e.g., "Java Developer")
- **Experience**: Years of experience (e.g., 5)
- **Skills**: Comma-separated skills (e.g., "Java, Spring Boot, React")

#### Job Details
- **Current CTC**: Current salary in rupees (e.g., 1200000)
- **Employment Type*** (Required): 
  - Full Time
  - Part Time
  - Contract
  - Intern
- **Location**: Work location (e.g., "Bangalore")
- **HR Email*** (Required): HR's email address (pre-filled)
- **Job Description**: Detailed job description

#### Login Credentials
- **Username*** (Required): Login username (min 3 characters)
- **Password*** (Required): Login password (min 8 characters)

*Required fields are marked with asterisk

## How It Works

### Frontend Flow
1. HR navigates to "Add New Candidate" tab
2. Fills in the candidate information form
3. Creates username and password for candidate login
4. Submits the form
5. Receives success confirmation with username
6. Form automatically resets for next entry

### Backend Flow
1. Validates HR authentication and role
2. Checks for duplicate username/email
3. Creates User account with CANDIDATE role
4. Encrypts password using BCrypt
5. Adds password to history for security
6. Creates Candidate profile linked to User
7. Associates candidate with the HR
8. Returns success response with created data

## API Endpoint

### Create Candidate
**POST** `/api/hr/{hrId}/create-candidate`

**Headers:**
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Doe",
  "phone": "9876543210",
  "position": "Java Developer",
  "experienceYears": 5,
  "skills": "Java, Spring Boot, React",
  "currentCtc": 1200000,
  "hrMailId": "admin@example.com",
  "jdDetails": "Senior Java Developer position...",
  "employmentType": "FULL_TIME",
  "location": "Bangalore",
  "username": "johndoe",
  "password": "SecurePass123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Candidate created successfully",
  "candidate": {
    "id": 1,
    "name": "John Doe",
    "email": "johndoe@candidate.com",
    "phone": "9876543210",
    "position": "Java Developer",
    "status": "APPLIED"
  },
  "user": {
    "id": 10,
    "username": "johndoe",
    "email": "johndoe@candidate.com",
    "role": "CANDIDATE"
  }
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Username already exists"
}
```

## Database Changes

### User Table
- New user record created with:
  - username: provided by HR
  - email: auto-generated as `{username}@candidate.com`
  - password: encrypted with BCrypt
  - role: "CANDIDATE"
  - isActive: true

### Candidate Table
- New candidate record created with:
  - All form fields
  - status: "APPLIED"
  - hr_id: linked to creating HR
  - email: same as user email

### Password History Table
- Password hash stored for security validation

## Security Features

1. **Password Encryption**: All passwords encrypted using BCrypt
2. **Password History**: Prevents password reuse (last 24 passwords)
3. **JWT Authentication**: Requires valid HR token
4. **Role Validation**: Only HR users can create candidates
5. **Duplicate Prevention**: Checks for existing username/email
6. **Input Validation**: Server-side validation of all fields

## Usage Instructions

### For HR Users

1. **Login to HR Dashboard**
   - Use your HR credentials
   - Navigate to HR Dashboard

2. **Access Add Candidate Tab**
   - Click on "➕ Add New Candidate" tab
   - Form will appear with HR email pre-filled

3. **Fill Candidate Information**
   - Enter all required fields (marked with *)
   - Provide optional fields as needed
   - Create unique username and strong password

4. **Submit Form**
   - Click "✅ Create Candidate" button
   - Wait for success confirmation
   - Note the username for candidate

5. **Share Credentials**
   - Provide username and password to candidate
   - Candidate can now login with these credentials

6. **Reset Form (Optional)**
   - Click "🔄 Reset Form" to clear all fields
   - Add another candidate if needed

### For Candidates

1. **Receive Credentials**
   - Get username and password from HR
   - Email will be auto-generated as `{username}@candidate.com`

2. **Login to System**
   - Go to login page
   - Enter username and password
   - Select role: CANDIDATE
   - Click Login

3. **Access Dashboard**
   - View your candidate dashboard
   - Update profile information
   - Track interview status

## Validation Rules

### Frontend Validation
- Name: Required, non-empty
- Phone: Required, non-empty
- Position: Required, non-empty
- Username: Required, min 3 characters
- Password: Required, min 8 characters
- Experience: Optional, must be number
- Current CTC: Optional, must be number

### Backend Validation
- All required fields must be present
- Username must be unique (case-insensitive)
- Email must be unique
- Password must be at least 8 characters
- HR must have valid HR role
- Employment type must be valid enum value

## Error Handling

### Common Errors
1. **"Username already exists"**
   - Solution: Choose a different username

2. **"Email already exists"**
   - Solution: Username generates email, try different username

3. **"Password must be at least 8 characters long"**
   - Solution: Use longer password

4. **"Invalid HR ID"**
   - Solution: Re-login to refresh session

5. **"Please fill in all required fields"**
   - Solution: Complete all fields marked with *

## Testing

### Manual Testing Steps

1. **Test Successful Creation**
   ```
   - Login as HR
   - Navigate to Add Candidate tab
   - Fill all required fields
   - Submit form
   - Verify success message
   - Check candidate appears in Candidate Details tab
   ```

2. **Test Duplicate Username**
   ```
   - Create candidate with username "test123"
   - Try creating another with same username
   - Verify error message appears
   ```

3. **Test Password Validation**
   ```
   - Enter password with less than 8 characters
   - Submit form
   - Verify validation error
   ```

4. **Test Candidate Login**
   ```
   - Create candidate via HR dashboard
   - Logout from HR account
   - Login with candidate credentials
   - Verify candidate dashboard loads
   ```

5. **Test Form Reset**
   ```
   - Fill form with data
   - Click Reset Form button
   - Verify all fields are cleared
   - Verify HR email remains pre-filled
   ```

## Files Modified

### Frontend
- `frontend/src/components/HRDashboard.js` - Added new tab and form
- `frontend/src/components/HRDashboard.css` - Added form styles

### Backend
- `backend/src/main/java/com/login/controller/HRController.java` - Added endpoint
- `backend/src/main/java/com/login/service/HRService.java` - Added creation logic

## Benefits

1. **Streamlined Process**: HR can create candidates without manual registration
2. **Centralized Management**: All candidate creation through HR dashboard
3. **Automatic Linking**: Candidates automatically linked to creating HR
4. **Secure Credentials**: Passwords encrypted and validated
5. **Immediate Access**: Candidates can login immediately after creation
6. **Audit Trail**: All candidates tracked with creation timestamp

## Future Enhancements

1. **Email Notification**: Send credentials to candidate via email
2. **Bulk Upload**: Import multiple candidates from CSV/Excel
3. **Custom Email Domain**: Allow HR to specify email domain
4. **Password Generation**: Auto-generate secure passwords
5. **Profile Photo Upload**: Add candidate photo during creation
6. **Resume Upload**: Attach candidate resume
7. **Interview Scheduling**: Schedule interview during creation

## Support

For issues or questions:
1. Check error messages in the form
2. Verify all required fields are filled
3. Ensure username is unique
4. Contact system administrator if problems persist

---

**Created by:** Bob  
**Date:** 2026-05-16  
**Version:** 1.0