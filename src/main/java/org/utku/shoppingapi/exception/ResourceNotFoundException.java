package org.utku.shoppingapi.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This is a runtime exception that typically results in a 404 HTTP status code.
 * 
 * Used when:
 * - A user, product, cart, or order is requested by ID but doesn't exist
 * - A resource reference is invalid or has been deleted
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * 
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}