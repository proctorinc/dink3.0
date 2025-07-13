package com.dink3.auth;

import com.dink3.jooq.tables.pojos.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "Authentication and JWT operations")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private static final Logger log = LoggerFactory.getLogger(
        AuthController.class
    );

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
        @Valid @RequestBody RegisterRequest body
    ) {
        try {
            User user = authService.register(
                body.email,
                body.username,
                body.password
            );
            log.info("User registered successfully: {}", body.email);
            String jwt = authService.generateJwtToken(user);
            String refresh = authService.generateRefreshToken(user);
            return ResponseEntity.ok(
                Map.of("token", jwt, "refreshToken", refresh)
            );
        } catch (IllegalArgumentException e) {
            log.warn(
                "Registration failed for email: {} - {}",
                body.email,
                e.getMessage()
            );
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        String email = body.email;
        String password = body.password;
        log.info("Login attempt for email: {}", email);
        Optional<User> userOpt = authService.authenticate(email, password);
        if (userOpt.isEmpty()) {
            log.warn("Login failed for email: {}", email);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        User user = userOpt.get();
        log.info("Login successful for email: {}", email);
        String jwt = authService.generateJwtToken(user);
        String refresh = authService.generateRefreshToken(user);
        return ResponseEntity.ok(Map.of("token", jwt, "refreshToken", refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest body) {
        String userId = body.userId;
        // String refreshToken = body.refreshToken;
        log.info("Refresh token attempt for userId: {}", userId);
        // TODO: Validate refresh token
        return ResponseEntity.ok(Map.of("token", "new-jwt-token"));
    }
}
