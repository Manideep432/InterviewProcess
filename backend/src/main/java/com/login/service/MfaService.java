package com.login.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.login.model.User;
import com.login.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Multi-Factor Authentication Service
 * Handles TOTP (Time-based One-Time Password) generation and verification
 * 
 * @author Bob
 */
@Service
public class MfaService {

    private static final String ISSUER = "LoginMicroservice";
    private static final int QR_CODE_SIZE = 200;

    @Autowired
    private UserRepository userRepository;

    private final GoogleAuthenticator googleAuthenticator;

    public MfaService() {
        this.googleAuthenticator = new GoogleAuthenticator();
    }

    /**
     * Generate MFA secret for a user
     * 
     * @param user The user
     * @return The generated secret key
     */
    public String generateSecret(User user) {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        return key.getKey();
    }

    /**
     * Generate QR code URL for Google Authenticator
     * 
     * @param user The user
     * @param secret The MFA secret
     * @return QR code URL
     */
    public String generateQrCodeUrl(User user, String secret) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(
            ISSUER,
            user.getUsername(),
            new GoogleAuthenticatorKey.Builder(secret).build()
        );
    }

    /**
     * Generate QR code image as Base64 string
     * 
     * @param qrCodeUrl The QR code URL
     * @return Base64 encoded QR code image
     */
    public String generateQrCodeImage(String qrCodeUrl) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                qrCodeUrl,
                BarcodeFormat.QR_CODE,
                QR_CODE_SIZE,
                QR_CODE_SIZE
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();
            
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Verify TOTP code
     * 
     * @param secret The MFA secret
     * @param code The TOTP code to verify
     * @return true if code is valid, false otherwise
     */
    public boolean verifyCode(String secret, int code) {
        return googleAuthenticator.authorize(secret, code);
    }

    /**
     * Enable MFA for a user
     * 
     * @param user The user
     * @param secret The MFA secret
     * @param verificationCode The verification code to confirm setup
     * @return true if MFA was enabled successfully
     */
    @Transactional
    public boolean enableMfa(User user, String secret, int verificationCode) {
        // Verify the code before enabling MFA
        if (!verifyCode(secret, verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setMfaSecret(secret);
        user.setMfaEnabled(true);
        userRepository.save(user);
        
        return true;
    }

    /**
     * Disable MFA for a user
     * 
     * @param user The user
     * @param verificationCode The verification code to confirm disable
     * @return true if MFA was disabled successfully
     */
    @Transactional
    public boolean disableMfa(User user, int verificationCode) {
        if (!user.isMfaEnabled()) {
            throw new RuntimeException("MFA is not enabled for this user");
        }

        // Verify the code before disabling MFA
        if (!verifyCode(user.getMfaSecret(), verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setMfaSecret(null);
        user.setMfaEnabled(false);
        userRepository.save(user);
        
        return true;
    }

    /**
     * Check if user has MFA enabled
     * 
     * @param user The user
     * @return true if MFA is enabled
     */
    public boolean isMfaEnabled(User user) {
        return user.isMfaEnabled() && user.getMfaSecret() != null;
    }

    /**
     * Validate MFA code for login
     * 
     * @param user The user
     * @param code The TOTP code
     * @return true if code is valid
     */
    public boolean validateMfaCode(User user, int code) {
        if (!isMfaEnabled(user)) {
            throw new RuntimeException("MFA is not enabled for this user");
        }

        return verifyCode(user.getMfaSecret(), code);
    }
}

// Made with Bob
