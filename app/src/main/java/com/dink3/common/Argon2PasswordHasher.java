package com.dink3.common;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordHasher {
    // OWASP recommendations (as of 2024):
    // - salt length: 16 bytes
    // - hash length: 32 bytes
    // - parallelism: 1
    // - memory: 65536 KB (64 MB)
    // - iterations: 3
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY = 65536;
    private static final int ITERATIONS = 3;

    private final Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, ITERATIONS);

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
} 