package com.login.dto;

/**
 * MFA Setup Response DTO
 * Contains QR code and secret for MFA setup
 * 
 * @author Bob
 */
public class MfaSetupResponse {

    private String secret;
    private String qrCodeUrl;
    private String qrCodeImage;
    private String message;

    // Constructors
    public MfaSetupResponse() {
    }

    public MfaSetupResponse(String secret, String qrCodeUrl, String qrCodeImage, String message) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
        this.qrCodeImage = qrCodeImage;
        this.message = message;
    }

    // Getters and Setters
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

// Made with Bob
