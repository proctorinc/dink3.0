package com.dink3.plaid;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.plaid.service.PlaidDataService;
import com.dink3.plaid.service.SyncNotAllowedException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@Tag(name = "Plaid", description = "Plaid integration for account linking and data syncing")
@RestController
@RequestMapping("/api/v1/plaid")
@SecurityRequirement(name = "bearerAuth")
public class PlaidController {
    private static final Logger log = LoggerFactory.getLogger(PlaidController.class);
    
    private final PlaidService plaidService;
    private final PlaidDataService plaidDataService;
    
    public PlaidController(PlaidService plaidService, PlaidDataService plaidDataService) {
        this.plaidService = plaidService;
        this.plaidDataService = plaidDataService;
    }
    
    /**
     * Create a Link token for the user to connect their bank account
     */
    @PostMapping("/link-token")
    public ResponseEntity<?> createLinkToken(@AuthenticationPrincipal User user) {
        log.info("Creating link token for user: {}", user.getId());
        String linkToken = plaidService.createLinkToken(user);
        
        if (linkToken != null) {
            return ResponseEntity.ok(Map.of("linkToken", linkToken));
        } else {
            return ResponseEntity.status(500).body("Failed to create link token");
        }
    }
    
    /**
     * Exchange public token for access token and link the account
     */
    @PostMapping("/exchange-token")
    public ResponseEntity<?> exchangePublicToken(@Valid @RequestBody ExchangeTokenRequest request, 
                                                @AuthenticationPrincipal User user) {
        log.info("Exchanging public token for user: {}", user.getId());
        boolean success = plaidService.exchangePublicToken(request.publicToken, user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Account linked successfully"));
        } else {
            return ResponseEntity.status(500).body("Failed to link account");
        }
    }
    
    /**
     * Manual sync endpoint for users to trigger data synchronization
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncData(@AuthenticationPrincipal User user) {
        log.info("Manual sync requested for user: {}", user.getId());
        
        try {
            plaidDataService.syncUserData(user);
            return ResponseEntity.ok(Map.of("message", "Data sync completed successfully"));
        } catch (SyncNotAllowedException e) {
            log.warn("Sync not allowed for user: {}: {}", user.getId(), e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error syncing data for user: {}", user.getId(), e);
            return ResponseEntity.status(500).body("Failed to sync data");
        }
    }
    
    /**
     * Webhook endpoint for receiving Plaid webhooks
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody String webhookData) {
        log.info("Received webhook: {}", webhookData);
        
        // TODO: Implement webhook verification and processing
        // For now, just acknowledge receipt
        return ResponseEntity.ok().build();
    }
} 