package org.utku.shoppingapi.exception;

/**
 * Exception thrown when there is insufficient stock for a requested operation.
 * This is a runtime exception that typically results in a 400 HTTP status code.
 * 
 * Used when:
 * - Attempting to order more items than available in stock
 * - Adding items to cart when stock is insufficient
 * - Any operation that would result in negative stock
 */
public class InsufficientStockException extends RuntimeException {
    
    /**
     * Constructs a new InsufficientStockException with the specified detail message.
     * 
     * @param message the detail message explaining the stock shortage
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}