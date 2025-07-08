package com.dink3;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith({SpringExtension.class, TestDatabaseCleanup.class})
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class TestConfig {
    // This base class ensures all tests run with test profile and transaction rollback
} 