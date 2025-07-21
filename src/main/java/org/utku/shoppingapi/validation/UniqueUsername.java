package org.utku.shoppingapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure username uniqueness.
 * This annotation validates that a username is not already taken by another user.
 * <p>
 * Usage: @UniqueUsername on String fields that represent usernames
 * </p>
 * <p>
 * Example:
 * <pre>
 * {@code
 * @UniqueUsername
 * private String username;
 * }
 * </pre>
 * </p>
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    /**
     * Error message to be returned if validation fails.
     */
    String message() default "This username is already taken";
    /**
     * Allows specification of validation groups.
     */
    Class<?>[] groups() default {};
    /**
     * Allows specification of custom payload objects.
     */
    Class<? extends Payload>[] payload() default {};
}