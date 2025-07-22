package org.utku.shoppingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.data.mapping.PropertyReferenceException;
import org.utku.shoppingapi.constants.AppConstants;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Shopping API.
 * Handles all exceptions thrown by controllers and provides
 * consistent error responses across the application.
 * <p>
 * Usage scenarios:
 * <ul>
 *   <li>Handles validation, database, authentication, and business logic errors</li>
 *   <li>Returns standardized error messages and status codes</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles ResourceNotFoundException and returns 404 status.
     * @param ex the exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles InsufficientStockException and returns 400 status.
     * @param ex the exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException and returns 400 status.
     * @param ex the exception
     * @return ResponseEntity with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles validation errors from JPA transactions.
     * @param ex the exception
     * @return ResponseEntity with validation error message
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handleValidation(TransactionSystemException ex) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppConstants.ResponseMessages.VALIDATION_ERROR +
                            cve.getConstraintViolations().iterator().next().getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AppConstants.ResponseMessages.TRANSACTION_ERROR);
    }

    /**
     * Handles database integrity violations (foreign key, duplicate, value too long).
     * @param ex the exception
     * @return ResponseEntity with specific error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex) {
        // Get the most specific cause to analyze the root problem from the database.
        String message = ex.getMostSpecificCause().getMessage().toLowerCase();

        // Check for foreign key constraint violation (resource in use).
        if (message.contains("violates foreign key constraint")) {
            return ResponseEntity.status(HttpStatus.CONFLICT) // Use 409 Conflict for this case.
                    .body(AppConstants.ErrorMessages.RESOURCE_IN_USE); // Provide a clear, user-friendly message.
        }

        // Check for duplicate key violation (e.g., unique username or email).
        if (message.contains("duplicate key") || message.contains("unique constraint")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AppConstants.ResponseMessages.DUPLICATE_ENTRY);
        }

        // Check for "value too long" error.
        if (message.contains("value too long")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppConstants.ResponseMessages.DATA_TOO_LONG);
        }

        // A generic fallback for other data integrity issues.
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("A database integrity error occurred. Please check your request.");
    }

    /**
     * Handles validation errors from request body.
     * @param ex the exception
     * @return ResponseEntity with field error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // Custom message for phone number validation error
            if (fieldName.equals("phoneNumber")) {
                errorMessage = "Invalid phone number. Only digits, +, -, spaces, and parentheses are allowed.";
            }
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles invalid property reference in sorting or filtering.
     * @param ex the exception
     * @return ResponseEntity with sorting error message
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<String> handlePropertyReferenceException(PropertyReferenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AppConstants.ResponseMessages.INVALID_SORTING_PARAMETER);
    }

    /**
     * Handles authentication failures due to bad credentials.
     * @param ex the exception
     * @return ResponseEntity with authentication error message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    /**
     * Handles authentication failures due to missing user.
     * @param ex the exception
     * @return ResponseEntity with authentication error message
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    /**
     * Handles access denied exceptions and returns 403 status.
     * @param ex the exception
     * @return ResponseEntity with forbidden error message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "You do not have the required role to perform this action.");

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles unexpected runtime exceptions.
     * @param ex the exception
     * @return ResponseEntity with generic error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Username is already taken") ||
                    ex.getMessage().contains("Email is already in use")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
            }
        }
        // It's good practice to log unexpected errors.
        // log.error("An unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred");
    }

    /**
     * Handles validation errors from entity constraints (e.g. phone number pattern).
     * Returns a field-specific error message with HTTP 400 status.
     *
     * @param ex ConstraintViolationException thrown by validation
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            // Custom message for phone number validation error
            if (field.contains("phoneNumber")) {
                message = "Invalid phone number. Only digits, +, -, spaces, and parentheses are allowed.";
            }
            errors.put(field, message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}