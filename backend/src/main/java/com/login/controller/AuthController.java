package com.login.controller;

import com.login.dto.*;
import com.login.model.User;
import com.login.service.AuthService;
import com.login.service.MfaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Authentication Controller - REST API endpoints for authentication
 *
 * @author Bob
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MfaService mfaService;

    @Autowired
    private com.login.service.OtpService otpService;

    /**
     * Register new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            // Return the first error message for simplicity
            String firstError = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .findFirst()
                    .orElse("Validation failed");
            Map<String, String> response = new HashMap<>();
            response.put("error", firstError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .findFirst()
                    .orElse("Validation failed");
            Map<String, String> response = new HashMap<>();
            response.put("error", firstError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Login user with MFA
     * POST /api/auth/login/mfa
     */
    @PostMapping("/login/mfa")
    public ResponseEntity<?> loginWithMfa(@Valid @RequestBody MfaLoginRequest request) {
        try {
            AuthResponse response = authService.loginWithMfa(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Request OTP for login
     * POST /api/auth/login/request-otp
     */
    @PostMapping("/login/request-otp")
    public ResponseEntity<?> requestLoginOtp(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String firstError = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .findFirst()
                    .orElse("Validation failed");
            Map<String, String> response = new HashMap<>();
            response.put("error", firstError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        try {
            // Validate credentials first
            User user = authService.validateCredentials(request.getUsername(), request.getPassword());
            
            // Generate and send OTP (returns OTP for backend response)
            String otp = otpService.generateAndSendLoginOtp(user.getEmail(), user.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP sent to your email");
            response.put("email", maskEmail(user.getEmail()));
            response.put("otp", otp); // Include OTP in response for development/testing
            response.put("note", "OTP is included in response for testing purposes");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Verify OTP and complete login
     * POST /api/auth/login/verify-otp
     */
    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String otp = request.get("otp");

            if (username == null || password == null || otp == null) {
                throw new RuntimeException("Username, password, and OTP are required");
            }

            // Validate credentials again
            User user = authService.validateCredentials(username, password);
            
            // Verify OTP
            boolean otpValid = otpService.verifyOtp(user.getEmail(), otp, "LOGIN");
            
            if (!otpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }

            // Generate JWT token and complete login
            AuthResponse response = authService.generateAuthResponse(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Mask email for privacy (show only first 2 chars and domain)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.com";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() <= 2) {
            return localPart + "***@" + domain;
        }
        return localPart.substring(0, 2) + "***@" + domain;
    }

    /**
     * Request OTP for forgot password
     * POST /api/auth/forgot-password/request-otp
     */
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<?> requestForgotPasswordOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            
            // Verify email exists
            User user = authService.getUserByEmail(email);
            
            // Generate and send OTP (returns OTP for backend response)
            String otp = otpService.generateAndSendLoginOtp(email, user.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP sent to your email");
            response.put("email", maskEmail(email));
            response.put("otp", otp); // Include OTP in response for development/testing
            response.put("note", "OTP is included in response for testing purposes");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Verify OTP and reset password
     * POST /api/auth/forgot-password/reset
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String otp = request.get("otp");
            String newPassword = request.get("newPassword");

            if (email == null || otp == null || newPassword == null) {
                throw new RuntimeException("Email, OTP, and new password are required");
            }

            // Verify OTP
            boolean otpValid = otpService.verifyOtp(email, otp, "LOGIN");
            
            if (!otpValid) {
                throw new RuntimeException("Invalid or expired OTP");
            }

            // Reset password
            authService.resetPassword(email, newPassword);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get current user details
     * GET /api/auth/user
     */
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Change password
     * POST /api/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);
            
            // Change password
            authService.changePassword(user.getUsername(), request.getCurrentPassword(), request.getNewPassword());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Setup MFA - Generate QR code
     * POST /api/auth/mfa/setup
     */
    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);

            // Check if MFA is already enabled
            if (user.isMfaEnabled()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "MFA is already enabled for this user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Generate secret and QR code
            String secret = mfaService.generateSecret(user);
            String qrCodeUrl = mfaService.generateQrCodeUrl(user, secret);
            String qrCodeImage = mfaService.generateQrCodeImage(qrCodeUrl);

            MfaSetupResponse response = new MfaSetupResponse(
                secret,
                qrCodeUrl,
                qrCodeImage,
                "Scan the QR code with Google Authenticator and verify with a code"
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Enable MFA - Verify and activate
     * POST /api/auth/mfa/enable
     */
    @PostMapping("/mfa/enable")
    public ResponseEntity<?> enableMfa(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);

            String secret = (String) request.get("secret");
            Integer code = (Integer) request.get("code");

            if (secret == null || code == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Secret and code are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Enable MFA
            mfaService.enableMfa(user, secret, code);

            Map<String, String> response = new HashMap<>();
            response.put("message", "MFA enabled successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Disable MFA
     * POST /api/auth/mfa/disable
     */
    @PostMapping("/mfa/disable")
    public ResponseEntity<?> disableMfa(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody MfaVerifyRequest request) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);

            // Disable MFA
            mfaService.disableMfa(user, request.getCode());

            Map<String, String> response = new HashMap<>();
            response.put("message", "MFA disabled successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Check MFA status
     * GET /api/auth/mfa/status
     */
    @GetMapping("/mfa/status")
    public ResponseEntity<?> getMfaStatus(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from Bearer header
            String token = authHeader.substring(7);
            User user = authService.validateTokenAndGetUser(token);

            Map<String, Object> response = new HashMap<>();
            response.put("mfaEnabled", user.isMfaEnabled());
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Health check endpoint
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Login Microservice");
        response.put("message", "Service is running");
        return ResponseEntity.ok(response);
    }
}

// Made with Bob
