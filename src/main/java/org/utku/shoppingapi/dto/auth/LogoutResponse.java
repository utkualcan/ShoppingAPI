package org.utku.shoppingapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for logout response.
 * Contains logout success message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {
    /**
     * Logout operation result message.
     */
    private String message;
    /**
     * Status of the logout operation (e.g., "success").
     */
    private String status;
    
    public LogoutResponse(String message) {
        this.message = message;
        this.status = "success";
    }
}