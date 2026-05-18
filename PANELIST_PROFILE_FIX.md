# Panelist Profile Creation Fix

## Problem
Panelists were unable to create or update their profiles, receiving errors when trying to save profile information.

## Root Cause
The [`PanelistService.updatePanelistProfile()`](backend/src/main/java/com/login/service/PanelistService.java:199) method expected an existing panelist record but threw an error if the profile didn't exist yet. This prevented panelists from creating their initial profile.

## Solution Implemented

### Backend Changes

#### 1. Modified PanelistService.updatePanelistProfile()
**File:** `backend/src/main/java/com/login/service/PanelistService.java`

**Changes:**
- Added logic to create a new panelist profile if it doesn't exist
- Validates that the user has PANELIST role before creating profile
- Requires specialization field (mandatory) when creating new profile
- Maintains existing update logic for profiles that already exist

**Key Code:**
```java
public PanelistProfileDTO updatePanelistProfile(Long userId, PanelistProfileDTO profileDTO) {
    Optional<Panelist> panelistOpt = panelistRepository.findByUserId(userId);
    
    Panelist panelist;
    if (panelistOpt.isEmpty()) {
        // Create new panelist profile if it doesn't exist
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || !"PANELIST".equals(userOpt.get().getRole())) {
            throw new RuntimeException("Invalid user or user is not a PANELIST");
        }
        
        panelist = new Panelist();
        panelist.setUser(userOpt.get());
        panelist.setActive(true);
        
        // Set specialization as required field
        if (profileDTO.getSpecialization() == null || profileDTO.getSpecialization().trim().isEmpty()) {
            throw new RuntimeException("Specialization is required");
        }
    } else {
        panelist = panelistOpt.get();
    }
    // ... rest of update logic
}
```

### Frontend Changes

#### 2. Enhanced PanelistProfile Component
**File:** `frontend/src/components/PanelistProfile.js`

**Changes:**
- Added client-side validation for required specialization field
- Improved error messages with emoji indicators (✅/❌)
- Added visual indicator (*) for required fields
- Better user feedback on save success/failure

**Key Features:**
```javascript
const handleSave = async () => {
    // Validate required fields
    if (!editedProfile.specialization || editedProfile.specialization.trim() === '') {
        setError('❌ Specialization is required');
        setLoading(false);
        return;
    }
    // ... save logic
}
```

## How It Works Now

### For New Panelists (No Profile Yet)
1. Panelist logs in and navigates to profile
2. System shows basic user info with empty profile fields
3. Panelist clicks "Edit Profile"
4. Fills in required field (Specialization) and optional fields
5. Clicks "Save"
6. Backend creates new panelist profile record
7. Success message displayed

### For Existing Panelists (Profile Exists)
1. Panelist logs in and navigates to profile
2. System loads existing profile data
3. Panelist clicks "Edit Profile"
4. Updates any fields
5. Clicks "Save"
6. Backend updates existing profile record
7. Success message displayed

## Required Fields
- **Specialization** (mandatory) - Must be filled when creating/updating profile

## Optional Fields
All other fields are optional and can be filled gradually:
- Contact: Phone, Location, LinkedIn, Slack Handle
- Professional: Company, Designation, Department, Employee ID, Work Type, Skills, Expertise
- Credentials: Education, Certifications
- Team: Team Name, Reporting Manager

## Testing Steps

### Test 1: New Panelist Profile Creation
1. Create a new user with PANELIST role (via HR or registration)
2. Login as that panelist
3. Navigate to Profile section
4. Click "Edit Profile"
5. Fill in Specialization (required)
6. Fill in any optional fields
7. Click "Save"
8. ✅ Profile should be created successfully

### Test 2: Existing Profile Update
1. Login as panelist with existing profile
2. Navigate to Profile section
3. Click "Edit Profile"
4. Modify any fields
5. Click "Save"
6. ✅ Profile should be updated successfully

### Test 3: Validation
1. Login as panelist
2. Navigate to Profile section
3. Click "Edit Profile"
4. Clear the Specialization field
5. Click "Save"
6. ❌ Should show error: "Specialization is required"

## API Endpoints

### Get Profile
```
GET /api/panelists/profile/{userId}
Authorization: Bearer <token>
```

### Update/Create Profile
```
PUT /api/panelists/profile/{userId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "specialization": "Full Stack Development",
  "experienceYears": 5,
  "expertise": "React, Node.js, AWS",
  "phone": "+91-9876543210",
  "location": "Bangalore, India",
  "linkedinUrl": "https://linkedin.com/in/username",
  "slackHandle": "@username",
  "designation": "Senior Developer",
  "company": "Tech Corp",
  "bio": "Passionate developer...",
  "skills": "React, Node.js, Python",
  "certifications": "AWS Certified",
  "education": "B.Tech Computer Science",
  "department": "Engineering",
  "employeeId": "EMP001",
  "workType": "Remote",
  "teamName": "Platform Team",
  "reportingManager": "John Doe"
}
```

## Error Handling

### Backend Errors
- "Invalid user or user is not a PANELIST" - User doesn't exist or wrong role
- "Specialization is required" - Missing required field
- "Panelist not found" - Only for GET requests when profile doesn't exist

### Frontend Errors
- "❌ Specialization is required" - Client-side validation
- "❌ Failed to update profile" - Server error
- "⚠️ No panelist profile found" - Profile doesn't exist (informational)

## Benefits
1. ✅ Panelists can now create their own profiles
2. ✅ No dependency on HR to create full profile
3. ✅ Better user experience with clear validation
4. ✅ Graceful handling of missing profiles
5. ✅ Maintains data integrity with required fields

## Files Modified
1. `backend/src/main/java/com/login/service/PanelistService.java`
2. `frontend/src/components/PanelistProfile.js`

---
**Status:** ✅ Fixed and Tested
**Date:** 2026-05-16
**Author:** Bob