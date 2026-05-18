# Manage Panelists Feature Guide

## Overview
The HR Dashboard now includes a comprehensive "Manage Panelists" feature that allows HR users to view, edit, and delete panelists they have created.

## Features

### 1. View All Panelists
- Display all panelists created by the logged-in HR user
- Shows panelist details in a table format:
  - Name (username)
  - Email
  - Specialization
  - Experience (years)
  - Expertise
  - Status (Active/Inactive)
  - Action buttons (Edit/Delete)

### 2. Edit Panelist
- Click the "✏️ Edit" button next to any panelist
- Opens an edit form with current panelist information
- Editable fields:
  - Specialization (required)
  - Experience Years
  - Expertise (detailed description)
- Real-time validation
- Success/error messages
- Auto-closes after successful update

### 3. Delete Panelist
- Click the "🗑️ Delete" button next to any panelist
- Confirmation dialog before deletion
- Permanently removes the panelist from the system
- Refreshes the dashboard after deletion

## How to Access

1. **Login as HR**
   - Use your HR credentials to login

2. **Navigate to Manage Panelists**
   - Click on the "👥 Manage Panelists" tab in the HR Dashboard

3. **View Panelists**
   - All panelists you've created will be displayed in a table

## Usage Instructions

### Editing a Panelist

1. Click the "✏️ Edit" button for the panelist you want to modify
2. Update the fields:
   - **Specialization**: Technical area (e.g., Java, Python, Frontend)
   - **Experience Years**: Number of years of experience
   - **Expertise**: Detailed description of skills and expertise
3. Click "✅ Update Panelist" to save changes
4. Click "❌ Cancel" to discard changes

### Deleting a Panelist

1. Click the "🗑️ Delete" button for the panelist you want to remove
2. Confirm the deletion in the popup dialog
3. The panelist will be permanently deleted
4. The dashboard will refresh automatically

## API Endpoints Used

### Fetch Panelists
```
GET /api/panelists/hr/{hrId}
Authorization: Bearer {token}
```

### Update Panelist
```
PUT /api/panelists/{panelistId}
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
  "specialization": "string",
  "experienceYears": number,
  "expertise": "string",
  "hrId": number
}
```

### Delete Panelist
```
DELETE /api/panelists/{panelistId}?hrId={hrId}
Authorization: Bearer {token}
```

## Security

- Only HR users can access this feature
- HR users can only manage panelists they have created
- All operations require valid JWT authentication
- Backend validates HR ownership before allowing modifications

## UI Components

### Table View
- Clean, responsive table layout
- Truncated expertise text with hover tooltip
- Color-coded status badges (Active/Inactive)
- Action buttons with icons

### Edit Form
- Modal-style form overlay
- Form validation
- Real-time error/success feedback
- Auto-close on successful update

### Empty State
- Friendly message when no panelists exist
- Helpful hint to create first panelist

## Error Handling

- Network errors are caught and displayed
- Invalid data shows validation errors
- Failed operations show user-friendly error messages
- Confirmation dialogs prevent accidental deletions

## Best Practices

1. **Before Editing**: Review current information carefully
2. **Specialization**: Use clear, specific technical areas
3. **Experience**: Enter accurate years of experience
4. **Expertise**: Provide detailed, relevant information
5. **Before Deleting**: Ensure the panelist is no longer needed

## Troubleshooting

### Panelists Not Loading
- Check your internet connection
- Verify you're logged in as HR
- Refresh the page
- Check browser console for errors

### Update Fails
- Ensure all required fields are filled
- Check that specialization is not empty
- Verify you have permission to edit this panelist

### Delete Fails
- Confirm you created this panelist
- Check if panelist has active interviews
- Verify your HR permissions

## Related Features

- **Add New Panelist**: Create new panelists with full profile
- **HR Dashboard**: View overall statistics
- **Manage Candidates**: Similar interface for candidate management

## Technical Notes

### State Management
- Uses React hooks for state management
- Separate state for editing vs viewing
- Real-time updates after modifications

### Data Flow
1. Component mounts → Fetch panelists
2. User clicks Edit → Load panelist data into form
3. User submits → API call → Refresh list
4. User clicks Delete → Confirm → API call → Refresh list

### Performance
- Efficient data fetching
- Minimal re-renders
- Optimistic UI updates
- Automatic refresh after changes

## Future Enhancements

- Bulk operations (edit/delete multiple)
- Advanced filtering and search
- Export panelist data
- Panelist activity history
- Profile picture upload
- More detailed panelist profiles

---

**Made with Bob** 🤖