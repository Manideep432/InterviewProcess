# Password Validation Implementation Guide

## Overview
This guide documents the comprehensive password validation system implemented in the backend to ensure strong password security.

## Password Requirements

### 1. Minimum Length
- **Requirement**: Password must be at least **15 characters long**
- **Validation**: Enforced by `PasswordValidator` class
- **Error Message**: "Password must be at least 15 characters long"

### 2. Character Type Diversity
- **Requirement**: Password must contain at least **2 different character types**
- **Character Types**:
  - Lowercase letters (a-z)
  - Uppercase letters (A-Z)
  - Numbers (0-9)
  - Special characters (!@#$%^&*, etc.)
- **Validation**: Enforced by `PasswordValidator` class
- **Error Message**: "Password must contain at least 2 different character types (lowercase, uppercase, numbers, special characters)"

### 3. Password Strength Score
- **Requirement**: Password must score at least "OK" in the strength meter
- **Weak Patterns Detected**:
  - Common words like "password"
  - Sequential numbers like "12345", "123", "234", etc.
  - Common patterns like "qwerty", "abc"
  - Repeated characters (e.g., "aaaa", "1111")
  - All zeros or ones
- **Validation**: Enforced by `PasswordValidator` class
- **Error Message**: "Password is too weak. Avoid common patterns like 'password12345', repeated characters, or sequential numbers"

### 4. Password History
- **Requirement**: Password cannot be the same as one of your last **24 passwords**
- **Validation**: Enforced by `PasswordHistoryService` class
- **Storage**: Password hashes are stored in `password_history` table
- **Error Message**: "Password cannot be the same as one of your last 24 passwords"

## Implementation Components

### 1. Custom Validation Annotation
**File**: `backend/src/main/java/com/login/validation/ValidPassword.java`
- Custom annotation `@ValidPassword` for password validation
- Applied to password fields in DTOs

### 2. Password Validator
**File**: `backend/src/main/java/com/login/validation/PasswordValidator.java`
- Implements `ConstraintValidator` interface
- Validates password length, character diversity, and strength
- Detects weak patterns using regex

### 3. Password History Entity
**File**: `backend/src/main/java/com/login/model/PasswordHistory.java`
- JPA entity to store password history
- Tracks user ID, password hash, and creation timestamp
- Maintains relationship with User entity

### 4. Password History Repository
**File**: `backend/src/main/java/com/login/repository/PasswordHistoryRepository.java`
- JPA repository for password history operations
- Queries for retrieving user's password history
- Ordered by creation date (newest first)

### 5. Password History Service
**File**: `backend/src/main/java/com/login/service/PasswordHistoryService.java`
- Business logic for password history management
- Checks if password was used before (last 24 passwords)
- Automatically maintains only the last 24 passwords
- Methods:
  - `isPasswordReused(User, String)`: Check if password was used before
  - `addPasswordToHistory(User, String)`: Add new password to history
  - `getPasswordHistoryCount(User)`: Get count of password history entries
  - `clearPasswordHistory(User)`: Clear all password history (admin use)

### 6. Updated DTOs
**Files**:
- `backend/src/main/java/com/login/dto/RegisterRequest.java`: Uses `@ValidPassword`
- `backend/src/main/java/com/login/dto/ChangePasswordRequest.java`: New DTO for password changes

### 7. Updated Services
**File**: `backend/src/main/java/com/login/service/AuthService.java`
- `register()`: Validates password and adds to history
- `changePassword()`: Validates new password against history

### 8. Updated Controllers
**File**: `backend/src/main/java/com/login/controller/AuthController.java`
- New endpoint: `POST /api/auth/change-password`

## API Endpoints

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "MySecurePass123!"
}
```

**Validation**:
- Password must meet all 4 requirements
- Returns 400 Bad Request with error message if validation fails

### Change Password
```http
POST /api/auth/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "MySecurePass123!",
  "newPassword": "NewSecurePass456@"
}
```

**Validation**:
- Current password must be correct
- New password must meet all 4 requirements
- New password cannot match any of last 24 passwords
- Returns 400 Bad Request with error message if validation fails

## Example Valid Passwords

✅ **Valid Examples**:
- `MySecurePassword123!` (15+ chars, uppercase, lowercase, numbers, special)
- `HelloWorld2024@#$` (15+ chars, uppercase, lowercase, numbers, special)
- `StrongPass!2024Now` (19 chars, uppercase, lowercase, numbers, special)
- `Secure@Password99` (18 chars, uppercase, lowercase, numbers, special)

❌ **Invalid Examples**:
- `short123!` (Too short - less than 15 characters)
- `alllowercase123` (Only lowercase and numbers - needs more diversity)
- `password12345678` (Contains weak pattern "password12345")
- `MyPassword11111!` (Contains repeated characters "11111")
- `SecurePass12345!` (Contains sequential numbers "12345")
- Previous password (Matches one of last 24 passwords)

## Database Schema

### password_history Table
```sql
CREATE TABLE password_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Testing

### Test Scenarios

1. **Test Minimum Length**:
   - Try password with 14 characters → Should fail
   - Try password with 15 characters → Should pass

2. **Test Character Diversity**:
   - Try password with only lowercase → Should fail
   - Try password with lowercase + numbers → Should pass
   - Try password with 3+ character types → Should pass

3. **Test Weak Patterns**:
   - Try "password12345678" → Should fail
   - Try "MyPassword11111!" → Should fail
   - Try "SecurePass12345!" → Should fail

4. **Test Password History**:
   - Register with password A
   - Change to password B
   - Try to change back to password A → Should fail
   - Change to 24 different passwords
   - Try to change to password A again → Should pass (beyond 24 limit)

## Security Benefits

1. **Strong Passwords**: Enforces minimum length and complexity
2. **Pattern Detection**: Prevents common weak patterns
3. **Password Reuse Prevention**: Prevents using recent passwords
4. **Encrypted Storage**: All passwords stored as bcrypt hashes
5. **History Tracking**: Maintains audit trail of password changes

## Configuration

No additional configuration required. The system uses:
- Minimum length: 15 characters (hardcoded in `PasswordValidator`)
- Minimum character types: 2 (hardcoded in `PasswordValidator`)
- Password history limit: 24 (hardcoded in `PasswordHistoryService`)

To modify these values, update the constants in the respective classes.

## Troubleshooting

### Common Issues

1. **"Password must be at least 15 characters long"**
   - Solution: Increase password length to 15+ characters

2. **"Password must contain at least 2 different character types"**
   - Solution: Add uppercase, numbers, or special characters

3. **"Password is too weak"**
   - Solution: Avoid common patterns like "password", "12345", repeated characters

4. **"Password cannot be the same as one of your last 24 passwords"**
   - Solution: Choose a different password that wasn't used recently

## Future Enhancements

Potential improvements:
- Configurable password requirements via application.properties
- Password expiration policy
- Account lockout after failed attempts
- Password strength meter in frontend
- Admin dashboard for password policy management

---

**Author**: Bob  
**Last Updated**: 2026-05-13  
**Version**: 1.0