# Panelist Status Fix - Active/Inactive Display Issue

## Problem Description
When HR creates a new panelist and views their full profile in the Panelist Dashboard, the status was showing as "❌ Inactive" instead of "✅ Active", even though the panelist was created with active status.

## Root Cause
The issue was caused by a JSON serialization mismatch between the backend and frontend:

1. **Backend DTO Issue**: The `PanelistProfileDTO` had a field named `isActive` with getter `isActive()`. When Jackson serializes this to JSON, it removes the "is" prefix and creates `"active": true/false` instead of `"isActive": true/false`.

2. **Frontend Expectation**: The frontend code was expecting the property to be named `isActive`, but was receiving `active` from the backend.

## Solution Implemented

### Backend Changes

#### File: `backend/src/main/java/com/login/dto/PanelistProfileDTO.java`

**Changed:**
- Renamed the private field from `isActive` to `active`
- Updated the setter to use `this.active = active` for clarity
- Kept the getter as `isActive()` for Java bean convention compatibility

**Result:** Jackson now correctly serializes this as `"active": true/false` in JSON responses.

### Frontend Changes

#### File: `frontend/src/components/PanelistProfile.js`

**Added backward compatibility handling:**
```javascript
const profileData = {
  ...data.profile,
  isActive: data.profile.isActive !== undefined ? data.profile.isActive : 
           (data.profile.active !== undefined ? data.profile.active : true)
};
```

This ensures the frontend can handle both property names (`isActive` and `active`) and defaults to `true` if neither is present.

## How It Works Now

### When HR Creates a New Panelist:

1. **User Creation** (`/api/auth/register`):
   - Creates a User account with role "PANELIST"
   - User is set to active by default

2. **Panelist Profile Creation** (`/api/panelists/create`):
   - Creates a Panelist record linked to the User
   - `PanelistService.createPanelist()` explicitly sets `panelist.setActive(true)` (line 54)
   - The Panelist model has `isActive = true` as default (line 96)

3. **Profile Update** (`/api/panelists/profile/{userId}`):
   - Updates additional profile fields (phone, location, skills, etc.)
   - Preserves the active status

4. **Profile Display** (Panelist Dashboard → View Full Profile):
   - Fetches profile via `/api/panelists/profile/{userId}`
   - Backend returns `"active": true` in JSON
   - Frontend normalizes to `isActive: true`
   - Displays "✅ Active" status badge

## Testing

To verify the fix:

1. **Create a new panelist as HR:**
   - Go to HR Dashboard → Add New Panelist tab
   - Fill in all required fields
   - Submit the form

2. **View the panelist profile:**
   - Go to Panelist Dashboard (login as the newly created panelist)
   - Click "👤 View Full Profile"
   - Verify the status shows "✅ Active"

3. **Check the API response:**
   - Open browser DevTools → Network tab
   - Look for the `/api/panelists/profile/{userId}` request
   - Verify the response contains `"active": true`

## Files Modified

1. `backend/src/main/java/com/login/dto/PanelistProfileDTO.java`
   - Fixed field naming for proper JSON serialization

2. `frontend/src/components/PanelistProfile.js`
   - Added backward compatibility for both property names

## Related Code References

- [`Panelist.java`](backend/src/main/java/com/login/model/Panelist.java:96) - Default `isActive = true`
- [`PanelistService.java`](backend/src/main/java/com/login/service/PanelistService.java:54) - Explicit `setActive(true)` on creation
- [`PanelistService.java`](backend/src/main/java/com/login/service/PanelistService.java:302) - DTO conversion sets active status
- [`PanelistProfile.js`](frontend/src/components/PanelistProfile.js:252-254) - Status badge display

## Additional Notes

- The fix maintains backward compatibility with existing data
- No database migration is required
- The change only affects the JSON serialization layer
- All existing panelists will continue to work correctly