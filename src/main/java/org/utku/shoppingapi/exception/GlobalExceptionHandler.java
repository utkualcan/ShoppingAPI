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
 * This class handles all exceptions thrown by controllers and provides
 * consistent error responses across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

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
     * Handles database integrity violations.
     * This updated method provides more specific error responses for common issues
     * like foreign key constraints and duplicate entries.
     *
     * @param ex The DataIntegrityViolationException instance.
     * @return ResponseEntity with a specific status and a clear error message.
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


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<String> handlePropertyReferenceException(PropertyReferenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AppConstants.ResponseMessages.INVALID_SORTING_PARAMETER);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid username or password");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "You do not have the required role to perform this action.");

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

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
}