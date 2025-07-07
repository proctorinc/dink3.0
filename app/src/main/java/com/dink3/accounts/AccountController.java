package com.dink3.accounts;

import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.Accounts;
import com.dink3.plaid.service.PlaidDataService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accounts", description = "Account operations")
@RestController
@RequestMapping("/api/v1/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    
    private final PlaidDataService plaidDataService;
    
    public AccountController(PlaidDataService plaidDataService) {
        this.plaidDataService = plaidDataService;
    }
    
    /**
     * Get all accounts for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<Accounts>> getAccounts(@AuthenticationPrincipal Users user) {
        if (user == null) {
            log.warn("Unauthorized access attempt to get accounts");
            return ResponseEntity.status(401).build();
        }
        
        log.info("Getting accounts for user: {}", user.getId());
        List<Accounts> accounts = plaidDataService.getUserAccounts(user);
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * Get a specific account by ID
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Accounts> getAccount(@PathVariable String accountId, 
                                              @AuthenticationPrincipal Users user) {
        if (user == null) {
            log.warn("Unauthorized access attempt to get account");
            return ResponseEntity.status(401).build();
        }
        
        log.info("Getting account {} for user: {}", accountId, user.getId());
        
        return plaidDataService.getAccountById(accountId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 