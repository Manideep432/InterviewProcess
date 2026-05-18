package com.login.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for password validation
 * Validates:
 * - At least 15 characters long
 * - Mix of at least 2 character types (lowercase, uppercase, numbers, special characters)
 * - Password strength score
 * 
 * @author Bob
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    
    String message() default "Password does not meet security requirements";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}

// Made with Bob
