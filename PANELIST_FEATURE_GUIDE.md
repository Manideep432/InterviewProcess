# 👨‍💼 Panelist Feature Guide

## Overview

This application now supports three user roles with specific access controls:
- **HR**: Can manage both Panelists and Candidates
- **PANELIST**: Can view assigned HR information and assigned Candidates
- **CANDIDATE**: Standard user role

## Architecture

### Backend Components

#### 1. Models
- **User**: Updated to support PANELIST role
- **Panelist**: Links a User (with PANELIST role) to an HR who assigned them
- **Candidate**: Represents job candidates, can be assigned to Panelists by HR

#### 2. Repositories
- **PanelistRepository**: Data access for Panelist entities
- **CandidateRepository**: Data access for Candidate entities

#### 3. Services
- **PanelistService**: Business logic for panelist management
- **CandidateService**: Business logic for candidate management
- **HRService**: Aggregated service for HR operations (access to both Panelists and Candidates)

#### 4. Controllers
- **PanelistController**: REST API endpoints for panelist operations
- **CandidateController**: REST API endpoints for candidate operations
- **HRController**: REST API endpoints for HR dashboard and operations

### Frontend Components

#### 1. Updated Components
- **Register.js**: Now includes PANELIST role option
- **Dashboard.js**: Role-based dashboard with different tabs for each role

## Access Control Rules

### HR Role
✅ Can access:
- All Panelists assigned by them
- All Candidates managed by them
- Can assign Panelists to Candidates
- Can create, update, and delete both Panelists and Candidates

### PANELIST Role
✅ Can access:
- Their assigned HR information (HR name who assigned them)
- Candidates assigned to them by their HR
- Can update status of assigned Candidates

❌ Cannot access:
- Other Panelists
- Candidates not assigned to them
- Candidates from other HRs

### CANDIDATE Role
✅ Can access:
- Their own profile information
- Application status

## API Endpoints

### HR Endpoints
```
GET    /api/hr/{hrId}/dashboard          - Get HR dashboard with overview
GET    /api/hr/{hrId}/panelists           - Get all panelists for HR
GET    /api/hr/{hrId}/candidates          - Get all candidates for HR
GET    /api/hr/{hrId}/candidates/status/{status} - Get candidates by status
POST   /api/hr/{hrId}/assign-panelist     - Assign panelist to candidate
GET    /api/hr/{hrId}/statistics          - Get HR statistics
```

### Panelist Endpoints
```
POST   /api/panelists/create              - Create new panelist (by HR)
GET    /api/panelists/all                 - Get all panelists
GET    /api/panelists/{id}                - Get panelist by ID
GET    /api/panelists/user/{userId}       - Get panelist by user ID
GET    /api/panelists/hr/{hrId}           - Get panelists by HR
GET    /api/panelists/hr/{hrId}/active    - Get active panelists by HR
GET    /api/panelists/active              - Get all active panelists
PUT    /api/panelists/{id}                - Update panelist
PUT    /api/panelists/{id}/toggle-status  - Toggle panelist active status
DELETE /api/panelists/{id}                - Delete panelist
GET    /api/panelists/user/{userId}/hr-name - Get assigned HR name
```

### Candidate Endpoints
```
POST   /api/candidates/create             - Create new candidate
GET    /api/candidates/all                - Get all candidates
GET    /api/candidates/{id}               - Get candidate by ID
GET    /api/candidates/hr/{hrId}          - Get candidates by HR
GET    /api/candidates/panelist/{panelistId} - Get candidates by panelist
PUT    /api/candidates/{id}/assign-panelist - Assign panelist to candidate
PUT    /api/candidates/{id}/status        - Update candidate status
PUT    /api/candidates/{id}               - Update candidate details
DELETE /api/candidates/{id}               - Delete candidate
```

## Test Users

The application comes with pre-configured test users:

### HR User
- **Username**: admin
- **Password**: admin123
- **Role**: HR
- **Access**: Can manage all panelists and candidates

### Panelist Users
- **Username**: panelist1
- **Password**: panelist123
- **Role**: PANELIST
- **Assigned HR**: admin
- **Specialization**: Java Development

- **Username**: panelist2
- **Password**: panelist123
- **Role**: PANELIST
- **Assigned HR**: admin
- **Specialization**: Frontend Development

### Candidate Users
- **Username**: Manideep
- **Password**: password123
- **Role**: CANDIDATE

## Sample Data

### Candidates
1. **John Doe** - Senior Java Developer (Status: APPLIED)
2. **Jane Smith** - React Developer (Status: SCREENING, Assigned to: panelist2)
3. **Bob Johnson** - Full Stack Developer (Status: INTERVIEW, Assigned to: panelist1)

## Usage Examples

### 1. HR Login Flow
1. Login as `admin` / `admin123`
2. Dashboard shows:
   - User Info tab
   - Panelists tab (manage panelists)
   - Candidates tab (manage candidates)
   - Feedback tab

### 2. Panelist Login Flow
1. Login as `panelist1` / `panelist123`
2. Dashboard shows:
   - User Info tab
   - Assigned Candidates tab (view candidates assigned by HR)
   - HR Information tab (view assigned HR details)
   - Feedback tab

### 3. Creating a New Panelist (HR Action)
```javascript
// HR creates a panelist
POST /api/panelists/create
{
  "userId": 5,  // User ID with PANELIST role
  "hrId": 4,    // HR user ID
  "specialization": "Java Development",
  "experienceYears": 5,
  "expertise": "Spring Boot, Microservices"
}
```

### 4. Assigning Panelist to Candidate (HR Action)
```javascript
// HR assigns panelist to candidate
POST /api/hr/4/assign-panelist
{
  "candidateId": 1,
  "panelistUserId": 5
}
```

### 5. Panelist Views Assigned Candidates
```javascript
// Panelist gets their assigned candidates
GET /api/candidates/panelist/5
```

### 6. Panelist Gets HR Information
```javascript
// Panelist gets their assigned HR name
GET /api/panelists/user/5/hr-name
```

## Database Schema

### Users Table
```sql
- id (PK)
- username
- email
- password
- role (HR, PANELIST, CANDIDATE)
- created_at
- updated_at
- is_active
- mfa_enabled
- mfa_secret
```

### Panelists Table
```sql
- id (PK)
- user_id (FK -> users.id)
- assigned_hr_id (FK -> users.id)
- specialization
- experience_years
- expertise
- is_active
- created_at
- updated_at
```

### Candidates Table
```sql
- id (PK)
- name
- email
- phone
- position
- status (APPLIED, SCREENING, INTERVIEW, SELECTED, REJECTED)
- experience_years
- skills
- hr_id (FK -> users.id)
- assigned_panelist_id (FK -> users.id)
- created_at
- updated_at
```

## Security Considerations

1. **Authentication**: All endpoints require JWT authentication
2. **Authorization**: 
   - HR can only access their own panelists and candidates
   - Panelists can only access candidates assigned to them
   - Candidates can only access their own data
3. **Data Validation**: All inputs are validated on both frontend and backend
4. **Password Security**: Passwords are hashed using BCrypt

## Future Enhancements

- [ ] Full frontend implementation for HR/Panelist management UI
- [ ] Real-time notifications for candidate assignments
- [ ] Interview scheduling system
- [ ] Evaluation forms for panelists
- [ ] Reporting and analytics dashboard
- [ ] Email notifications for assignments
- [ ] File upload for candidate resumes
- [ ] Video interview integration

## Troubleshooting

### Issue: Panelist cannot see assigned candidates
**Solution**: Ensure the panelist is properly assigned by HR and the candidate has `assigned_panelist_id` set.

### Issue: HR cannot create panelist
**Solution**: Verify that the user has PANELIST role before creating a Panelist record.

### Issue: Access denied errors
**Solution**: Check that JWT token is valid and user has appropriate role for the endpoint.

## Support

For issues or questions, please refer to:
- Main README.md
- QUICK_START.md
- API documentation in controller files

---

**Made with Bob** 🤖