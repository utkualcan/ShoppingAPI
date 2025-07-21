package org.utku.shoppingapi.exception;

/**
 * Exception thrown when a validation error occurs in the application.
 * This is a custom runtime exception for handling validation failures.
 */
public class ValidationException extends RuntimeException {
    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message explaining the validation error
     */
    public ValidationException(String message) {
        super(message);
    }
}
