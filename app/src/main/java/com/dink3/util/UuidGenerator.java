package com.dink3.util;

import java.util.UUID;

/**
 * Utility class for generating secure UUIDs for database entities.
 * Uses UUID.randomUUID() which generates cryptographically secure random UUIDs.
 */
public class UuidGenerator {
    
    /**
     * Generate a new secure UUID as a string.
     * 
     * @return A new UUID string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generate a new secure UUID without hyphens (for shorter URLs).
     * 
     * @return A new UUID string without hyphens
     */
    public static String generateShortUuid() {
        return UUID.randomUUID().toString();
    }
} 