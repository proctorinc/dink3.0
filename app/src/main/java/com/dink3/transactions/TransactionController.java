package com.dink3.transactions;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.jooq.tables.pojos.Transaction;
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
    public ResponseEntity<List<Transaction>> getTransactions(@AuthenticationPrincipal User user) {
        log.info("Getting transactions for user: {}", user.getId());
        List<Transaction> transactions = plaidDataService.getUserTransactions(user);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * Get a specific transaction by ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId, 
                                                      @AuthenticationPrincipal User user) {
        log.info("Getting transaction {} for user: {}", transactionId, user.getId());
        
        return plaidDataService.getTransactionById(transactionId, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 