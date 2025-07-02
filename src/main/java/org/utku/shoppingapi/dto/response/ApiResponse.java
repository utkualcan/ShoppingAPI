package org.utku.shoppingapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.utku.shoppingapi.constants.AppConstants;

/**
 * Generic API response wrapper for consistent response format.
 * This class provides a standardized structure for all API responses,
 * including success/error status, message, and data payload.
 * 
 * @param <T> The type of data being returned in the response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    
    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;
    
    /**
     * Human-readable message describing the result.
     */
    private String message;
    
    /**
     * The actual data payload of the response.
     */
    private T data;
    
    /**
     * Creates a successful response with data and default success message.
     * 
     * @param data The data to include in the response
     * @param <T> The type of the data
     * @return ApiResponse indicating success with the provided data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, AppConstants.ResponseMessages.OPERATION_SUCCESSFUL, data);
    }
    
    /**
     * Creates a successful response with custom message and data.
     * 
     * @param message Custom success message
     * @param data The data to include in the response
     * @param <T> The type of the data
     * @return ApiResponse indicating success with custom message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    /**
     * Creates an error response with the specified error message.
     * 
     * @param message Error message describing what went wrong
     * @param <T> The type of the data (will be null for error responses)
     * @return ApiResponse indicating failure with error message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}