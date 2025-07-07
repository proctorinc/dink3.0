package com.dink3.plaid.service;

import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.PlaidItems;
import com.dink3.jooq.tables.pojos.Institutions;
import com.dink3.jooq.tables.pojos.Accounts;
import com.dink3.jooq.tables.pojos.Transactions;
import com.dink3.jooq.tables.pojos.UserSubscriptions;
import com.dink3.plaid.repository.PlaidItemRepository;
import com.dink3.institutions.InstitutionRepository;
import com.dink3.accounts.AccountRepository;
import com.dink3.transactions.TransactionRepository;
import com.dink3.plaid.repository.UserSubscriptionRepository;
import com.dink3.plaid.PlaidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
    public void syncUserData(Users user) {
        UserSubscriptions subscription = getUserSubscription(user);
        
        // Check if user can sync based on their tier
        if (!canUserSync(subscription)) {
            throw new RuntimeException("Sync not available. Premium users can sync anytime. Basic users can sync once every 24 hours.");
        }
        
        // Get all Plaid items for the user
        List<PlaidItems> items = plaidItemRepository.findByUserId(user.getId());
        
        for (PlaidItems item : items) {
            plaidService.syncItemData(item);
        }
        
        // Update last sync time
        updateLastSyncTime(subscription);
        
        log.info("Completed data sync for user: {}", user.getId());
    }
    
    /**
     * Get all accounts for a user
     */
    public List<Accounts> getUserAccounts(Users user) {
        List<PlaidItems> items = plaidItemRepository.findByUserId(user.getId());
        return items.stream()
                .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                .toList();
    }
    
    /**
     * Get a specific account by ID
     */
    public Optional<Accounts> getAccountById(Integer accountId, Users user) {
        return accountRepository.findById(accountId)
                .filter(account -> {
                    // Verify the account belongs to the user
                    List<PlaidItems> userItems = plaidItemRepository.findByUserId(user.getId());
                    return userItems.stream()
                            .anyMatch(item -> item.getPlaidItemId().equals(account.getPlaidItemId()));
                });
    }
    
    /**
     * Get all transactions for a user
     */
    public List<Transactions> getUserTransactions(Users user) {
        List<PlaidItems> items = plaidItemRepository.findByUserId(user.getId());
        List<String> userAccountIds = items.stream()
                .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                .map(Accounts::getPlaidAccountId)
                .toList();
        
        return transactionRepository.findAll().stream()
                .filter(transaction -> userAccountIds.contains(transaction.getPlaidAccountId()))
                .toList();
    }
    
    /**
     * Get a specific transaction by ID
     */
    public Optional<Transactions> getTransactionById(Integer transactionId, Users user) {
        return transactionRepository.findById(transactionId)
                .filter(transaction -> {
                    // Verify the transaction belongs to the user
                    List<PlaidItems> userItems = plaidItemRepository.findByUserId(user.getId());
                    List<String> userAccountIds = userItems.stream()
                            .flatMap(item -> accountRepository.findByPlaidItemId(item.getPlaidItemId()).stream())
                            .map(Accounts::getPlaidAccountId)
                            .toList();
                    return userAccountIds.contains(transaction.getPlaidAccountId());
                });
    }
    
    /**
     * Get all institutions
     */
    public List<Institutions> getAllInstitutions() {
        return institutionRepository.findAll();
    }
    
    /**
     * Get a specific institution by ID
     */
    public Optional<Institutions> getInstitutionById(Integer institutionId) {
        return institutionRepository.findById(institutionId);
    }
    
    /**
     * Get user subscription or create default basic subscription
     */
    private UserSubscriptions getUserSubscription(Users user) {
        Optional<UserSubscriptions> existing = userSubscriptionRepository.findByUserId(user.getId());
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create default basic subscription
        UserSubscriptions subscription = new UserSubscriptions();
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
    private boolean canUserSync(UserSubscriptions subscription) {
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
    private void updateLastSyncTime(UserSubscriptions subscription) {
        subscription.setLastSyncAt(LocalDateTime.now().toString());
        subscription.setUpdatedAt(LocalDateTime.now().toString());
        userSubscriptionRepository.update(subscription);
    }
} 