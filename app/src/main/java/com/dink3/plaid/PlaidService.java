package com.dink3.plaid;

import com.dink3.jooq.tables.pojos.Account;
import com.dink3.jooq.tables.pojos.Institution;
import com.dink3.jooq.tables.pojos.PlaidItem;
import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.TransactionLocation;
import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import com.dink3.transactions.dto.TransactionDto;
import com.dink3.transactions.repositories.TransactionLocationRepository;
import com.dink3.transactions.repositories.TransactionPaymentMetaRepository;
import com.dink3.transactions.repositories.TransactionRepository;
import com.dink3.transactions.dto.*;
import com.dink3.accounts.dto.AccountDto;
import com.dink3.institutions.dto.InstitutionDto;
import com.dink3.plaid.dto.PlaidItemDto;
import com.dink3.jooq.tables.pojos.User;
import com.dink3.institutions.InstitutionRepository;
import com.dink3.accounts.AccountRepository;
import com.dink3.util.UuidGenerator;
import com.plaid.client.ApiClient;
import com.plaid.client.model.*;
import com.plaid.client.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.dink3.plaid.mapper.PlaidTransactionMapper;
import com.dink3.plaid.repository.PlaidItemRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlaidService {
    private static final Logger log = LoggerFactory.getLogger(PlaidService.class);
    
    private final PlaidApi plaidApi;
    private final PlaidItemRepository plaidItemRepository;
    private final InstitutionRepository institutionRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLocationRepository transactionLocationRepository;
    private final TransactionPaymentMetaRepository transactionPaymentMetaRepository;
    
    @Value("${plaid.webhook-url}")
    private String webhookUrl;
    @Value("${plaid.environment}")
    private String plaidEnv;
    
    public PlaidService(ApiClient plaidClient, 
                       PlaidItemRepository plaidItemRepository,
                       InstitutionRepository institutionRepository,
                       AccountRepository accountRepository,
                       TransactionRepository transactionRepository,
                       TransactionLocationRepository transactionLocationRepository,
                       TransactionPaymentMetaRepository transactionPaymentMetaRepository) {
        // Use the properly configured ApiClient to create PlaidApi
        this.plaidApi = plaidClient.createService(PlaidApi.class);
        this.plaidItemRepository = plaidItemRepository;
        this.institutionRepository = institutionRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionLocationRepository = transactionLocationRepository;
        this.transactionPaymentMetaRepository = transactionPaymentMetaRepository;
    }
    
    /**
     * Create a Link token for the user to connect their bank account
     */
    public String createLinkToken(User user) {
        try {
            LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .user(new LinkTokenCreateRequestUser()
                    .clientUserId(user.getId().toString()))
                .clientName("Dink3")
                .products(List.of(Products.TRANSACTIONS))
                .countryCodes(List.of(CountryCode.US))
                .language("en")
                .webhook(webhookUrl);
            var responseRaw = plaidApi.linkTokenCreate(request).execute();
            LinkTokenCreateResponse response = responseRaw.body();
            if (response != null && response.getLinkToken() != null) {
                log.info("Created link token for user: {}", user.getId());
                return response.getLinkToken();
            }
        } catch (Exception e) {
            log.error("Error creating link token for user: {}", user.getId(), e);
        }
        return null;
    }
    
    /**
     * Get item information including institution ID
     */
    private String getInstitutionIdFromItem(String accessToken) {
        try {
            ItemGetRequest request = new ItemGetRequest()
                .accessToken(accessToken);
            var responseRaw = plaidApi.itemGet(request).execute();
            ItemGetResponse response = responseRaw.body();

            if (response != null && response.getItem() != null) {
                return response.getItem().getInstitutionId();
            }
        } catch (Exception e) {
            log.error("Error getting item information", e);
        }
        return null;
    }

    /**
     * Exchange public token for access token and store the item
     */
    public boolean exchangePublicToken(String publicToken, User user) {
        try {
            ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);
            var responseRaw = plaidApi.itemPublicTokenExchange(request).execute();
            ItemPublicTokenExchangeResponse response = responseRaw.body();
            if (response != null && response.getAccessToken() != null) {
                // Get institution ID from the item
                String institutionId = getInstitutionIdFromItem(response.getAccessToken());
                
                // Check if item already exists
                Optional<PlaidItem> existing = plaidItemRepository.findByPlaidItemId(response.getItemId());
                PlaidItem itemToSync;
                
                if (existing.isPresent()) {
                    // Update existing item - preserve ID and user-specific fields
                    PlaidItemDto itemDto = PlaidItemDto.fromPlaidItem(response.getItemId(), response.getAccessToken(), user.getId(), institutionId != null ? institutionId : "");
                    itemDto.updateExistingPlaidItem(existing.get());
                    plaidItemRepository.update(existing.get());
                    itemToSync = existing.get();
                } else {
                    // Create new item
                    PlaidItemDto itemDto = PlaidItemDto.fromPlaidItem(response.getItemId(), response.getAccessToken(), user.getId(), institutionId != null ? institutionId : "");
                    itemDto.setId(UuidGenerator.generateUuid());
                    itemDto.setCreatedAt(LocalDateTime.now().toString());
                    itemDto.setUpdatedAt(LocalDateTime.now().toString());
                    PlaidItem newItem = itemDto.toPlaidItem();
                    plaidItemRepository.save(newItem);
                    itemToSync = newItem;
                }
                
                // Fetch and store initial data
                syncItemData(itemToSync);
                
                log.info("Successfully exchanged public token for user: {}", user.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("Error exchanging public token for user: {}", user.getId(), e);
        }
        return false;
    }
    
    /**
     * Sync all data for a specific item (accounts, transactions, institution)
     */
    public void syncItemData(PlaidItem item) {
        try {
            // Get accounts
            AccountsGetRequest accountsRequest = new AccountsGetRequest()
                .accessToken(item.getPlaidAccessToken());
            var responseRaw = plaidApi.accountsGet(accountsRequest).execute();
            AccountsGetResponse accountsResponse = responseRaw.body();
            if (accountsResponse != null && accountsResponse.getAccounts() != null) {
                
                // Sync institution if we have the institution ID
                String institutionId = getInstitutionIdFromItem(item.getPlaidAccessToken());
                if (institutionId != null && !institutionId.isEmpty()) {
                    syncInstitution(institutionId);
                } else {
                    log.warn("No institution ID available for item: {}", item.getPlaidItemId());
                }
                
                // Store accounts
                for (com.plaid.client.model.AccountBase plaidAccount : accountsResponse.getAccounts()) {
                    storeAccount(plaidAccount, item.getPlaidItemId(), item.getUserId());
                }
                
                // Fetch transactions for each account
                for (com.plaid.client.model.AccountBase plaidAccount : accountsResponse.getAccounts()) {
                    syncTransactions(plaidAccount.getAccountId(), item.getPlaidAccessToken(), item.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("Error syncing item data for item: {}", item.getPlaidItemId(), e);
        }
    }
    
    /**
     * Sync transactions for a specific account
     */
    private void syncTransactions(String accountId, String accessToken, String userId) {
        try {
            // Get transactions for the last 30 days
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusDays(30);
            
            TransactionsGetRequest request = new TransactionsGetRequest()
                .accessToken(accessToken)
                .startDate(startDate.toLocalDate())
                .endDate(endDate.toLocalDate())
                .options(new TransactionsGetRequestOptions()
                .accountIds(List.of(accountId)));
            var responseRaw = plaidApi.transactionsGet(request).execute();
            TransactionsGetResponse response = responseRaw.body();
            if (response != null && response.getTransactions() != null) {
                for (com.plaid.client.model.Transaction plaidTransaction : response.getTransactions()) {
                    if (plaidTransaction.getAccountId().equals(accountId)) {
                        storeTransaction(plaidTransaction, userId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error syncing transactions for account: {}", accountId, e);
        }
    }
    
    /**
     * Store an account in the database
     */
    private void storeAccount(AccountBase plaidAccount, String plaidItemId, String userId) {
        try {
            // Check if account already exists
            Optional<Account> existing = accountRepository.findByPlaidAccountId(plaidAccount.getAccountId());
            
            if (existing.isPresent()) {
                // Update existing account - preserve ID and user-specific fields
                AccountDto accountDto = AccountDto.fromPlaidAccount(plaidAccount, userId, plaidItemId);
                accountDto.updateExistingAccount(existing.get());
                accountRepository.update(existing.get());
            } else {
                // Create new account
                AccountDto accountDto = AccountDto.fromPlaidAccount(plaidAccount, userId, plaidItemId);
                accountDto.setId(UuidGenerator.generateUuid());
                accountDto.setCreatedAt(LocalDateTime.now().toString());
                accountDto.setUpdatedAt(LocalDateTime.now().toString());
                accountRepository.save(accountDto.toAccount());
            }
        } catch (Exception e) {
            log.error("Error storing account: {}", plaidAccount.getAccountId(), e);
        }
    }
    
    /**
     * Store a transaction in the database
     */
    private void storeTransaction(com.plaid.client.model.Transaction plaidTransaction, String userId) {
        try {
            // Check if transaction already exists
            Optional<Transaction> existing = transactionRepository.findByPlaidTransactionId(plaidTransaction.getTransactionId());
            
            if (existing.isPresent()) {
                // Update existing transaction - preserve ID and user-specific fields
                TransactionDto transactionDto = TransactionDto.fromPlaidTransaction(plaidTransaction, userId);
                transactionDto.updateExistingTransaction(existing.get());
                transactionRepository.update(existing.get());
            } else {
                // Create new transaction
                TransactionDto transactionDto = TransactionDto.fromPlaidTransaction(plaidTransaction, userId);
                transactionDto.setId(UuidGenerator.generateUuid());
                transactionDto.setCreatedAt(LocalDateTime.now().toString());
                transactionDto.setUpdatedAt(LocalDateTime.now().toString());
                transactionRepository.save(transactionDto.toTransaction());
            }

            // Store TransactionLocation if Plaid location exists
            TransactionLocation location = PlaidTransactionMapper.toTransactionLocation(plaidTransaction, existing.isPresent() ? existing.get().getId() : null);
            if (location != null) {
                transactionLocationRepository.upsert(location);
            }

            // Store TransactionPaymentMeta if Plaid payment meta exists
            TransactionPaymentMeta paymentMeta = PlaidTransactionMapper.toTransactionPaymentMeta(plaidTransaction, existing.isPresent() ? existing.get().getId() : null);
            if (paymentMeta != null) {
                transactionPaymentMetaRepository.upsert(paymentMeta);
            }
        } catch (Exception e) {
            log.error("Error storing transaction: {}", plaidTransaction.getTransactionId(), e);
        }
    }
    
    /**
     * Sync institution data
     */
    private void syncInstitution(String institutionId) {
        try {
            // Check if institution already exists
            Optional<Institution> existing = institutionRepository.findByPlaidInstitutionId(institutionId);
            
            InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest()
                .institutionId(institutionId)
                .countryCodes(List.of(CountryCode.US));
            var responseRaw = plaidApi.institutionsGetById(request).execute();
            InstitutionsGetByIdResponse response = responseRaw.body();
            if (response != null && response.getInstitution() != null) {
                com.plaid.client.model.Institution plaidInstitution = response.getInstitution();
                
                if (existing.isPresent()) {
                    // Update existing institution - preserve ID
                    InstitutionDto institutionDto = InstitutionDto.fromPlaidInstitution(plaidInstitution, institutionId);
                    institutionDto.updateExistingInstitution(existing.get());
                    institutionRepository.update(existing.get());
                } else {
                    // Create new institution
                    InstitutionDto institutionDto = InstitutionDto.fromPlaidInstitution(plaidInstitution, institutionId);
                    institutionDto.setId(UuidGenerator.generateUuid());
                    institutionDto.setCreatedAt(LocalDateTime.now().toString());
                    institutionDto.setUpdatedAt(LocalDateTime.now().toString());
                    institutionRepository.save(institutionDto.toInstitution());
                }
            }
        } catch (Exception e) {
            log.error("Error syncing institution: {}", institutionId, e);
        }
    }
} 