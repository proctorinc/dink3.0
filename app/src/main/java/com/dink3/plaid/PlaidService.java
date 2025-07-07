package com.dink3.plaid;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.*;
import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.PlaidItems;
import com.dink3.jooq.tables.pojos.Institutions;
import com.dink3.jooq.tables.pojos.Accounts;
import com.dink3.jooq.tables.pojos.Transactions;
import com.dink3.plaid.repository.PlaidItemRepository;
import com.dink3.institutions.InstitutionRepository;
import com.dink3.accounts.AccountRepository;
import com.dink3.transactions.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.dink3.util.UuidGenerator;

@Service
public class PlaidService {
    private static final Logger log = LoggerFactory.getLogger(PlaidService.class);
    
    private final PlaidApi plaidApi;
    private final PlaidItemRepository plaidItemRepository;
    private final InstitutionRepository institutionRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    @Value("${plaid.webhook-url}")
    private String webhookUrl;
    @Value("${plaid.environment}")
    private String plaidEnv;
    
    public PlaidService(ApiClient plaidClient, 
                       PlaidItemRepository plaidItemRepository,
                       InstitutionRepository institutionRepository,
                       AccountRepository accountRepository,
                       TransactionRepository transactionRepository) {
        // Use the properly configured ApiClient to create PlaidApi
        this.plaidApi = plaidClient.createService(PlaidApi.class);
        this.plaidItemRepository = plaidItemRepository;
        this.institutionRepository = institutionRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Create a Link token for the user to connect their bank account
     */
    public String createLinkToken(Users user) {
        try {
            LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .user(new LinkTokenCreateRequestUser()
                    .clientUserId(user.getId().toString()))
                .clientName("Dink3")
                .products(List.of(Products.TRANSACTIONS))
                .countryCodes(List.of(CountryCode.US))
                .language("en")
                .webhook(webhookUrl);
            
            LinkTokenCreateResponse response = plaidApi.linkTokenCreate(request).execute().body();
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
    public boolean exchangePublicToken(String publicToken, Users user) {
        try {
            ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .publicToken(publicToken);
            
            ItemPublicTokenExchangeResponse response = plaidApi.itemPublicTokenExchange(request).execute().body();
            if (response != null && response.getAccessToken() != null) {
                // Store the item
                PlaidItems item = new PlaidItems();
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
    public void syncItemData(PlaidItems item) {
        try {
            // Get accounts
            AccountsGetRequest accountsRequest = new AccountsGetRequest()
                .accessToken(item.getPlaidAccessToken());
            
            AccountsGetResponse accountsResponse = plaidApi.accountsGet(accountsRequest).execute().body();
            if (accountsResponse != null && accountsResponse.getAccounts() != null) {
                // Update institution ID from the item (PlaidItems) if available
                String institutionId = item.getPlaidInstitutionId();
                if (institutionId != null && !institutionId.isEmpty()) {
                    syncInstitution(institutionId);
                }
                
                // Store accounts
                for (AccountBase account : accountsResponse.getAccounts()) {
                    storeAccount(account, item.getPlaidItemId());
                }
                
                // Fetch transactions for each account
                for (AccountBase account : accountsResponse.getAccounts()) {
                    syncTransactions(account.getAccountId(), item.getPlaidAccessToken());
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
            
            TransactionsGetResponse response = plaidApi.transactionsGet(request).execute().body();
            if (response != null && response.getTransactions() != null) {
                for (Transaction transaction : response.getTransactions()) {
                    if (transaction.getAccountId().equals(accountId)) {
                        storeTransaction(transaction);
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
            Accounts account = new Accounts();
            account.setId(UuidGenerator.generateUuid());
            account.setPlaidItemId(plaidItemId);
            account.setPlaidAccountId(plaidAccount.getAccountId());
            account.setName(plaidAccount.getName());
            account.setMask(plaidAccount.getMask());
            account.setOfficialName(plaidAccount.getOfficialName());
            account.setType(plaidAccount.getType().getValue());
            account.setSubtype(plaidAccount.getSubtype() != null ? plaidAccount.getSubtype().getValue() : null);
            
            // Handle balance conversion from Double to Float
            if (plaidAccount.getBalances().getCurrent() != null) {
                account.setCurrentBalance(plaidAccount.getBalances().getCurrent().floatValue());
            }
            if (plaidAccount.getBalances().getAvailable() != null) {
                account.setAvailableBalance(plaidAccount.getBalances().getAvailable().floatValue());
            }
            
            account.setIsoCurrencyCode(plaidAccount.getBalances().getIsoCurrencyCode());
            account.setUnofficialCurrencyCode(plaidAccount.getBalances().getUnofficialCurrencyCode());
            account.setCreatedAt(LocalDateTime.now().toString());
            account.setUpdatedAt(LocalDateTime.now().toString());
            
            accountRepository.saveOrUpdate(account);
        } catch (Exception e) {
            log.error("Error storing account: {}", plaidAccount.getAccountId(), e);
        }
    }
    
    /**
     * Store a transaction in the database
     */
    private void storeTransaction(Transaction plaidTransaction) {
        try {
            // Check if transaction already exists
            Optional<Transactions> existing = transactionRepository.findByPlaidTransactionId(plaidTransaction.getTransactionId());
            if (existing.isPresent()) {
                // Update existing transaction
                Transactions transaction = existing.get();
                updateTransactionFromPlaid(transaction, plaidTransaction);
                transactionRepository.update(transaction);
            } else {
                // Create new transaction
                Transactions transaction = new Transactions();
                transaction.setId(UuidGenerator.generateUuid());
                transaction.setPlaidTransactionId(plaidTransaction.getTransactionId());
                transaction.setPlaidAccountId(plaidTransaction.getAccountId());
                transaction.setAmount(plaidTransaction.getAmount().floatValue());
                transaction.setIsoCurrencyCode(plaidTransaction.getIsoCurrencyCode());
                transaction.setUnofficialCurrencyCode(plaidTransaction.getUnofficialCurrencyCode());
                transaction.setDate(plaidTransaction.getDate().toString());
                transaction.setDatetime(plaidTransaction.getDatetime() != null ? plaidTransaction.getDatetime().toString() : null);
                transaction.setName(plaidTransaction.getName());
                transaction.setMerchantName(plaidTransaction.getMerchantName());
                transaction.setPaymentChannel(plaidTransaction.getPaymentChannel() != null ? plaidTransaction.getPaymentChannel().getValue() : null);
                transaction.setPending(plaidTransaction.getPending());
                transaction.setPendingTransactionId(plaidTransaction.getPendingTransactionId());
                transaction.setAccountOwner(plaidTransaction.getAccountOwner());
                transaction.setCategoryId(plaidTransaction.getCategoryId());
                transaction.setCategory(plaidTransaction.getCategory() != null ? String.join(", ", plaidTransaction.getCategory()) : null);
                
                // Location data
                if (plaidTransaction.getLocation() != null) {
                    transaction.setLocationAddress(plaidTransaction.getLocation().getAddress());
                    transaction.setLocationCity(plaidTransaction.getLocation().getCity());
                    transaction.setLocationRegion(plaidTransaction.getLocation().getRegion());
                    transaction.setLocationPostalCode(plaidTransaction.getLocation().getPostalCode());
                    transaction.setLocationCountry(plaidTransaction.getLocation().getCountry());
                    if (plaidTransaction.getLocation().getLat() != null) {
                        transaction.setLocationLat(plaidTransaction.getLocation().getLat().floatValue());
                    }
                    if (plaidTransaction.getLocation().getLon() != null) {
                        transaction.setLocationLon(plaidTransaction.getLocation().getLon().floatValue());
                    }
                }
                
                // Payment metadata
                if (plaidTransaction.getPaymentMeta() != null) {
                    transaction.setPaymentMetaReferenceNumber(plaidTransaction.getPaymentMeta().getReferenceNumber());
                    transaction.setPaymentMetaPayer(plaidTransaction.getPaymentMeta().getPayer());
                    transaction.setPaymentMetaPaymentMethod(plaidTransaction.getPaymentMeta().getPaymentMethod());
                    transaction.setPaymentMetaPaymentProcessor(plaidTransaction.getPaymentMeta().getPaymentProcessor());
                    transaction.setPaymentMetaPpdId(plaidTransaction.getPaymentMeta().getPpdId());
                    transaction.setPaymentMetaReason(plaidTransaction.getPaymentMeta().getReason());
                    transaction.setPaymentMetaByOrderOf(plaidTransaction.getPaymentMeta().getByOrderOf());
                    transaction.setPaymentMetaPayee(plaidTransaction.getPaymentMeta().getPayee());
                }
                
                transaction.setCreatedAt(LocalDateTime.now().toString());
                transaction.setUpdatedAt(LocalDateTime.now().toString());
                
                transactionRepository.save(transaction);
            }
        } catch (Exception e) {
            log.error("Error storing transaction: {}", plaidTransaction.getTransactionId(), e);
        }
    }
    
    /**
     * Update existing transaction with new data from Plaid
     */
    private void updateTransactionFromPlaid(Transactions transaction, Transaction plaidTransaction) {
        transaction.setAmount(plaidTransaction.getAmount().floatValue());
        transaction.setPending(plaidTransaction.getPending());
        transaction.setUpdatedAt(LocalDateTime.now().toString());
    }
    
    /**
     * Sync institution data
     */
    private void syncInstitution(String institutionId) {
        try {
            // Check if institution already exists
            Optional<Institutions> existing = institutionRepository.findByPlaidInstitutionId(institutionId);
            if (existing.isPresent()) {
                return; // Already exists
            }
            
            InstitutionsGetByIdRequest request = new InstitutionsGetByIdRequest()
                .institutionId(institutionId)
                .countryCodes(List.of(CountryCode.US));
            
            InstitutionsGetByIdResponse response = plaidApi.institutionsGetById(request).execute().body();
            if (response != null && response.getInstitution() != null) {
                Institution plaidInstitution = response.getInstitution();
                
                Institutions institution = new Institutions();
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