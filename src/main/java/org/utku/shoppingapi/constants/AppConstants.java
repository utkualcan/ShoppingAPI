package org.utku.shoppingapi.constants;

/**
 * Centralized repository of application-wide constants for the Shopping API.
 * 
 * <p>This utility class serves as the single source of truth for all constant
 * values used throughout the e-commerce application. By centralizing constants,
 * we eliminate magic numbers and strings, improve maintainability, and ensure
 * consistency across the entire codebase.
 * 
 * <p>The constants are organized into logical groups using nested static classes:
 * <ul>
 *   <li>{@link Pagination} - Pagination and sorting defaults</li>
 *   <li>{@link Validation} - Field length limits and validation constraints</li>
 *   <li>{@link Cart} - Shopping cart business rules and limits</li>
 *   <li>{@link ErrorMessages} - Standardized error messages</li>
 *   <li>{@link Queries} - Database query constants</li>
 *   <li>{@link ResponseMessages} - HTTP response messages</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Immutable constants using {@code public static final}</li>
 *   <li>Logical grouping for better organization and discoverability</li>
 *   <li>Backward compatibility through top-level constant aliases</li>
 *   <li>Prevention of instantiation through private constructor</li>
 * </ul>
 * 
 * <p>Usage examples:
 * <pre>
 * // Using grouped constants (recommended)
 * int pageSize = AppConstants.Pagination.DEFAULT_SIZE;
 * String errorMsg = AppConstants.ErrorMessages.USER_NOT_FOUND;
 * 
 * // Using backward-compatible constants
 * int pageSize = AppConstants.DEFAULT_PAGE_SIZE;
 * </pre>
 * 
 * @author Shopping API Development Team
 * @version 1.0
 * @since 1.0
 */
public final class AppConstants {
    
    /**
     * Constants related to pagination and data retrieval configuration.
     * 
     * <p>These constants define default behaviors for paginated API endpoints,
     * ensuring consistent pagination across all controllers and preventing
     * performance issues from oversized result sets.
     * 
     * @since 1.0
     */
    public static final class Pagination {
        /** Default number of items per page for paginated results. */
        public static final int DEFAULT_SIZE = 20;
        /** Maximum allowed page size to prevent performance issues. */
        public static final int MAX_SIZE = 100;
        /** Default field used for sorting when no sort parameter is specified. */
        public static final String DEFAULT_SORT_FIELD = "id";
        /** Default sort direction (ascending) when no direction is specified. */
        public static final String DEFAULT_SORT_DIRECTION = "asc";
        
        /** Private constructor to prevent instantiation of nested utility class. */
        private Pagination() {}
    }
    
    /**
     * Constants defining validation rules and field length constraints.
     * 
     * <p>These constants ensure consistent validation across all entities
     * and DTOs, preventing database constraint violations and maintaining
     * data quality standards.
     * 
     * <p>Values are aligned with database schema definitions to prevent
     * data truncation and constraint violations.
     * 
     * @since 1.0
     */
    public static final class Validation {
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final int MIN_PASSWORD_LENGTH = 6;
        public static final int MAX_PHONE_LENGTH = 15; // Maximum allowed length for phone numbers
        public static final int MAX_EMAIL_LENGTH = 100; // Maximum allowed length for email addresses
        public static final int MAX_NAME_LENGTH = 100; // Maximum allowed length for user names
        public static final int MAX_PRODUCT_NAME_LENGTH = 255; // Maximum allowed length for product names
        public static final int MIN_PRODUCT_NAME_LENGTH = 2; // Minimum allowed length for product names
        public static final int MAX_DESCRIPTION_LENGTH = 1000; // Maximum allowed length for product descriptions
        public static final int MAX_CATEGORY_LENGTH = 100; // Maximum allowed length for category names
        public static final int MAX_SESSION_ID_LENGTH = 100; // Maximum allowed length for session IDs

        /** Private constructor to prevent instantiation of nested utility class. */
        private Validation() {}
    }
    
    /**
     * Constants governing shopping cart and order business rules.
     * 
     * <p>These constants define business constraints for e-commerce operations,
     * such as minimum and maximum quantities that can be added to a cart.
     * 
     * @since 1.0
     */
    public static final class Cart {
        public static final int MIN_QUANTITY = 1; // Minimum quantity allowed per cart item
        public static final int MAX_QUANTITY = 100; // Maximum quantity allowed per cart item

        /** Private constructor to prevent instantiation of nested utility class. */
        private Cart() {}
    }
    
    /**
     * Standardized error messages for consistent user communication.
     * 
     * <p>These constants ensure that error messages are consistent across
     * the entire application, improving user experience and simplifying
     * internationalization efforts.
     * 
     * <p>Messages are designed to be informative yet secure, avoiding
     * exposure of sensitive system information.
     * 
     * @since 1.0
     */
    public static final class ErrorMessages {
        public static final String USER_NOT_FOUND = "User not found with id: ";
        public static final String PRODUCT_NOT_FOUND = "Product not found with id: ";
        public static final String CART_NOT_FOUND = "Cart not found with id: ";
        public static final String ORDER_NOT_FOUND = "Order not found with id: ";
        public static final String CART_IS_EMPTY = "Cart is empty";
        public static final String INSUFFICIENT_STOCK = "Insufficient stock for product: ";
        public static final String PRODUCT_NOT_FOUND_IN_CART = "Product not found in cart";
        public static final String ALREADY_IN_FAVORITES = "Product is already in favorites";
        public static final String RESOURCE_IN_USE = "This resource cannot be deleted because it is in use by other records.";
        
        /** Private constructor to prevent instantiation of nested utility class. */
        private ErrorMessages() {}
    }
    
    /**
     * JPQL query constants for repository layer operations.
     * 
     * <p>These constants centralize complex JPQL queries used across
     * repository classes, improving maintainability and reducing the
     * risk of query syntax errors.
     * 
     * <p>All queries are optimized for performance and follow JPA
     * best practices for entity relationships and fetching strategies.
     * 
     * @since 1.0
     */
    public static final class Queries {
        public static final String FIND_ACTIVE_USERS = "SELECT u FROM User u WHERE u.enabled = true";
        public static final String FIND_ACTIVE_USER_BY_EMAIL = "SELECT u FROM User u WHERE u.email = :email AND u.enabled = true";
        public static final String FIND_AVAILABLE_PRODUCTS = "SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity > 0";
        public static final String FIND_PRODUCTS_BY_PRICE_RANGE = "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true";
        public static final String FIND_LOW_STOCK_PRODUCTS = "SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true";
        
        /** Private constructor to prevent instantiation of nested utility class. */
        private Queries() {}
    }
    
    /**
     * Standardized HTTP response messages for API consistency.
     * 
     * <p>These constants ensure that API responses provide consistent
     * messaging across all endpoints, improving client integration
     * and user experience.
     * 
     * <p>Messages are designed to be informative for developers while
     * remaining user-friendly for end-user applications.
     * 
     * @since 1.0
     */
    public static final class ResponseMessages {
        public static final String OPERATION_SUCCESSFUL = "Operation successful";
        public static final String VALIDATION_ERROR = "Validation error: ";
        public static final String TRANSACTION_ERROR = "Transaction error";
        public static final String DATA_TOO_LONG = "Data too long for database field. Please check field lengths.";
        public static final String DUPLICATE_ENTRY = "Duplicate entry. Username or email already exists.";
        public static final String REFERENCED_RECORD_NOT_EXISTS = "Referenced record does not exist. Please check the provided IDs.";
        public static final String USER_CANNOT_BE_DELETED = "This user cannot be deleted. Related orders exist.";
        public static final String INVALID_SORTING_PARAMETER = "Invalid sorting parameter. Usage: sort=id,asc or sort=username,desc. Valid fields: id, username, email, createdAt";
        
        /** Private constructor to prevent instantiation of nested utility class. */
        private ResponseMessages() {}
    }
    
    // Backward compatibility - keeping old constants as aliases
    public static final int DEFAULT_PAGE_SIZE = Pagination.DEFAULT_SIZE;
    public static final int MAX_PAGE_SIZE = Pagination.MAX_SIZE;
    public static final String DEFAULT_SORT_FIELD = Pagination.DEFAULT_SORT_FIELD;
    public static final String DEFAULT_SORT_DIRECTION = Pagination.DEFAULT_SORT_DIRECTION;
    public static final int MIN_USERNAME_LENGTH = Validation.MIN_USERNAME_LENGTH;
    public static final int MAX_USERNAME_LENGTH = Validation.MAX_USERNAME_LENGTH;
    public static final int MIN_PASSWORD_LENGTH = Validation.MIN_PASSWORD_LENGTH;
    public static final int MAX_PHONE_LENGTH = Validation.MAX_PHONE_LENGTH;
    public static final int MAX_EMAIL_LENGTH = Validation.MAX_EMAIL_LENGTH;
    public static final int MAX_NAME_LENGTH = Validation.MAX_NAME_LENGTH;
    public static final int MAX_PRODUCT_NAME_LENGTH = Validation.MAX_PRODUCT_NAME_LENGTH;
    public static final int MIN_PRODUCT_NAME_LENGTH = Validation.MIN_PRODUCT_NAME_LENGTH;
    public static final int MAX_DESCRIPTION_LENGTH = Validation.MAX_DESCRIPTION_LENGTH;
    public static final int MAX_CATEGORY_LENGTH = Validation.MAX_CATEGORY_LENGTH;
    public static final int MAX_SESSION_ID_LENGTH = Validation.MAX_SESSION_ID_LENGTH;
    public static final int MIN_QUANTITY = Cart.MIN_QUANTITY;
    public static final int MAX_QUANTITY = Cart.MAX_QUANTITY;
    public static final String USER_NOT_FOUND = ErrorMessages.USER_NOT_FOUND;
    public static final String PRODUCT_NOT_FOUND = ErrorMessages.PRODUCT_NOT_FOUND;
    public static final String CART_NOT_FOUND = ErrorMessages.CART_NOT_FOUND;
    public static final String ORDER_NOT_FOUND = ErrorMessages.ORDER_NOT_FOUND;
    public static final String CART_IS_EMPTY = ErrorMessages.CART_IS_EMPTY;
    public static final String INSUFFICIENT_STOCK = ErrorMessages.INSUFFICIENT_STOCK;
    public static final String PRODUCT_NOT_FOUND_IN_CART = ErrorMessages.PRODUCT_NOT_FOUND_IN_CART;
    public static final String ALREADY_IN_FAVORITES = ErrorMessages.ALREADY_IN_FAVORITES;
    public static final String RESOURCE_IN_USE = ErrorMessages.RESOURCE_IN_USE;
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * 
     * <p>This class is designed to be used only for its static constants
     * and should never be instantiated. The constructor throws an
     * UnsupportedOperationException to enforce this design decision.
     * 
     * @throws UnsupportedOperationException always, to prevent instantiation
     */
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}