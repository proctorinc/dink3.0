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
            log.info("Calling Plaid: linkTokenCreate request={}", request);
            var responseRaw = plaidApi.linkTokenCreate(request).execute();
            log.info("Plaid linkTokenCreate response: {}", responseRaw);
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
     * Exchange public token for access token and store the item
     */
    public boolean exchangePublicToken(String publicToken, User user) {
        try {
            ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);
            log.info("Calling Plaid: itemPublicTokenExchange request={}", request);
            var responseRaw = plaidApi.itemPublicTokenExchange(request).execute();
            log.info("Plaid itemPublicTokenExchange response: status={} body={}", responseRaw.code(), responseRaw.body());
            ItemPublicTokenExchangeResponse response = responseRaw.body();
            if (response != null && response.getAccessToken() != null) {
                // Store the item
                PlaidItem item = new PlaidItem();
                item.setId(UuidGenerator.generateUuid());
                item.setUserId(user.getId());
                item.setPlaidItemId(response.getItemId());
                item.setPlaidAccessToken(response.getAccessToken());
                item.setPlaidInstitutionId(""); // Will be updated when we fetch accounts
                item.setStatus("good");
                item.setCreatedAt(LocalDateTime.now().toString());
                item.setUpdatedAt(LocalDateTime.now().toString());
                
                plaidItemRepository.save(item);
                
                // Fetch and store initial data
                syncItemData(item);
                
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
            log.info("Calling Plaid: accountsGet request={}", accountsRequest);
            var responseRaw = plaidApi.accountsGet(accountsRequest).execute();
            log.info("Plaid accountsGet response: status={} body={}", responseRaw.code(), responseRaw.body());
            AccountsGetResponse accountsResponse = responseRaw.body();
            if (accountsResponse != null && accountsResponse.getAccounts() != null) {
                // Update institution ID from the item (PlaidItem) if available
                String institutionId = item.getPlaidInstitutionId();
                if (institutionId != null && !institutionId.isEmpty()) {
                    syncInstitution(institutionId);
                }
                
                // Store accounts
                for (com.plaid.client.model.AccountBase plaidAccount : accountsResponse.getAccounts()) {
                    storeAccount(plaidAccount, item.getPlaidItemId());
                }
                
                // Fetch transactions for each account
                for (com.plaid.client.model.AccountBase plaidAccount : accountsResponse.getAccounts()) {
                    syncTransactions(plaidAccount.getAccountId(), item.getPlaidAccessToken());
                }
            }
        } catch (Exception e) {
            log.error("Error syncing item data for item: {}", item.getPlaidItemId(), e);
        }
    }
    
    /**
     * Sync transactions for a specific account
     */
    private void syncTransactions(String accountId, String accessToken) {
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
            log.info("Calling Plaid: transactionsGet request={}", request);
            var responseRaw = plaidApi.transactionsGet(request).execute();
            log.info("Plaid transactionsGet response: status={} body={}", responseRaw.code(), responseRaw.body());
            TransactionsGetResponse response = responseRaw.body();
            if (response != null && response.getTransactions() != null) {
                for (com.plaid.client.model.Transaction plaidTransaction : response.getTransactions()) {
                    if (plaidTransaction.getAccountId().equals(accountId)) {
                        storeTransaction(plaidTransaction);
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
    private void storeAccount(AccountBase plaidAccount, String plaidItemId) {
        try {
            Account account = new Account();
            account.setId(UuidGenerator.generateUuid());
            account.setPlaidAccountId(plaidAccount.getAccountId());
            account.setPlaidItemId(plaidItemId);
            account.setName(plaidAccount.getName());
            account.setMask(plaidAccount.getMask());
            account.setOfficialName(plaidAccount.getOfficialName());
            account.setType(plaidAccount.getType() != null ? plaidAccount.getType().getValue() : null);
            account.setSubtype(plaidAccount.getSubtype() != null ? plaidAccount.getSubtype().getValue() : null);
            account.setIsoCurrencyCode(plaidAccount.getBalances().getIsoCurrencyCode());
            account.setUnofficialCurrencyCode(plaidAccount.getBalances().getUnofficialCurrencyCode());
            account.setAvailableBalance(plaidAccount.getBalances().getAvailable() != null ? plaidAccount.getBalances().getAvailable().floatValue() : null);
            account.setCurrentBalance(plaidAccount.getBalances().getCurrent() != null ? plaidAccount.getBalances().getCurrent().floatValue() : null);
            // account.setBalanceLimit(plaidAccount.getBalances().getLimit());
            
            accountRepository.save(account);
            
        } catch (Exception e) {
            log.error("Error storing account: {}", plaidAccount.getAccountId(), e);
        }
    }
    
    /**
     * Store a transaction in the database
     */
    private TransactionDto storeTransaction(com.plaid.client.model.Transaction plaidTransaction) {
        try {
            // Check if transaction already exists
            Optional<Transaction> existing = transactionRepository.findByPlaidTransactionId(plaidTransaction.getTransactionId());
            Transaction transaction;
            if (existing.isPresent()) {
                transaction = PlaidTransactionMapper.toTransaction(plaidTransaction, existing.get().getId());
            } else {
                transaction = PlaidTransactionMapper.toTransaction(plaidTransaction, null);
            }
            transactionRepository.save(transaction);

            // Store TransactionLocation if Plaid location exists
            TransactionLocation location = PlaidTransactionMapper.toTransactionLocation(plaidTransaction, transaction.getId());
            if (location != null) {
                transactionLocationRepository.save(location);
            }

            // Store TransactionPaymentMeta if Plaid payment meta exists
            TransactionPaymentMeta paymentMeta = PlaidTransactionMapper.toTransactionPaymentMeta(plaidTransaction, transaction.getId());
            if (paymentMeta != null) {
                transactionPaymentMetaRepository.save(paymentMeta);
            }

            // Build DTOs
            TransactionLocationDto locationDto = location != null ? TransactionLocationDto.builder()
                .id(location.getId())
                .transactionId(location.getTransactionId())
                .address(location.getAddress())
                .city(location.getCity())
                .region(location.getRegion())
                .postalCode(location.getPostalCode())
                .country(location.getCountry())
                .lat(location.getLat())
                .lon(location.getLon())
                .build() : null;

            TransactionPaymentMetaDto paymentMetaDto = paymentMeta != null ? TransactionPaymentMetaDto.builder()
                .id(paymentMeta.getId())
                .transactionId(paymentMeta.getTransactionId())
                .referenceNumber(paymentMeta.getReferenceNumber())
                .payer(paymentMeta.getPayer())
                .paymentMethod(paymentMeta.getPaymentMethod())
                .paymentProcessor(paymentMeta.getPaymentProcessor())
                .ppdId(paymentMeta.getPpdId())
                .reason(paymentMeta.getReason())
                .byOrderOf(paymentMeta.getByOrderOf())
                .payee(paymentMeta.getPayee())
                .build() : null;

            return TransactionDto.builder()
                .id(transaction.getId())
                .plaidTransactionId(transaction.getPlaidTransactionId())
                .plaidAccountId(transaction.getPlaidAccountId())
                .categoryId(transaction.getCategoryId())
                .amount(transaction.getAmount())
                .isoCurrencyCode(transaction.getIsoCurrencyCode())
                .unofficialCurrencyCode(transaction.getUnofficialCurrencyCode())
                .date(transaction.getDate())
                .datetime(transaction.getDatetime())
                .name(transaction.getName())
                .paymentChannel(transaction.getPaymentChannel())
                .pending(transaction.getPending())
                .pendingTransactionId(transaction.getPendingTransactionId())
                .accountOwner(transaction.getAccountOwner())
                .merchantName(transaction.getMerchantName())
                .merchantCategoryId(transaction.getMerchantCategoryId())
                .merchantCategory(transaction.getMerchantCategory())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .location(locationDto)
                .paymentMeta(paymentMetaDto)
                .build();
        } catch (Exception e) {
            log.error("Error storing transaction: {}", plaidTransaction.getTransactionId(), e);
            return null;
        }
    }

    /**
     * Update existing transaction with new data from Plaid
     */
    private void updateTransactionFromPlaid(Transaction transaction, com.plaid.client.model.Transaction plaidTransaction) {
        Transaction updated = PlaidTransactionMapper.toTransaction(plaidTransaction, transaction.getId());
        transactionRepository.update(updated);
    }
    
    /**
     * Sync institution data
     */
    private void syncInstitution(String institutionId) {
        try {
            // Check if institution already exists
            Optional<Institution> existing = institutionRepository.findByPlaidInstitutionId(institutionId);
            if (existing.isPresent()) {
                return; // Already exists
            }
            
            InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest()
                .institutionId(institutionId)
                .countryCodes(List.of(CountryCode.US));
            log.info("Calling Plaid: institutionsGetById request={}", request);
            var responseRaw = plaidApi.institutionsGetById(request).execute();
            log.info("Plaid institutionsGetById response: status={} body={}", responseRaw.code(), responseRaw.body());
            InstitutionsGetByIdResponse response = responseRaw.body();
            if (response != null && response.getInstitution() != null) {
                com.plaid.client.model.Institution plaidInstitution = response.getInstitution();
                
                Institution institution = new Institution();
                institution.setId(UuidGenerator.generateUuid());
                institution.setPlaidInstitutionId(institutionId);
                institution.setName(plaidInstitution.getName());
                institution.setLogo(plaidInstitution.getLogo());
                institution.setPrimaryColor(plaidInstitution.getPrimaryColor());
                institution.setUrl(plaidInstitution.getUrl());
                institution.setCreatedAt(LocalDateTime.now().toString());
                institution.setUpdatedAt(LocalDateTime.now().toString());
                
                institutionRepository.save(institution);
            }
        } catch (Exception e) {
            log.error("Error syncing institution: {}", institutionId, e);
        }
    }
} 