package com.dink3.auth;

import com.dink3.jooq.tables.pojos.Users;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Auth", description = "Authentication and JWT operations")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body) {
        String email = body.email;
        String username = body.username;
        String password = body.password;
        log.info("Registration attempt for email: {}", email);
        if (!authService.validatePasswordStrength(password)) {
            log.warn("Registration failed for email: {} due to weak password", email);
            return ResponseEntity.badRequest().body("Password does not meet requirements");
        }
        Users user = authService.register(email, username, password);
        log.info("User registered successfully: {}", email);
        String jwt = authService.generateJwtToken(user);
        String refresh = authService.generateRefreshToken(user);
        return ResponseEntity.ok(Map.of("token", jwt, "refreshToken", refresh));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        String email = body.email;
        String password = body.password;
        log.info("Login attempt for email: {}", email);
        Optional<Users> userOpt = authService.authenticate(email, password);
        if (userOpt.isEmpty()) {
            log.warn("Login failed for email: {}", email);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        Users user = userOpt.get();
        log.info("Login successful for email: {}", email);
        String jwt = authService.generateJwtToken(user);
        String refresh = authService.generateRefreshToken(user);
        return ResponseEntity.ok(Map.of("token", jwt, "refreshToken", refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest body) {
        String userId = body.userId;
        String refreshToken = body.refreshToken;
        log.info("Refresh token attempt for userId: {}", userId);
        // TODO: Validate refresh token
        return ResponseEntity.ok(Map.of("token", "new-jwt-token"));
    }
} 