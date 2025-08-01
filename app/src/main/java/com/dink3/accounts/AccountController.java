package com.dink3.accounts;

import com.dink3.jooq.tables.pojos.Account;
import com.dink3.jooq.tables.pojos.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Accounts", description = "Financial account operations")
@RestController
@RequestMapping("/api/v1/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(
        AccountController.class
    );

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Get all accounts for the authenticated user
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(
        @AuthenticationPrincipal User user
    ) {
        log.info("Getting accounts for user: {}", user.getId());
        List<Account> accounts = accountService.getAccountsByUserId(
            user.getId()
        );
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get a specific account by ID
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(
        @PathVariable String accountId,
        @AuthenticationPrincipal User user
    ) {
        log.info("Getting account {} for user: {}", accountId, user.getId());

        return accountService
            .getAccountByIdForUser(accountId, user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
