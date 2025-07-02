package org.utku.shoppingapi.constants;

/**
 * Application-wide constants to avoid magic numbers and strings.
 * This class centralizes all constant values used throughout the application.
 * Constants are grouped by functionality for better organization.
 */
public final class AppConstants {
    
    /**
     * Pagination-related constants.
     */
    public static final class Pagination {
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;
        public static final String DEFAULT_SORT_FIELD = "id";
        public static final String DEFAULT_SORT_DIRECTION = "asc";
        
        private Pagination() {}
    }
    
    /**
     * Validation-related constants for field lengths and constraints.
     */
    public static final class Validation {
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final int MIN_PASSWORD_LENGTH = 6;
        public static final int MAX_PHONE_LENGTH = 15;
        public static final int MAX_EMAIL_LENGTH = 100;
        public static final int MAX_NAME_LENGTH = 100;
        public static final int MAX_PRODUCT_NAME_LENGTH = 255;
        public static final int MIN_PRODUCT_NAME_LENGTH = 2;
        public static final int MAX_DESCRIPTION_LENGTH = 1000;
        public static final int MAX_CATEGORY_LENGTH = 100;
        public static final int MAX_SESSION_ID_LENGTH = 100;
        
        private Validation() {}
    }
    
    /**
     * Cart and order-related constants.
     */
    public static final class Cart {
        public static final int MIN_QUANTITY = 1;
        public static final int MAX_QUANTITY = 100;
        
        private Cart() {}
    }
    
    /**
     * Error message constants for consistent messaging.
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
        
        private ErrorMessages() {}
    }
    
    /**
     * Database query constants to avoid magic strings in repositories.
     */
    public static final class Queries {
        public static final String FIND_ACTIVE_USERS = "SELECT u FROM User u WHERE u.enabled = true";
        public static final String FIND_ACTIVE_USER_BY_EMAIL = "SELECT u FROM User u WHERE u.email = :email AND u.enabled = true";
        public static final String FIND_AVAILABLE_PRODUCTS = "SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity > 0";
        public static final String FIND_PRODUCTS_BY_PRICE_RANGE = "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true";
        public static final String FIND_LOW_STOCK_PRODUCTS = "SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.active = true";
        
        private Queries() {}
    }
    
    /**
     * HTTP response message constants for consistent API responses.
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
    
    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}