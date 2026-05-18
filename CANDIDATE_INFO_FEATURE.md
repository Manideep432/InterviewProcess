# Candidate Info Feature - Implementation Guide

## Overview
This feature provides a modern, professional UI for candidates to fill in their personal information after logging in. The interface includes a light blue theme with a clean, minimal design.

## Features Implemented

### Frontend Components

#### 1. CandidateInfo Component (`frontend/src/components/CandidateInfo.js`)
- **Full-page layout** with light blue header/navbar
- **Navigation menu** with three tabs:
  - Home
  - Candidate Info
  - Feed Back
- **Role badge** showing "Role: CANDIDATE" on the left
- **Logout button** on the right
- **White background** for main content area

#### 2. Candidate Info Form
The form includes the following fields:
- **Candidate Photo** - Drag & drop upload (JPG/PNG)
  - Shows preview after upload
  - Remove button to clear photo
- **Candidate Name** - Text input
- **Mail ID** - Email input
- **Phone Number** - Tel input
- **Location** - Text input
- **CV Upload** - Drag & drop upload (PDF)
- **Upload GVT ID** - Drag & drop upload (.jpg)

#### 3. Modern UI Features
- Drag and drop file upload with visual feedback
- Photo preview functionality
- Responsive design for mobile and desktop
- Clean spacing and professional alignment
- Soft shadows and rounded corners
- Modern sans-serif fonts
- Light blue and white color theme
- Success/error status messages after save

### Backend Implementation

#### 1. New DTO (`backend/src/main/java/com/login/dto/CandidateInfoRequest.java`)
- Handles candidate information with file uploads
- Supports MultipartFile for photo, CV, and government ID

#### 2. Updated CandidateController
- **New endpoint**: `POST /api/candidates/save-info`
- Accepts multipart form data
- Uses Spring Security Authentication to get current user
- Returns success/error response with uploaded file information

#### 3. Updated CandidateService
- **New method**: `saveCandidateInfo()`
- Validates user is a CANDIDATE role
- Creates or updates candidate record
- Handles file uploads to `uploads/candidates/{userId}/` directory
- Generates unique filenames for uploaded files
- Stores file metadata

#### 4. File Upload Handling
- Files are saved to: `uploads/candidates/{userId}/`
- Unique filenames generated using UUID
- Supports:
  - Photo: JPG/PNG
  - CV: PDF
  - Government ID: JPG

### Dashboard Integration

The Dashboard component now checks the user role:
- If role is **CANDIDATE**: Shows the new CandidateInfo component
- If role is **HR** or **PANELIST**: Shows the existing dashboard

## How to Use

### For Candidates

1. **Login** with candidate credentials
2. You'll see the new candidate dashboard with:
   - Light blue header showing "Role: CANDIDATE"
   - Navigation menu: Home, Candidate Info, Feed Back
   - Logout button

3. **Navigate to "Candidate Info"** tab
4. **Fill in your details**:
   - Upload your photo (drag & drop or click)
   - Enter your name
   - Enter your email
   - Enter your phone number
   - Enter your location
   - Upload your CV (PDF)
   - Upload your government ID (JPG)

5. **Click "Save"** button
6. You'll see a success message if saved successfully

### API Endpoint

```
POST http://localhost:8080/api/candidates/save-info
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data

Form Data:
- candidateName: string
- mailId: string
- phoneNumber: string
- location: string
- photo: file (optional)
- cv: file (optional)
- gvtId: file (optional)
```

## File Structure

```
frontend/src/components/
├── CandidateInfo.js       # New candidate dashboard component
├── CandidateInfo.css      # Styling for candidate dashboard
└── Dashboard.js           # Updated to route candidates to CandidateInfo

backend/src/main/java/com/login/
├── dto/
│   └── CandidateInfoRequest.java    # New DTO for candidate info
├── controller/
│   └── CandidateController.java     # Added save-info endpoint
└── service/
    └── CandidateService.java        # Added saveCandidateInfo method
```

## Design Specifications

### Colors
- **Primary Blue**: `#4facfe` to `#00f2fe` (gradient)
- **Background**: `#ffffff` (pure white)
- **Text**: `#333` (dark gray)
- **Secondary Text**: `#666` (medium gray)
- **Borders**: `#e0e0e0` (light gray)

### Typography
- **Font Family**: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif
- **Header**: 1.8rem, font-weight 600
- **Body**: 1rem, font-weight 400
- **Labels**: 0.95rem, font-weight 600

### Spacing
- **Header Padding**: 1rem 2rem
- **Card Padding**: 2.5rem
- **Form Gap**: 1.5rem
- **Input Padding**: 0.8rem 1rem

### Components
- **Border Radius**: 8px (inputs), 12px (upload boxes), 16px (cards)
- **Box Shadow**: `0 4px 20px rgba(0, 0, 0, 0.08)`
- **Transitions**: `all 0.3s ease`

## Testing

### Manual Testing Steps

1. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start Frontend**:
   ```bash
   cd frontend
   npm start
   ```

3. **Test Login**:
   - Login with a CANDIDATE role user
   - Verify you see the new UI (not the old dashboard)

4. **Test Navigation**:
   - Click "Home" - should show welcome message
   - Click "Candidate Info" - should show the form
   - Click "Feed Back" - should show feedback textarea

5. **Test File Upload**:
   - Drag and drop a photo - should show preview
   - Click upload area - should open file picker
   - Upload CV and Government ID

6. **Test Form Submission**:
   - Fill all fields
   - Click "Save"
   - Should see success message
   - Check backend logs for file upload confirmation

7. **Test Logout**:
   - Click "Logout" button
   - Should redirect to login page

## Security Considerations

- JWT token required for all API calls
- Only users with CANDIDATE role can save their info
- Files are saved in user-specific directories
- File types are validated (photo: JPG/PNG, CV: PDF, ID: JPG)

## Future Enhancements

1. Add location field to Candidate model (currently stored in skills field)
2. Add file size validation
3. Add image compression for photos
4. Add file preview for CV and Government ID
5. Add edit functionality to update existing information
6. Add validation for phone number format
7. Add email verification
8. Add progress indicator during file upload
9. Add ability to download uploaded files
10. Add candidate profile view for HR/Panelist

## Troubleshooting

### Issue: Files not uploading
- Check if `uploads/candidates/` directory exists
- Verify file permissions
- Check file size limits in Spring Boot configuration

### Issue: UI not showing for candidate
- Verify user role is exactly "CANDIDATE" (case-sensitive)
- Check browser console for errors
- Clear browser cache and reload

### Issue: Backend compilation errors
- Run `mvn clean compile` to rebuild
- Check all imports are correct
- Verify Spring Boot version compatibility

## Notes

- The old dashboard content (welcome message, user info, tech stack) has been completely removed for CANDIDATE role
- HR and PANELIST roles still see the original dashboard
- File uploads are stored locally in the `uploads/` directory
- Consider using cloud storage (AWS S3, Azure Blob) for production

## Made with Bob