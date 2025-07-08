package com.dink3;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestDatabaseCleanup implements AfterAllCallback {
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Clean up test database file after all tests in a class complete
        cleanupTestDatabase();
    }
    
    public static void cleanupTestDatabase() {
        try {
            Path testDbPath = Paths.get("dink3-test.db");
            if (Files.exists(testDbPath)) {
                Files.delete(testDbPath);
                System.out.println("Test database cleaned up: " + testDbPath.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to cleanup test database: " + e.getMessage());
        }
    }
} 