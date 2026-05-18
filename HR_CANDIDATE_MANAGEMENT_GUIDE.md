# HR Candidate Management Feature Guide

## Overview
This guide explains the enhanced HR Dashboard features for managing candidates, including adding new candidates with automatic email OTP login credentials and editing existing candidate details.

## Features Implemented

### 1. **Add New Candidate with Email OTP**
HR can create new candidates with login credentials. When a candidate is created:
- A User account is automatically created with CANDIDATE role
- Login credentials (username/password) are set up
- An OTP is automatically generated and sent to the candidate's email
- The candidate can use this OTP to login for the first time

### 2. **Manage Candidates Tab**
A new "Manage Candidates" tab displays all candidates created by the HR with:
- Complete candidate information in a table format
- Real-time login status (Online/Offline)
- Edit functionality for each candidate
- One-by-one candidate management

### 3. **Edit Candidate Details**
HR can edit candidate information including:
- Personal Information (Name, Phone, Position, Status, Experience, Skills)
- Job Details (Current CTC, Employment Type, Location, Job Description)
- Status updates (Applied, Screening, Interview, Selected, Rejected)

## Backend Implementation

### New Endpoints

#### 1. Get HR's Candidates
```
GET /api/hr/{hrId}/my-candidates
```
Returns all candidates created by the specific HR.

**Response:**
```json
{
  "success": true,
  "candidates": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "johndoe@candidate.com",
      "phone": "1234567890",
      "position": "Java Developer",
      "status": "APPLIED",
      "experienceYears": 5,
      "skills": "Java, Spring Boot, React",
      "currentCtc": 1200000,
      "employmentType": "FULL_TIME",
      "location": "Bangalore",
      "isLoggedIn": true,
      "lastLoginAt": "2026-05-16T12:00:00"
    }
  ],
  "count": 1
}
```

#### 2. Update Candidate
```
PUT /api/hr/{hrId}/update-candidate/{candidateId}
```
Updates candidate details.

**Request Body:**
```json
{
  "name": "John Doe Updated",
  "phone": "9876543210",
  "position": "Senior Java Developer",
  "status": "INTERVIEW",
  "experienceYears": 6,
  "skills": "Java, Spring Boot, React, Microservices",
  "currentCtc": 1500000,
  "employmentType": "FULL_TIME",
  "location": "Bangalore"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Candidate updated successfully",
  "candidate": {
    "id": 1,
    "name": "John Doe Updated",
    "email": "johndoe@candidate.com",
    "phone": "9876543210",
    "position": "Senior Java Developer",
    "status": "INTERVIEW"
  }
}
```

#### 3. Create Candidate with OTP (Enhanced)
```
POST /api/hr/{hrId}/create-candidate
```
Now automatically sends OTP email to the candidate.

**Request Body:**
```json
{
  "name": "Jane Smith",
  "phone": "1234567890",
  "position": "React Developer",
  "experienceYears": 3,
  "skills": "React, JavaScript, TypeScript",
  "currentCtc": 800000,
  "hrMailId": "hr@company.com",
  "jdDetails": "Looking for React developer with 3+ years experience",
  "employmentType": "FULL_TIME",
  "location": "Mumbai",
  "username": "janesmith",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Candidate created successfully. Login credentials sent via email.",
  "candidate": {
    "id": 2,
    "name": "Jane Smith",
    "email": "janesmith@candidate.com",
    "phone": "1234567890",
    "position": "React Developer",
    "status": "APPLIED"
  },
  "user": {
    "id": 10,
    "username": "janesmith",
    "email": "janesmith@candidate.com",
    "role": "CANDIDATE"
  },
  "otpSent": true
}
```

## Frontend Implementation

### New Tab: Manage Candidates
Located in HR Dashboard, this tab shows:
- Table view of all candidates created by the HR
- Columns: Name, Email, Phone, Position, Status, Experience, Location, Login Status, Actions
- Edit button for each candidate
- Real-time online/offline status indicator

### Edit Candidate Form
When clicking "Edit" button:
- Opens an inline edit form
- Pre-fills all current candidate data
- Allows updating:
  - Personal Information
  - Job Details
  - Status
- Save/Cancel buttons
- Success/Error messages
- Auto-refreshes candidate list after successful update

### Add New Candidate Form (Enhanced)
The existing "Add New Candidate" form now:
- Automatically generates email from username
- Sends OTP to candidate's email upon creation
- Shows success message with OTP sent confirmation
- Resets form after successful creation

## Email OTP Flow

### When Candidate is Created:
1. HR fills the "Add New Candidate" form
2. Backend creates User account and Candidate profile
3. OTP Service generates a 6-digit OTP
4. Email Service sends OTP to candidate's email
5. OTP is valid for 5 minutes
6. Candidate receives email with login credentials and OTP

### Email Template:
```
Hello [Username],

Your One-Time Password (OTP) for login is:

    [123456]

This OTP is valid for 5 minutes.

If you didn't request this OTP, please ignore this email.

Best regards,
Login Microservice Team
```

### Candidate Login Process:
1. Candidate goes to login page
2. Enters username and password
3. System sends OTP to registered email
4. Candidate enters OTP
5. Successfully logged in

## Usage Instructions

### For HR:

#### Adding a New Candidate:
1. Login to HR Dashboard
2. Click "Add New Candidate" tab
3. Fill in all required fields:
   - Name, Phone, Position (required)
   - Username, Password (required for login)
   - Optional: Experience, Skills, CTC, Location, Job Description
4. Click "Create Candidate"
5. Success message shows candidate created and OTP sent
6. Candidate receives email with login OTP

#### Managing Candidates:
1. Click "Manage Candidates" tab
2. View all your created candidates in table format
3. Check online/offline status
4. Click "Edit" button on any candidate
5. Update required fields
6. Click "Update Candidate"
7. Changes are saved and list refreshes

#### Editing a Candidate:
1. In "Manage Candidates" tab, click "Edit" on desired candidate
2. Edit form opens with current data
3. Modify any fields as needed
4. Click "Update Candidate" to save
5. Click "Cancel" to discard changes
6. Success message appears on successful update

### For Candidates:

#### First Time Login:
1. Receive email from HR with username
2. Go to login page
3. Enter username and password (provided by HR)
4. System sends OTP to your email
5. Check email for 6-digit OTP
6. Enter OTP in login page
7. Successfully logged in to Candidate Dashboard

## Security Features

1. **Password Encryption**: All passwords are encrypted using BCrypt
2. **OTP Expiry**: OTPs expire after 5 minutes
3. **Rate Limiting**: Maximum 5 OTP requests per hour per email
4. **One-time Use**: Each OTP can only be used once
5. **Authorization**: Only the HR who created a candidate can edit them

## Database Schema

### Candidate Table (Enhanced):
- All existing fields remain
- Login tracking fields already present:
  - `is_logged_in`: Boolean
  - `last_login_at`: Timestamp

### EmailOtp Table:
- `id`: Primary key
- `email`: Candidate email
- `otp`: 6-digit code
- `created_at`: Timestamp
- `expires_at`: Expiry timestamp
- `is_used`: Boolean
- `used_at`: Timestamp
- `purpose`: LOGIN/REGISTRATION/etc.

## API Testing

### Test Create Candidate with OTP:
```bash
curl -X POST http://localhost:8081/api/hr/1/create-candidate \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Candidate",
    "phone": "9876543210",
    "position": "Software Engineer",
    "experienceYears": 2,
    "skills": "Java, Python",
    "currentCtc": 600000,
    "hrMailId": "hr@company.com",
    "employmentType": "FULL_TIME",
    "location": "Delhi",
    "username": "testcandidate",
    "password": "Test@1234"
  }'
```

### Test Get My Candidates:
```bash
curl -X GET http://localhost:8081/api/hr/1/my-candidates \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test Update Candidate:
```bash
curl -X PUT http://localhost:8081/api/hr/1/update-candidate/5 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "status": "INTERVIEW",
    "experienceYears": 3
  }'
```

## Troubleshooting

### OTP Not Received:
1. Check email configuration in `application.properties`
2. Verify SMTP settings are correct
3. Check spam/junk folder
4. Ensure email service is running
5. Check backend logs for email sending errors

### Cannot Edit Candidate:
1. Verify you are the HR who created the candidate
2. Check authentication token is valid
3. Ensure candidate ID is correct
4. Check backend logs for authorization errors

### Candidate Not Appearing in List:
1. Refresh the page
2. Check if you're logged in as the correct HR
3. Verify candidate was created successfully
4. Check browser console for API errors

## Future Enhancements

1. **Bulk Candidate Upload**: Import multiple candidates via CSV
2. **Email Templates**: Customizable email templates for OTP
3. **SMS OTP**: Alternative OTP delivery via SMS
4. **Candidate Deletion**: Soft delete candidates
5. **Advanced Filters**: Filter candidates by status, location, etc.
6. **Export Candidates**: Export candidate list to Excel/PDF
7. **Candidate History**: Track all changes made to candidate profile

## Support

For issues or questions:
- Check backend logs: `backend/logs/`
- Check browser console for frontend errors
- Verify email configuration
- Ensure database is running
- Check network connectivity

---

**Made with Bob** 🤖