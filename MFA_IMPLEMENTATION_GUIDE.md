# Multi-Factor Authentication (MFA) Implementation Guide

## Overview
This guide documents the comprehensive Multi-Factor Authentication (MFA) system implemented using TOTP (Time-based One-Time Password) with Google Authenticator support.

## What is MFA?

Multi-Factor Authentication adds an extra layer of security by requiring users to provide two forms of identification:
1. **Something you know**: Password
2. **Something you have**: Mobile device with authenticator app

## Features

### ✅ TOTP-Based Authentication
- Uses industry-standard TOTP (RFC 6238)
- Compatible with Google Authenticator, Authy, Microsoft Authenticator, etc.
- 6-digit codes that refresh every 30 seconds

### ✅ QR Code Generation
- Automatic QR code generation for easy setup
- Base64-encoded images for frontend display
- Manual entry option with secret key

### ✅ Secure Storage
- MFA secrets encrypted in database
- Per-user MFA configuration
- Optional MFA (users can choose to enable/disable)

## Implementation Components

### 1. User Entity Updates
**File**: `backend/src/main/java/com/login/model/User.java`

Added fields:
```java
@Column(name = "mfa_enabled")
private boolean mfaEnabled = false;

@Column(name = "mfa_secret")
private String mfaSecret;
```

### 2. MFA Service
**File**: `backend/src/main/java/com/login/service/MfaService.java`

Key methods:
- `generateSecret(User)`: Generate TOTP secret
- `generateQrCodeUrl(User, String)`: Create QR code URL
- `generateQrCodeImage(String)`: Generate Base64 QR code image
- `verifyCode(String, int)`: Verify TOTP code
- `enableMfa(User, String, int)`: Enable MFA with verification
- `disableMfa(User, int)`: Disable MFA with verification
- `validateMfaCode(User, int)`: Validate code during login

### 3. DTOs
**Files**:
- `MfaSetupResponse.java`: Contains QR code and secret for setup
- `MfaVerifyRequest.java`: For verifying MFA codes
- `MfaLoginRequest.java`: For login with MFA code

### 4. Updated Auth Service
**File**: `backend/src/main/java/com/login/service/AuthService.java`

- `login()`: Returns "MFA_REQUIRED" error if MFA is enabled
- `loginWithMfa()`: New method for MFA-enabled login

### 5. MFA Endpoints
**File**: `backend/src/main/java/com/login/controller/AuthController.java`

New endpoints added (see API section below)

## Database Schema

### Updated users Table
```sql
ALTER TABLE users ADD COLUMN mfa_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN mfa_secret VARCHAR(255);
```

## API Endpoints

### 1. Setup MFA (Generate QR Code)
```http
POST /api/auth/mfa/setup
Authorization: Bearer <token>
```

**Response**:
```json
{
  "secret": "JBSWY3DPEHPK3PXP",
  "qrCodeUrl": "otpauth://totp/LoginMicroservice:username?secret=JBSWY3DPEHPK3PXP&issuer=LoginMicroservice",
  "qrCodeImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
  "message": "Scan the QR code with Google Authenticator and verify with a code"
}
```

### 2. Enable MFA (Verify and Activate)
```http
POST /api/auth/mfa/enable
Authorization: Bearer <token>
Content-Type: application/json

{
  "secret": "JBSWY3DPEHPK3PXP",
  "code": 123456
}
```

**Response**:
```json
{
  "message": "MFA enabled successfully"
}
```

### 3. Login with MFA
```http
POST /api/auth/login/mfa
Content-Type: application/json

{
  "username": "john_doe",
  "password": "MySecurePass123!",
  "mfaCode": 123456
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "message": "Login successful with MFA"
}
```

### 4. Disable MFA
```http
POST /api/auth/mfa/disable
Authorization: Bearer <token>
Content-Type: application/json

{
  "code": 123456
}
```

**Response**:
```json
{
  "message": "MFA disabled successfully"
}
```

### 5. Check MFA Status
```http
GET /api/auth/mfa/status
Authorization: Bearer <token>
```

**Response**:
```json
{
  "mfaEnabled": true,
  "username": "john_doe"
}
```

### 6. Regular Login (Modified)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "MySecurePass123!"
}
```

**Response (MFA Enabled)**:
```json
{
  "error": "MFA_REQUIRED"
}
```

**Response (MFA Not Enabled)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "message": "Login successful"
}
```

## User Flow

### Enabling MFA

1. **User logs in** with username and password
2. **User requests MFA setup**: `POST /api/auth/mfa/setup`
3. **Backend generates**:
   - Secret key
   - QR code URL
   - QR code image (Base64)
4. **User scans QR code** with Google Authenticator app
5. **User enters verification code** from app
6. **User confirms setup**: `POST /api/auth/mfa/enable` with secret and code
7. **MFA is enabled** for the user

### Login with MFA

1. **User enters** username and password: `POST /api/auth/login`
2. **Backend responds** with "MFA_REQUIRED" error
3. **Frontend prompts** for MFA code
4. **User enters** 6-digit code from authenticator app
5. **User submits**: `POST /api/auth/login/mfa` with credentials and code
6. **Backend validates** password and MFA code
7. **User is authenticated** and receives JWT token

### Disabling MFA

1. **User is logged in** (has valid token)
2. **User requests to disable MFA**: `POST /api/auth/mfa/disable`
3. **User provides** current MFA code for verification
4. **Backend validates** code and disables MFA
5. **MFA is disabled** for the user

## Security Considerations

### ✅ Implemented Security Features

1. **Secret Protection**
   - MFA secrets stored securely in database
   - Secrets never exposed in logs
   - Secrets only shown once during setup

2. **Code Verification**
   - Codes expire after 30 seconds
   - Time-based validation prevents replay attacks
   - Invalid codes rejected immediately

3. **Setup Verification**
   - Must verify code before enabling MFA
   - Prevents accidental lockouts
   - Ensures user has working authenticator

4. **Disable Protection**
   - Requires valid MFA code to disable
   - Prevents unauthorized MFA removal
   - Maintains security even if token is compromised

### 🔒 Best Practices

1. **Backup Codes** (Future Enhancement)
   - Generate backup codes during setup
   - Store securely for account recovery
   - One-time use only

2. **Rate Limiting** (Recommended)
   - Limit MFA verification attempts
   - Prevent brute force attacks
   - Implement exponential backoff

3. **Account Recovery** (Future Enhancement)
   - Email-based recovery option
   - Admin override capability
   - Security questions as fallback

## Compatible Authenticator Apps

- ✅ Google Authenticator (Android/iOS)
- ✅ Microsoft Authenticator (Android/iOS)
- ✅ Authy (Android/iOS/Desktop)
- ✅ 1Password (with TOTP support)
- ✅ LastPass Authenticator
- ✅ Any RFC 6238 compliant TOTP app

## Testing

### Test Scenarios

#### 1. Enable MFA
```bash
# Step 1: Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"YourPassword123!"}'

# Step 2: Setup MFA (use token from step 1)
curl -X POST http://localhost:8080/api/auth/mfa/setup \
  -H "Authorization: Bearer <token>"

# Step 3: Scan QR code with Google Authenticator

# Step 4: Enable MFA with code from app
curl -X POST http://localhost:8080/api/auth/mfa/enable \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"secret":"<secret_from_step2>","code":123456}'
```

#### 2. Login with MFA
```bash
# Step 1: Try regular login (will fail with MFA_REQUIRED)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"YourPassword123!"}'

# Step 2: Login with MFA code
curl -X POST http://localhost:8080/api/auth/login/mfa \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"YourPassword123!","mfaCode":123456}'
```

#### 3. Disable MFA
```bash
curl -X POST http://localhost:8080/api/auth/mfa/disable \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"code":123456}'
```

## Troubleshooting

### Common Issues

1. **"Invalid verification code"**
   - Ensure device time is synchronized
   - Check if code has expired (30-second window)
   - Verify correct secret was used

2. **"MFA is already enabled"**
   - User already has MFA active
   - Disable first, then re-enable if needed

3. **"MFA is not enabled for this user"**
   - User hasn't completed MFA setup
   - Complete setup process first

4. **QR Code not scanning**
   - Try manual entry with secret key
   - Ensure QR code image is clear
   - Check authenticator app permissions

## Dependencies

### Maven Dependencies (pom.xml)
```xml
<!-- TOTP (Time-based One-Time Password) for MFA -->
<dependency>
    <groupId>com.warrenstrange</groupId>
    <artifactId>googleauth</artifactId>
    <version>1.5.0</version>
</dependency>

<!-- ZXing for QR Code Generation -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.1</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.1</version>
</dependency>
```

## Future Enhancements

1. **Backup Codes**
   - Generate 10 one-time backup codes
   - Store hashed in database
   - Allow recovery if device is lost

2. **SMS/Email OTP**
   - Alternative to authenticator app
   - Fallback option for users
   - Integration with SMS gateway

3. **Remember Device**
   - Trust device for 30 days
   - Skip MFA on trusted devices
   - Revoke trust option

4. **MFA Enforcement**
   - Admin can require MFA for all users
   - Role-based MFA requirements
   - Grace period for compliance

5. **Audit Logging**
   - Log MFA setup/disable events
   - Track failed MFA attempts
   - Security monitoring dashboard

---

**Author**: Bob  
**Last Updated**: 2026-05-13  
**Version**: 1.0