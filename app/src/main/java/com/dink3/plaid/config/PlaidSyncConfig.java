package com.dink3.plaid.config;

import org.springframework.context.annotation.Configuration;
import java.util.Set;

@Configuration
public class PlaidSyncConfig {
    
    /**
     * Fields that should be updated from Plaid for accounts
     */
    public static final Set<String> UPDATEABLE_ACCOUNT_FIELDS = Set.of(
        "name", "mask", "officialName", "type", "subtype", 
        "currentBalance", "availableBalance", "isoCurrencyCode", "unofficialCurrencyCode"
    );
    
    /**
     * Fields that should be updated from Plaid for institutions
     */
    public static final Set<String> UPDATEABLE_INSTITUTION_FIELDS = Set.of(
        "name", "logo", "primaryColor", "url"
    );
    
    /**
     * Fields that should be updated from Plaid for transactions
     */
    public static final Set<String> UPDATEABLE_TRANSACTION_FIELDS = Set.of(
        "amount", "isoCurrencyCode", "unofficialCurrencyCode", "date", "datetime",
        "name", "paymentChannel", "pending", "pendingTransactionId", "accountOwner",
        "merchantName", "merchantCategoryId", "merchantCategory"
    );
    
    /**
     * Fields that should be updated from Plaid for PlaidItems
     */
    public static final Set<String> UPDATEABLE_PLAID_ITEM_FIELDS = Set.of(
        "plaidAccessToken", "plaidInstitutionId", "status", "lastWebhook"
    );
} 