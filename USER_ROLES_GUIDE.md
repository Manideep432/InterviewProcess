# User Roles Implementation Guide

## Overview
The login system now supports two types of users with role-based access:
- **HR**: Human Resources personnel
- **CANDIDATE**: Job applicants/candidates

## Backend Implementation

### 1. Database Schema
The `User` entity includes a `role` field:
```java
@NotBlank(message = "Role is required")
@Column(name = "role", nullable = false, length = 20)
private String role; // HR or CANDIDATE
```

### 2. Registration Endpoint
**Endpoint**: `POST /api/auth/register`

**Request Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "role": "CANDIDATE"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CANDIDATE"
}
```

### 3. Role Validation
The backend validates that the role must be either "HR" or "CANDIDATE":
```java
if (!role.equals("HR") && !role.equals("CANDIDATE")) {
    throw new RuntimeException("Invalid role. Must be HR or CANDIDATE");
}
```

### 4. Login Response
All login methods (standard login, OTP login) now return the user's role:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "CANDIDATE"
}
```

## Frontend Implementation

### 1. Registration Form
The registration form includes a role selection dropdown:
```jsx
<select
  name="role"
  value={formData.role}
  onChange={handleChange}
  required
>
  <option value="CANDIDATE">Candidate</option>
  <option value="HR">HR</option>
</select>
```

### 2. AuthService Updates
The `authService.register()` method now accepts a role parameter:
```javascript
async register(username, email, password, role) {
  const response = await axios.post(`${API_URL}/register`, {
    username,
    email,
    password,
    role
  });
  // Stores role in localStorage
}
```

### 3. User Data Storage
User data including role is stored in localStorage:
```javascript
localStorage.setItem('user', JSON.stringify({
  username: response.data.username,
  email: response.data.email,
  role: response.data.role
}));
```

### 4. Dashboard Display
The dashboard displays the user's role:
```jsx
<div className="info-item">
  <span className="info-label">👔 Role:</span>
  <span className="info-value">{user.role || 'CANDIDATE'}</span>
</div>
```

## Usage Examples

### Register as Candidate
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "candidate1",
    "email": "candidate@example.com",
    "password": "SecurePass123!",
    "role": "CANDIDATE"
  }'
```

### Register as HR
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "hr_manager",
    "email": "hr@example.com",
    "password": "SecurePass123!",
    "role": "HR"
  }'
```

### Login (Returns Role)
```bash
curl -X POST http://localhost:8081/api/auth/login/request-otp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "candidate1",
    "password": "SecurePass123!"
  }'
```

## Role-Based Features (Future Enhancements)

### Potential HR-Specific Features
- View all candidates
- Post job openings
- Schedule interviews
- Access candidate profiles
- Send bulk emails

### Potential Candidate-Specific Features
- Apply for jobs
- Upload resume
- Track application status
- View interview schedules
- Update profile

## Security Considerations

1. **Role Validation**: Backend validates role on registration
2. **Role Immutability**: Users cannot change their role after registration
3. **JWT Token**: Role can be included in JWT claims for authorization
4. **Frontend Guards**: Can implement route guards based on user role

## Database Schema

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Testing

### Test Registration with Different Roles
1. Register as CANDIDATE
2. Register as HR
3. Try registering with invalid role (should fail)
4. Login and verify role is returned
5. Check dashboard displays correct role

### Verify Role Persistence
1. Register a user with a role
2. Logout
3. Login again
4. Verify role is still correct

## Configuration

### Backend (application.properties)
```properties
server.port=8081
```

### Frontend (.env)
```properties
REACT_APP_API_URL=http://localhost:8081/api
```

## Troubleshooting

### Role Not Showing in Dashboard
- Check browser localStorage for user data
- Verify backend is returning role in login response
- Check console for any JavaScript errors

### Invalid Role Error
- Ensure role is exactly "HR" or "CANDIDATE" (case-sensitive)
- Check registration form is sending correct role value

### Role Not Persisting
- Verify database schema includes role column
- Check JPA entity has role field with proper annotations
- Ensure role is being saved in AuthService.register()

## API Endpoints Summary

| Endpoint | Method | Description | Role Required |
|----------|--------|-------------|---------------|
| `/api/auth/register` | POST | Register new user with role | None |
| `/api/auth/login/request-otp` | POST | Request OTP (returns role) | None |
| `/api/auth/login/verify-otp` | POST | Verify OTP (returns role) | None |

## Next Steps

1. **Implement Role-Based Authorization**: Add Spring Security role checks
2. **Create Role-Specific Endpoints**: Different APIs for HR and CANDIDATE
3. **Add Role-Based UI**: Different dashboard views for different roles
4. **Implement Permissions**: Fine-grained access control within roles

---

**Made with Bob** 🤖