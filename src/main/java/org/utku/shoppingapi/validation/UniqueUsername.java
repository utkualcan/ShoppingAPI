package org.utku.shoppingapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure username uniqueness.
 * This annotation validates that a username is not already taken by another user.
 * 
 * Usage: @UniqueUsername on String fields that represent usernames
 * 
 * @author Shopping API Team
 * @version 1.0
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "This username is already taken";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}