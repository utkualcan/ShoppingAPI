package org.utku.shoppingapi.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom handler for access denied exceptions in Spring Security.
 * Returns a standardized JSON error response when a user attempts to access a forbidden resource.
 * <p>
 * Usage scenarios:
 * <ul>
 *   <li>Triggered when a user lacks the required role or permission for an endpoint</li>
 *   <li>Used by Spring Security's AccessDeniedHandler interface</li>
 * </ul>
 * <p>
 * The response includes status, error type, message, and request path for client-side handling.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * ObjectMapper for serializing error response to JSON.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles access denied exceptions and writes a JSON error response.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param accessDeniedException the exception thrown by Spring Security
     * @throws IOException if writing to the response fails
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("error", "Forbidden");
        body.put("message", "You do not have permission to access this resource.");
        body.put("path", request.getServletPath());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}