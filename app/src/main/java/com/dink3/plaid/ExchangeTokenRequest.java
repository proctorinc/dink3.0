package com.dink3.plaid;

import io.swagger.v3.oas.annotations.media.Schema;

public class ExchangeTokenRequest {
    @Schema(description = "Plaid public token", example = "example-plaid-token")
    public String publicToken;
} 