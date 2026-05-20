# HR Profile Feature - Complete Guide

## Overview
The HR Profile feature allows HR users to view and manage their personal and professional information through a dedicated profile section in the HR Dashboard.

## Features Implemented

### Backend (Already Implemented)
✅ **HRProfile Entity** (`backend/src/main/java/com/login/model/HRProfile.java`)
- Comprehensive profile fields including personal, professional, and HR-specific information
- Automatic timestamp management (createdAt, updatedAt)
- One-to-one relationship with User entity

✅ **HRProfileDTO** (`backend/src/main/java/com/login/dto/HRProfileDTO.java`)
- Data transfer object for safe profile data transmission
- Excludes sensitive information

✅ **HRProfileRepository** (`backend/src/main/java/com/login/repository/HRProfileRepository.java`)
- Database operations for HR profiles

✅ **HRService Methods** (`backend/src/main/java/com/login/service/HRService.java`)
- `getHRProfile(Long hrId)` - Retrieves HR profile or returns empty profile
- `createOrUpdateHRProfile(Long hrId, Map<String, Object> profileData)` - Creates or updates profile
- `convertToDTO(HRProfile profile)` - Converts entity to DTO

✅ **HRController Endpoints** (`backend/src/main/java/com/login/controller/HRController.java`)
- `GET /api/hr/{hrId}/profile` - Get HR profile
- `POST /api/hr/{hrId}/profile` - Create or update HR profile

### Frontend (Just Implemented)
✅ **HR Profile Button** - Added in dashboard header
✅ **Profile Tab UI** - Complete form with all profile fields
✅ **Profile State Management** - React state for profile data
✅ **API Integration** - Fetch and save profile functions
✅ **CSS Styling** - Professional styling already exists

## Profile Fields

### Personal Information
- Full Name (required)
- Phone
- Location
- Address

### Professional Details
- Designation
- Department
- Employee ID
- Experience (Years)
- Company
- Work Type (On-site/Remote/Hybrid)
- Bio

### HR Specific Information
- HR Specialization (e.g., Recruitment, Training)
- Region (Geographic region managed)
- Team Name
- Reporting Manager

### Contact Information
- LinkedIn URL
- Slack Handle
- Emergency Contact Name
- Emergency Phone

### Skills & Education
- Skills
- Certifications
- Education

### Profile Statistics (Read-only)
- Total Candidates Managed
- Total Panelists Managed
- Account Status
- Email

## How to Use

### 1. Access HR Profile
1. Login as HR user
2. Click the **"👤 HR Profile"** button in the dashboard header
3. The profile tab will open

### 2. First Time Setup
- If no profile exists, you'll see an empty form
- Fill in at least the **Full Name** (required field)
- Fill in other fields as needed
- Click **"💾 Save Profile"**

### 3. Update Profile
- Navigate to the profile tab
- Modify any fields
- Click **"💾 Save Profile"**
- Success message will appear

### 4. View Profile Statistics
- After saving, profile statistics will display at the bottom
- Shows total candidates and panelists managed
- Shows account status and email

## API Endpoints

### Get HR Profile
```http
GET /api/hr/{hrId}/profile
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "profile": {
    "id": 1,
    "userId": 2,
    "username": "hr_user",
    "email": "hr@example.com",
    "fullName": "John Doe",
    "phone": "+1234567890",
    "designation": "Senior HR Manager",
    "department": "Human Resources",
    "experienceYears": 5,
    "company": "Tech Corp",
    "hrSpecialization": "Recruitment",
    "region": "North America",
    "totalCandidatesManaged": 25,
    "totalPanelistsManaged": 10,
    "active": true
  }
}
```

### Create/Update HR Profile
```http
POST /api/hr/{hrId}/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "John Doe",
  "phone": "+1234567890",
  "location": "New York, NY",
  "designation": "Senior HR Manager",
  "department": "Human Resources",
  "experienceYears": 5,
  "company": "Tech Corp",
  "bio": "Experienced HR professional...",
  "hrSpecialization": "Recruitment",
  "region": "North America",
  "skills": "Recruitment, Employee Relations, HRIS",
  "education": "MBA in HR Management"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Profile saved successfully",
  "profile": { ... }
}
```

## Testing Steps

### 1. Start Backend
```bash
cd backend
mvn spring-boot:run
```
Backend should be running on `http://localhost:8081`

### 2. Start Frontend
```bash
cd frontend
npm start
```
Frontend should be running on `http://localhost:3000`

### 3. Test HR Profile Feature

#### Test Case 1: First Time Profile Creation
1. Login as HR user (username: `hr`, password: `hr123`)
2. Click **"👤 HR Profile"** button
3. Fill in the form:
   - Full Name: "John Doe" (required)
   - Phone: "+1234567890"
   - Designation: "Senior HR Manager"
   - Department: "Human Resources"
   - Experience: 5
   - HR Specialization: "Recruitment"
4. Click **"💾 Save Profile"**
5. Verify success message appears
6. Verify profile statistics show at bottom

#### Test Case 2: Update Existing Profile
1. Navigate to HR Profile tab
2. Modify some fields (e.g., change phone number)
3. Click **"💾 Save Profile"**
4. Verify success message
5. Refresh page and verify changes persist

#### Test Case 3: Cancel Without Saving
1. Navigate to HR Profile tab
2. Make some changes
3. Click **"❌ Cancel"**
4. Verify you return to Home tab
5. Navigate back to profile - changes should not be saved

#### Test Case 4: Required Field Validation
1. Navigate to HR Profile tab
2. Clear the "Full Name" field
3. Try to save
4. Verify browser validation prevents submission

## Database Schema

The `hr_profiles` table includes:
```sql
CREATE TABLE hr_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    location VARCHAR(100),
    address VARCHAR(200),
    designation VARCHAR(100),
    department VARCHAR(100),
    employee_id VARCHAR(100),
    experience_years INT,
    company VARCHAR(100),
    bio VARCHAR(1000),
    linkedin_url VARCHAR(100),
    slack_handle VARCHAR(100),
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    skills VARCHAR(500),
    certifications VARCHAR(500),
    education VARCHAR(500),
    work_type VARCHAR(50),
    team_name VARCHAR(100),
    reporting_manager VARCHAR(100),
    hr_specialization VARCHAR(100),
    region VARCHAR(100),
    total_candidates_managed INT DEFAULT 0,
    total_panelists_managed INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Troubleshooting

### Profile Not Loading
- Check browser console for errors
- Verify backend is running on port 8081
- Check authentication token is valid
- Verify HR user ID is correct

### Save Not Working
- Check network tab for API response
- Verify all required fields are filled
- Check backend logs for errors
- Ensure database connection is active

### Profile Data Not Persisting
- Check database connection
- Verify transaction is committing
- Check for database constraints violations
- Review backend logs for errors

## Security Features

✅ **Authentication Required** - JWT token validation
✅ **Role-Based Access** - Only HR users can access their profile
✅ **User Validation** - Verifies HR role before operations
✅ **Data Sanitization** - Input validation on backend
✅ **CORS Protection** - Configured for localhost only

## Future Enhancements

Potential improvements:
- Profile picture upload
- Document attachments
- Activity history
- Performance metrics
- Team management
- Calendar integration
- Notification preferences

## Summary

The HR Profile feature is now **fully implemented** with:
- ✅ Complete backend API
- ✅ Database schema and repository
- ✅ Frontend UI with form
- ✅ State management
- ✅ API integration
- ✅ Professional styling
- ✅ Error handling
- ✅ Success notifications

**You can now click the "HR Profile" button in the HR Dashboard to view and manage HR profile information!**

---

Made with ❤️ by Bob