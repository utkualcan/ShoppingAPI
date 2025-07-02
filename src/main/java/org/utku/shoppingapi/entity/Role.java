package org.utku.shoppingapi.entity;

/**
 * Enumeration representing user roles in the system.
 * Defines the different levels of access and permissions available to users.
 */
public enum Role {
    
    /**
     * Standard user role with basic access permissions.
     * Can browse products, manage cart, place orders, and manage favorites.
     */
    USER,
    
    /**
     * Administrator role with elevated permissions.
     * Can manage products, view all orders, and perform administrative tasks.
     */
    ADMIN
}