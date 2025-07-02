package org.utku.shoppingapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.utku.shoppingapi.repository.UserRepository;

/**
 * Validator implementation for the UniqueUsername constraint.
 * This class performs the actual validation logic to check if a username
 * is already taken by querying the user repository.
 */
@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Initializes the validator (no initialization needed for this validator).
     * 
     * @param constraintAnnotation The annotation instance
     */
    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        // No initialization required
    }

    /**
     * Validates that the provided username is unique.
     * 
     * @param username The username to validate
     * @param context The validation context
     * @return true if username is unique or null, false if already taken
     */
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Let @NotNull handle null values
        }
        return !userRepository.existsByUsername(username);
    }
}