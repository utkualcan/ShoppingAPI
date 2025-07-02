package org.utku.shoppingapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * 
 * Handles various types of exceptions including:
 * - Resource not found errors
 * - Validation errors
 * - Data integrity violations
 * - Business logic exceptions
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException thrown when requested resources are not found.
     * 
     * @param ex The ResourceNotFoundException instance
     * @return ResponseEntity with 404 Not Found status and error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles InsufficientStockException thrown when there's not enough stock for an operation.
     * 
     * @param ex The InsufficientStockException instance
     * @return ResponseEntity with 400 Bad Request status and error message
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException thrown when invalid arguments are provided.
     * 
     * @param ex The IllegalArgumentException instance
     * @return ResponseEntity with 400 Bad Request status and error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles TransactionSystemException, typically caused by validation errors during transaction commit.
     * 
     * @param ex The TransactionSystemException instance
     * @return ResponseEntity with appropriate status and error message
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
     * Handles DataIntegrityViolationException thrown when database constraints are violated.
     * Provides specific error messages for different types of constraint violations.
     * 
     * @param ex The DataIntegrityViolationException instance
     * @return ResponseEntity with appropriate status and user-friendly error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        if (message.contains("value too long")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppConstants.ResponseMessages.DATA_TOO_LONG);
        }
        if (message.contains("duplicate key") || message.contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AppConstants.ResponseMessages.DUPLICATE_ENTRY);
        }
        if (message.contains("foreign key constraint") || message.contains("is not present in table")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppConstants.ResponseMessages.REFERENCED_RECORD_NOT_EXISTS);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(AppConstants.ResponseMessages.USER_CANNOT_BE_DELETED);
    }

    /**
     * Handles MethodArgumentNotValidException thrown when request body validation fails.
     * Returns a map of field names and their corresponding error messages.
     * 
     * @param ex The MethodArgumentNotValidException instance
     * @return ResponseEntity with 400 Bad Request status and validation error details
     */
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

    /**
     * Handles PropertyReferenceException thrown when invalid sorting parameters are provided.
     * 
     * @param ex The PropertyReferenceException instance
     * @return ResponseEntity with 400 Bad Request status and sorting usage instructions
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<String> handlePropertyReferenceException(PropertyReferenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AppConstants.ResponseMessages.INVALID_SORTING_PARAMETER);
    }
}