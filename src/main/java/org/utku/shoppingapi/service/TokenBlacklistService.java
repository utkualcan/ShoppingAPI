package org.utku.shoppingapi.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing blacklisted JWT tokens.
 * Provides functionality to blacklist tokens during logout and check if tokens are blacklisted.
 */
@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Add a token to the blacklist.
     * 
     * @param token JWT token to blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Check if a token is blacklisted.
     * 
     * @param token JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Remove a token from the blacklist (for cleanup purposes).
     * 
     * @param token JWT token to remove from blacklist
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * Get the number of blacklisted tokens.
     * 
     * @return number of blacklisted tokens
     */
    public int getBlacklistedTokensCount() {
        return blacklistedTokens.size();
    }
}