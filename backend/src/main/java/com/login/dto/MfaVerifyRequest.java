package com.login.dto;

import jakarta.validation.constraints.NotNull;

/**
 * MFA Verify Request DTO
 * Used for verifying MFA codes
 * 
 * @author Bob
 */
public class MfaVerifyRequest {

    @NotNull(message = "Verification code is required")
    private Integer code;

    // Constructors
    public MfaVerifyRequest() {
    }

    public MfaVerifyRequest(Integer code) {
        this.code = code;
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}

// Made with Bob
