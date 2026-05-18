# Panelist Auto-Assignment Feature

## Overview
This feature allows HR to assign panelists to candidates when creating a new candidate. The panelist's name is automatically populated when their email address is entered, making the assignment process quick and error-free.

## Features

### 1. **Auto-Complete Panelist Name**
- When HR enters a panelist's email address, the system automatically searches the database
- If the email matches a registered panelist, their name is auto-populated
- Real-time validation with visual feedback (searching indicator, error messages)

### 2. **Multiple Panelist Assignment**
- HR can assign multiple panelists to a single candidate
- Each panelist is displayed in a list with their name and email
- Easy removal of panelists from the assignment list

### 3. **User-Friendly Interface**
- Clean, intuitive UI with clear labels and instructions
- Auto-populated name field (read-only) to prevent manual errors
- Visual indicators for loading states and errors
- Responsive design that works on all screen sizes

## Implementation Details

### Backend Changes

#### 1. New API Endpoint: Search Panelist by Email
**File:** `backend/src/main/java/com/login/controller/HRController.java`

```java
@GetMapping("/{hrId}/search-panelist")
public ResponseEntity<?> searchPanelistByEmail(
    @PathVariable Long hrId,
    @RequestParam String email)
```

**Purpose:** Searches for a panelist by email and returns their details

**Response:**
```json
{
  "success": true,
  "panelist": {
    "id": 1,
    "userId": 5,
    "username": "John Doe",
    "email": "john.doe@example.com",
    "specialization": "Java Development",
    "experienceYears": 5,
    "phone": "+1234567890",
    "designation": "Senior Developer",
    "company": "Tech Corp",
    "active": true
  }
}
```

#### 2. Service Method
**File:** `backend/src/main/java/com/login/service/HRService.java`

```java
public Map<String, Object> searchPanelistByEmail(Long hrId, String email)
```

**Logic:**
1. Validates HR credentials
2. Searches for user with PANELIST role and matching email
3. Retrieves panelist profile details
4. Returns formatted panelist information

### Frontend Changes

#### 1. New State Variables
**File:** `frontend/src/components/HRDashboard.js`

```javascript
// Panelist assignment state
const [assignedPanelists, setAssignedPanelists] = useState([]);
const [panelistEmailInput, setPanelistEmailInput] = useState('');
const [panelistNameInput, setPanelistNameInput] = useState('');
const [searchingPanelist, setSearchingPanelist] = useState(false);
const [panelistSearchError, setPanelistSearchError] = useState('');
```

#### 2. Key Functions

**`handlePanelistEmailChange(e)`**
- Triggers when user types in the email field
- Automatically searches for panelist when email format is valid
- Updates the name field with auto-populated data
- Shows loading indicator during search
- Displays error if panelist not found

**`handleAddPanelist()`**
- Adds the panelist to the assignment list
- Validates that email and name are present
- Prevents duplicate assignments
- Clears input fields after adding

**`handleRemovePanelist(email)`**
- Removes a panelist from the assignment list
- Filters out the panelist by email

#### 3. UI Components

**Panelist Assignment Section:**
```jsx
<div className="form-section">
  <h3>👥 Assign Panelists (Optional)</h3>
  
  {/* Email Input with Auto-Search */}
  <input
    type="email"
    value={panelistEmailInput}
    onChange={handlePanelistEmailChange}
    placeholder="Enter panelist email"
  />
  
  {/* Auto-Populated Name Field (Read-Only) */}
  <input
    type="text"
    value={panelistNameInput}
    readOnly
    placeholder="Auto-populated from email"
  />
  
  {/* Add Button */}
  <button onClick={handleAddPanelist}>
    ➕ Add Panelist
  </button>
  
  {/* Assigned Panelists List */}
  {assignedPanelists.map(panelist => (
    <div>
      <strong>{panelist.name}</strong>
      <small>{panelist.email}</small>
      <button onClick={() => handleRemovePanelist(panelist.email)}>
        ❌ Remove
      </button>
    </div>
  ))}
</div>
```

## How to Use

### For HR Users:

1. **Navigate to Add New Candidate**
   - Go to HR Dashboard
   - Click on "Add New Candidate" tab

2. **Fill in Candidate Details**
   - Enter all required candidate information
   - Fill in personal details, job details, and login credentials

3. **Assign Panelists (Optional)**
   - Scroll to the "Assign Panelists" section
   - Enter a panelist's email address
   - Wait for the name to auto-populate (usually instant)
   - Click "Add Panelist" to add them to the list
   - Repeat for multiple panelists if needed

4. **Review and Submit**
   - Review all assigned panelists in the list
   - Remove any panelist if needed by clicking "Remove"
   - Click "Create Candidate" to submit

### Visual Feedback:

- **🔍 Searching...** - System is looking up the panelist
- **✨ Name is automatically filled** - Confirmation that auto-fill is working
- **⚠️ Panelist not found** - Email doesn't match any registered panelist
- **📋 Assigned Panelists (X)** - Shows count of assigned panelists

## API Flow

```
User enters email → Frontend validates format → API call to /search-panelist
                                                          ↓
Backend searches User table (role=PANELIST, email=input)
                                                          ↓
Backend retrieves Panelist profile details
                                                          ↓
Frontend receives panelist data → Auto-populates name field
                                                          ↓
User clicks "Add Panelist" → Added to local state array
                                                          ↓
User submits form → Panelist assignments saved with candidate
```

## Error Handling

1. **Invalid Email Format**
   - No search is triggered until email contains '@' and has minimum length

2. **Panelist Not Found**
   - Clear error message: "Panelist not found with this email"
   - Name field remains empty
   - Add button stays disabled

3. **Duplicate Assignment**
   - Error message: "This panelist is already added"
   - Prevents adding the same panelist twice

4. **Network Errors**
   - Generic error message: "Error searching panelist"
   - User can retry by re-entering the email

## Benefits

1. **Reduced Errors**: Auto-population eliminates typos in panelist names
2. **Time Saving**: No need to manually look up and type panelist names
3. **Better UX**: Instant feedback and validation
4. **Flexibility**: Support for multiple panelist assignments
5. **Data Integrity**: Only registered panelists can be assigned

## Testing Checklist

- [ ] Enter valid panelist email → Name auto-populates
- [ ] Enter invalid email → Error message shown
- [ ] Add multiple panelists → All appear in list
- [ ] Remove panelist → Removed from list
- [ ] Try to add duplicate → Error message shown
- [ ] Submit form with panelists → Data saved correctly
- [ ] Submit form without panelists → Works normally
- [ ] Test with slow network → Loading indicator appears
- [ ] Test form reset → Panelist list clears

## Future Enhancements

1. **Dropdown Suggestions**: Show list of matching panelists as user types
2. **Panelist Availability**: Show if panelist is available for interviews
3. **Bulk Assignment**: Assign same panelists to multiple candidates
4. **Email Validation**: Check if email domain matches company domain
5. **Panelist Workload**: Show current number of assigned candidates

## Technical Notes

- Email search is case-insensitive
- Search triggers only when email format is valid (contains '@' and length > 5)
- Debouncing is not implemented (searches on every keystroke after validation)
- Panelist data is fetched fresh on each search (no caching)
- Assignment list is stored in component state (not persisted until form submission)

## Files Modified

### Backend:
1. `backend/src/main/java/com/login/controller/HRController.java` - Added search endpoint
2. `backend/src/main/java/com/login/service/HRService.java` - Added search service method

### Frontend:
1. `frontend/src/components/HRDashboard.js` - Added panelist assignment UI and logic

## Support

For issues or questions about this feature, please contact the development team or refer to the main project documentation.

---

**Feature Status:** ✅ Completed and Ready for Testing
**Last Updated:** 2026-05-16
**Version:** 1.0.0