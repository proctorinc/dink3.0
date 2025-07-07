package com.dink3.transactions;

import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.Transactions;
import com.dink3.plaid.service.PlaidDataService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transactions", description = "Transaction operations")
@RestController
@RequestMapping("/api/v1/transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    
    private final PlaidDataService plaidDataService;
    
    public TransactionController(PlaidDataService plaidDataService) {
        this.plaidDataService = plaidDataService;
    }
    
    /**
     * Get all transactions for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<Transactions>> getTransactions(@AuthenticationPrincipal Users user) {
        if (user == null) {
            log.warn("Unauthorized access attempt to get transactions");
            return ResponseEntity.status(401).build();
        }
        
        log.info("Getting transactions for user: {}", user.getId());
        List<Transactions> transactions = plaidDataService.getUserTransactions(user);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get a specific transaction by ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transactions> getTransaction(@PathVariable Integer transactionId, 
                                                      @AuthenticationPrincipal Users user) {
        if (user == null) {
            log.warn("Unauthorized access attempt to get transaction");
            return ResponseEntity.status(401).build();
        }
        
        log.info("Getting transaction {} for user: {}", transactionId, user.getId());
        
        return plaidDataService.getTransactionById(transactionId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 