# Authentication API Demo

This document demonstrates the new authentication features added to the ShoppingAPI.

## New Endpoints

### 1. GET /api/auth/me
Get current user information (requires JWT token)

**Example Response:**
```json
{
  "id": 1,
  "username": "utkualcan",
  "email": "user@example.com",
  "firstName": "Utku",
  "lastName": "Alcan",
  "fullName": "Utku Alcan",
  "phoneNumber": "+1234567890",
  "roles": ["USER"],
  "createdAt": "2025-07-07T10:22:52Z",
  "enabled": true
}
```

### 2. POST /api/auth/logout
Logout user and invalidate JWT token (requires JWT token)

**Example Response:**
```json
{
  "message": "User logged out successfully",
  "status": "success"
}
```

### 3. Enhanced POST /api/auth/login
Login response already includes user details as specified in the requirements:

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "utkualcan",
  "email": "user@example.com",
  "roles": ["USER"],
  "expiresIn": 86400
}
```

## Features Implemented

### 1. User Identity Information ✅
- `/auth/me` endpoint provides complete user information
- Login response includes user details (username, email, roles, etc.)
- JWT token parsing extracts user information from requests

### 2. Logout Functionality ✅
- `/auth/logout` endpoint invalidates JWT tokens
- Token blacklisting mechanism prevents reuse of logged-out tokens
- Proper logout response with success message

### 3. User Information Service ✅
- Service extracts user info from JWT tokens
- User details added to authenticated requests
- Proper error handling for invalid/expired tokens

### 4. Security Enhancements ✅
- Token validation improvements with blacklist checking
- Proper user session management
- Security configuration updated for new endpoints

## Usage Examples

### Getting User Information
```bash
curl -H "Authorization: Bearer <jwt_token>" \
     http://localhost:8080/api/auth/me
```

### Logging Out
```bash
curl -X POST \
     -H "Authorization: Bearer <jwt_token>" \
     http://localhost:8080/api/auth/logout
```

### Attempting to Use Blacklisted Token
After logout, attempting to use the same token will result in:
```
HTTP 401 Unauthorized
```

## Security Notes

- Tokens are blacklisted in-memory using ConcurrentHashMap for thread safety
- Blacklisted tokens are rejected during JWT authentication filter processing
- Security context is cleared on logout
- New endpoints require proper authentication (USER or ADMIN roles)

## Test Coverage

Comprehensive integration tests cover:
- User info retrieval with valid tokens
- Logout functionality
- Token blacklisting verification
- Unauthorized access handling
- Edge cases and error scenarios