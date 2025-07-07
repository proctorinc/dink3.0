package com.dink3.common;

import com.plaid.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PlaidConfig {
    
    @Value("${plaid.client-id}")
    private String clientId;
    
    @Value("${plaid.secret}")
    private String secret;
    
    @Value("${plaid.environment}")
    private String environment;
    
    @Bean
    public ApiClient plaidApiClient() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", clientId);
        apiKeys.put("secret", secret);
        
        ApiClient apiClient = new ApiClient(apiKeys);
        
        // Set the environment based on configuration
        switch (environment.toLowerCase()) {
            case "sandbox":
                apiClient.setPlaidAdapter(ApiClient.Sandbox);
                break;
            case "development":
                apiClient.setPlaidAdapter(ApiClient.Development);
                break;
            case "production":
                apiClient.setPlaidAdapter(ApiClient.Production);
                break;
            default:
                apiClient.setPlaidAdapter(ApiClient.Sandbox);
        }
        
        return apiClient;
    }
} 