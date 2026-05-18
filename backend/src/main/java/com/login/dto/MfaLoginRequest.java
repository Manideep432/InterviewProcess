package com.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * MFA Login Request DTO
 * Used for login with MFA code
 * 
 * @author Bob
 */
public class MfaLoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "MFA code is required")
    private Integer mfaCode;

    // Constructors
    public MfaLoginRequest() {
    }

    public MfaLoginRequest(String username, String password, Integer mfaCode) {
        this.username = username;
        this.password = password;
        this.mfaCode = mfaCode;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMfaCode() {
        return mfaCode;
    }

    public void setMfaCode(Integer mfaCode) {
        this.mfaCode = mfaCode;
    }
}

// Made with Bob
