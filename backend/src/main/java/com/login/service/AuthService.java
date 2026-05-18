package com.login.service;

import com.login.dto.AuthResponse;
import com.login.dto.LoginRequest;
import com.login.dto.MfaLoginRequest;
import com.login.dto.RegisterRequest;
import com.login.model.User;
import com.login.repository.UserRepository;
import com.login.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

/**
 * Authentication Service - Handles user authentication logic
 *
 * @author Bob
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Autowired
    private MfaService mfaService;

    @Autowired
    private CandidateService candidateService;

    /**
     * Register a new user with enhanced transaction management and logging
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AuthResponse register(RegisterRequest request) {
        logger.info("Starting registration process for username: {}", request.getUsername());
        
        try {
            // Check if username already exists (case-insensitive)
            if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
                logger.warn("Registration failed: Username already exists - {}", request.getUsername());
                throw new RuntimeException("Username already exists");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Registration failed: Email already exists - {}", request.getEmail());
                throw new RuntimeException("Email already exists");
            }

            // Validate role
            String role = request.getRole();
            if (role == null || (!role.equalsIgnoreCase("HR") &&
                                 !role.equalsIgnoreCase("CANDIDATE") &&
                                 !role.equalsIgnoreCase("PANELIST"))) {
                logger.warn("Registration failed: Invalid role - {}", role);
                throw new RuntimeException("Invalid role. Must be either HR, CANDIDATE, or PANELIST");
            }

            // Normalize role: HR -> HR, CANDIDATE -> CANDIDATE, PANELIST -> PANELIST
            String normalizedRole;
            if (role.equalsIgnoreCase("HR")) {
                normalizedRole = "HR";
            } else if (role.equalsIgnoreCase("CANDIDATE")) {
                normalizedRole = "CANDIDATE";
            } else {
                normalizedRole = "PANELIST";
            }

            logger.info("Creating user with role: {}", normalizedRole);

            // Encode password
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(encodedPassword);
            user.setRole(normalizedRole);
            user.setActive(true);

            logger.info("Saving user to database: {}", user.getUsername());

            // Save user and flush to database immediately
            User savedUser = userRepository.saveAndFlush(user);

            logger.info("User saved successfully with ID: {}", savedUser.getId());

            // Verify user was saved
            if (savedUser.getId() == null) {
                logger.error("User ID is null after save - database save may have failed");
                throw new RuntimeException("Failed to save user to database");
            }

            // Verify user exists in database
            boolean userExists = userRepository.existsById(savedUser.getId());
            if (!userExists) {
                logger.error("User not found in database after save - ID: {}", savedUser.getId());
                throw new RuntimeException("User verification failed after save");
            }

            logger.info("User verified in database with ID: {}", savedUser.getId());

            // Add password to history
            try {
                passwordHistoryService.addPasswordToHistory(savedUser, encodedPassword);
                logger.info("Password history added for user: {}", savedUser.getUsername());
            } catch (Exception e) {
                logger.error("Failed to add password to history: {}", e.getMessage());
                // Don't fail registration if password history fails
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(savedUser.getUsername());

            logger.info("Registration completed successfully for user: {} with ID: {}",
                       savedUser.getUsername(), savedUser.getId());

            // Return response
            return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "User registered successfully"
            );
        } catch (RuntimeException e) {
            logger.error("Registration failed for username: {} - Error: {}",
                        request.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during registration for username: {} - Error: {}",
                        request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Login user (without MFA or MFA not enabled)
     */
    public AuthResponse login(LoginRequest request) {
        // Find user by username (case-insensitive)
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Check if MFA is enabled
        if (user.isMfaEnabled()) {
            throw new RuntimeException("MFA_REQUIRED");
        }

        // Track candidate login if user is a candidate
        if ("CANDIDATE".equals(user.getRole())) {
            try {
                candidateService.trackCandidateLogin(user.getEmail());
            } catch (Exception e) {
                logger.error("Failed to track candidate login: {}", e.getMessage());
            }
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return response
        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            "Login successful"
        );
    }

    /**
     * Login user with MFA
     */
    public AuthResponse loginWithMfa(MfaLoginRequest request) {
        // Find user by username (case-insensitive)
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Check if MFA is enabled
        if (!user.isMfaEnabled()) {
            throw new RuntimeException("MFA is not enabled for this user");
        }

        // Verify MFA code
        if (!mfaService.validateMfaCode(user, request.getMfaCode())) {
            throw new RuntimeException("Invalid MFA code");
        }

        // Track candidate login if user is a candidate
        if ("CANDIDATE".equals(user.getRole())) {
            try {
                candidateService.trackCandidateLogin(user.getEmail());
            } catch (Exception e) {
                logger.error("Failed to track candidate login: {}", e.getMessage());
            }
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return response
        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            "Login successful with MFA"
        );
    }

    /**
     * Change user password with validation
     */
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Check if new password was used before (last 24 passwords)
        if (passwordHistoryService.isPasswordReused(user, newPassword)) {
            throw new RuntimeException("Password cannot be the same as one of your last 24 passwords");
        }

        // Encode new password
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Update user password
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Add to password history
        passwordHistoryService.addPasswordToHistory(user, encodedPassword);
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Validate token and get user
     */
    public User validateTokenAndGetUser(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String username = jwtUtil.extractUsername(token);
        return getUserByUsername(username);
    }

    /**
     * Validate user credentials (for OTP flow)
     */
    public User validateCredentials(String username, String password) {
        // Find user by username (case-insensitive)
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Check if user is active
        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    /**
     * Generate auth response (for OTP flow)
     */
    public AuthResponse generateAuthResponse(User user) {
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());

        // Return response
        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            "Login successful"
        );
    }

    /**
     * Get user by email for forgot password
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));
    }

    /**
     * Reset password using OTP
     */
    @Transactional
    public void resetPassword(String email, String newPassword) {
        // Find user by email
        User user = getUserByEmail(email);

        // Validate new password
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }

        // Check password history (check plain text password before encoding)
        if (passwordHistoryService.isPasswordReused(user, newPassword)) {
            throw new RuntimeException("Cannot reuse any of your last 24 passwords");
        }

        // Encode and update password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Add to password history
        passwordHistoryService.addPasswordToHistory(user, encodedPassword);
    }
}

// Made with Bob
