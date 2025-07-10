package com.dink3.plaid.service;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.jooq.tables.pojos.PlaidItem;
import com.dink3.jooq.tables.pojos.Institution;
import com.dink3.jooq.tables.pojos.Account;
import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.UserSubscription;
import com.dink3.plaid.repository.PlaidItemRepository;
import com.dink3.institutions.InstitutionRepository;
import com.dink3.accounts.AccountRepository;
import com.dink3.plaid.repository.UserSubscriptionRepository;
import com.dink3.transactions.repositories.TransactionRepository;
import com.dink3.plaid.PlaidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import com.dink3.util.UuidGenerator;
import com.dink3.plaid.service.SyncNotAllowedException;

@Service
public class PlaidDataService {
    private static final Logger log = LoggerFactory.getLogger(PlaidDataService.class);
    
    private final PlaidService plaidService;
    private final PlaidItemRepository plaidItemRepository;
    private final InstitutionRepository institutionRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    
    public PlaidDataService(PlaidService plaidService,
                           PlaidItemRepository plaidItemRepository,
                           InstitutionRepository institutionRepository,
                           AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           UserSubscriptionRepository userSubscriptionRepository) {
        this.plaidService = plaidService;
        this.plaidItemRepository = plaidItemRepository;
        this.institutionRepository = institutionRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }
    
    /**
     * Sync all data for a user based on their subscription tier
     */
    public void syncUserData(User user) {
        UserSubscription subscription = getUserSubscription(user);
        
        // Check if user can sync based on their tier
        if (!canUserSync(subscription)) {
            throw new SyncNotAllowedException("Sync not available. Basic users can sync once every 24 hours. Upgrade to Premium to sync anytime.");
        }
        
        // Get all Plaid items for the user
        List<PlaidItem> items = plaidItemRepository.findByUserId(user.getId());
        
        for (PlaidItem item : items) {
            plaidService.syncItemData(item);
        }
        
        // Update last sync time
        updateLastSyncTime(subscription);
        
        log.info("Completed data sync for user: {}", user.getId());
    }
    
    /**
     * Get all accounts for a user
     */
    public List<Account> getUserAccounts(User user) {
        List<PlaidItem> items = plaidItemRepository.findByUserId(user.getId());
        return items.stream()
                .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                .toList();
    }
    
    /**
     * Get a specific account by ID
     */
    public Optional<Account> getAccountById(String accountId, User user) {
        return accountRepository.findById(accountId)
                .filter(account -> {
                    // Verify the account belongs to the user
                    List<PlaidItem> userItems = plaidItemRepository.findByUserId(user.getId());
                    return userItems.stream()
                            .anyMatch(item -> item.getPlaidItemId().equals(account.getPlaidItemId()));
                });
    }
    
    /**
     * Get all transactions for a user
     */
    public List<Transaction> getUserTransactions(User user) {
        List<PlaidItem> items = plaidItemRepository.findByUserId(user.getId());
        List<String> userAccountIds = items.stream()
                .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                .map(Account::getPlaidAccountId)
                .toList();
        
        return transactionRepository.findAll().stream()
                .filter(transaction -> userAccountIds.contains(transaction.getPlaidAccountId()))
                .toList();
    }
    
    /**
     * Get a specific transaction by ID
     */
    public Optional<Transaction> getTransactionById(String transactionId, User user) {
        return transactionRepository.findById(transactionId)
                .filter(transaction -> {
                    // Verify the transaction belongs to the user
                    List<PlaidItem> userItems = plaidItemRepository.findByUserId(user.getId());
                    List<String> userAccountIds = userItems.stream()
                            .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                            .map(Account::getPlaidAccountId)
                            .toList();
                    return userAccountIds.contains(transaction.getPlaidAccountId());
                });
    }
    
    /**
     * Get all institutions
     */
    public List<Institution> getAllInstitutions() {
        return institutionRepository.findAll();
    }
    
    /**
     * Get a specific institution by ID
     */
    public Optional<Institution> getInstitutionById(String institutionId) {
        return institutionRepository.findById(institutionId);
    }
    
    /**
     * Get user subscription or create default basic subscription
     */
    private UserSubscription getUserSubscription(User user) {
        Optional<UserSubscription> existing = userSubscriptionRepository.findByUserId(user.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create default basic subscription
        UserSubscription subscription = new UserSubscription();
        subscription.setId(UuidGenerator.generateUuid());
        subscription.setUserId(user.getId());
        subscription.setTier("basic");
        subscription.setCreatedAt(LocalDateTime.now().toString());
        subscription.setUpdatedAt(LocalDateTime.now().toString());
        userSubscriptionRepository.save(subscription);
        
        return subscription;
    }
    
    /**
     * Check if user can sync based on their subscription tier
     */
    private boolean canUserSync(UserSubscription subscription) {
        if ("premium".equals(subscription.getTier())) {
            return true; // Premium users can sync anytime
        }
        
        // Basic users can sync once every 24 hours
        if (subscription.getLastSyncAt() == null) {
            return true; // First time sync
        }
        
        LocalDateTime lastSync = LocalDateTime.parse(subscription.getLastSyncAt());
        LocalDateTime now = LocalDateTime.now();
        long hoursSinceLastSync = ChronoUnit.HOURS.between(lastSync, now);
        
        return hoursSinceLastSync >= 24;
    }
    
    /**
     * Update the last sync time for a user
     */
    private void updateLastSyncTime(UserSubscription subscription) {
        subscription.setLastSyncAt(LocalDateTime.now().toString());
        subscription.setUpdatedAt(LocalDateTime.now().toString());
        userSubscriptionRepository.update(subscription);
    }
} 