package com.login.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Password Validator Implementation
 * Validates password against security requirements:
 * - At least 15 characters long
 * - Mix of at least 2 character types (lowercase, uppercase, numbers, special characters)
 * - Password strength score (checks for common patterns)
 * 
 * @author Bob
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 15;
    private static final int MIN_CHARACTER_TYPES = 2;
    
    // Patterns for character types
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    
    // Common weak password patterns
    private static final Pattern[] WEAK_PATTERNS = {
        Pattern.compile("(?i)password"),
        Pattern.compile("(?i)12345"),
        Pattern.compile("(?i)qwerty"),
        Pattern.compile("(?i)abc"),
        Pattern.compile("(?i)111"),
        Pattern.compile("(?i)000"),
        Pattern.compile("(.)\\1{3,}"), // Repeated characters (e.g., aaaa)
        Pattern.compile("(?i)(012|123|234|345|456|567|678|789|890)") // Sequential numbers
    };

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            addConstraintViolation(context, "Password is required");
            return false;
        }

        // Check minimum length
        if (password.length() < MIN_LENGTH) {
            addConstraintViolation(context, "Password must be at least 15 characters long");
            return false;
        }

        // Check character type diversity
        int characterTypes = countCharacterTypes(password);
        if (characterTypes < MIN_CHARACTER_TYPES) {
            addConstraintViolation(context, 
                "Password must contain at least 2 different character types (lowercase, uppercase, numbers, special characters)");
            return false;
        }

        // Check password strength (detect weak patterns)
        if (!hasAcceptableStrength(password)) {
            addConstraintViolation(context, 
                "Password is too weak. Avoid common patterns like 'password12345', repeated characters, or sequential numbers");
            return false;
        }

        return true;
    }

    /**
     * Count the number of different character types in the password
     */
    private int countCharacterTypes(String password) {
        int types = 0;
        
        if (LOWERCASE_PATTERN.matcher(password).find()) {
            types++;
        }
        if (UPPERCASE_PATTERN.matcher(password).find()) {
            types++;
        }
        if (DIGIT_PATTERN.matcher(password).find()) {
            types++;
        }
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            types++;
        }
        
        return types;
    }

    /**
     * Check if password has acceptable strength
     * Returns false if password contains common weak patterns
     */
    private boolean hasAcceptableStrength(String password) {
        for (Pattern weakPattern : WEAK_PATTERNS) {
            if (weakPattern.matcher(password).find()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add custom constraint violation message
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }
}

// Made with Bob
