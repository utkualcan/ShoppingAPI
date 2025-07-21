package org.utku.shoppingapi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message response DTO for authentication operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    /**
     * Message describing the result of the operation.
     */
    private String message;
    /**
     * Unique identifier of the affected user (if applicable).
     */
    private Long userId;
    /**
     * Role assigned to the user (if applicable).
     */
    private String role;

    public MessageResponse(String message) {
        this.message = message;
    }
}