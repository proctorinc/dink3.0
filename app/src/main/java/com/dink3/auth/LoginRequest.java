package com.dink3.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {
    @Schema(description = "User's email", example = "user@example.com")
    public String email;

    @Schema(description = "User's password", example = "StrongPassword123")
    public String password;
} 