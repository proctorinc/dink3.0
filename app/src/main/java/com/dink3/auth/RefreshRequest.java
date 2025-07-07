package com.dink3.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public class RefreshRequest {
    @Schema(description = "User ID", example = "1")
    public String userId;

    @Schema(description = "Refresh token", example = "some-refresh-token-value")
    public String refreshToken;
} 