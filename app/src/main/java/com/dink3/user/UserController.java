package com.dink3.user;

import com.dink3.jooq.tables.pojos.Users;
import com.dink3.user.dto.UserProfileDto;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "User", description = "User operations")
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal Users user) {
        if (user == null) {
            log.warn("Unauthorized access attempt to /me endpoint");
            return ResponseEntity.status(401).body("Unauthorized");
        }
        log.info("User info accessed for userId: {}", user.getId());
        Optional<UserProfileDto> profileOpt = userService.getUserProfile(user.getId());
        if (profileOpt.isEmpty()) {
            log.error("User profile not found for userId: {}", user.getId());
            return ResponseEntity.status(404).body("User profile not found");
        }
        return ResponseEntity.ok(profileOpt.get());
    }
} 