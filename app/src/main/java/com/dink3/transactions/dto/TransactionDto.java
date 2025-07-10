package com.dink3.transactions.dto;

import com.dink3.transactions.dto.TransactionLocationDto;
import com.dink3.transactions.dto.TransactionPaymentMetaDto;
import com.plaid.client.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {
    private String id;
    private String userId;
    private String plaidTransactionId;
    private String plaidAccountId;
    private String categoryId;
    private Float amount;
    private String isoCurrencyCode;
    private String unofficialCurrencyCode;
    private String date;
    private String datetime;
    private String name;
    private String paymentChannel;
    private Boolean pending;
    private String pendingTransactionId;
    private String accountOwner;
    private String merchantName;
    private String merchantCategoryId;
    private String merchantCategory;
    private String createdAt;
    private String updatedAt;
    private TransactionLocationDto location;
    private TransactionPaymentMetaDto paymentMeta;
    
    /**
     * Convert from Plaid Transaction to TransactionDto
     */
    public static TransactionDto fromPlaidTransaction(Transaction plaidTransaction, String userId) {
        return TransactionDto.builder()
                .plaidTransactionId(plaidTransaction.getTransactionId())
                .userId(userId)
                .plaidAccountId(plaidTransaction.getAccountId())
                .categoryId(plaidTransaction.getCategoryId())
                .amount(plaidTransaction.getAmount() != null ? plaidTransaction.getAmount().floatValue() : null)
                .isoCurrencyCode(plaidTransaction.getIsoCurrencyCode())
                .unofficialCurrencyCode(plaidTransaction.getUnofficialCurrencyCode())
                .date(plaidTransaction.getDate() != null ? plaidTransaction.getDate().toString() : null)
                .datetime(plaidTransaction.getDatetime() != null ? plaidTransaction.getDatetime().toString() : null)
                .name(plaidTransaction.getName())
                .paymentChannel(plaidTransaction.getPaymentChannel() != null ? plaidTransaction.getPaymentChannel().getValue() : null)
                .pending(plaidTransaction.getPending())
                .pendingTransactionId(plaidTransaction.getPendingTransactionId())
                .accountOwner(plaidTransaction.getAccountOwner())
                .merchantName(plaidTransaction.getMerchantName())
                .merchantCategory(plaidTransaction.getCategory() != null ? String.join(", ", plaidTransaction.getCategory()) : null)
                .build();
    }
    
    /**
     * Update an existing Transaction entity with Plaid data (preserves ID and user-specific fields)
     */
    public void updateExistingTransaction(com.dink3.jooq.tables.pojos.Transaction existingTransaction) {
        existingTransaction.setPlaidAccountId(this.plaidAccountId);
        existingTransaction.setCategoryId(this.categoryId);
        existingTransaction.setAmount(this.amount);
        existingTransaction.setIsoCurrencyCode(this.isoCurrencyCode);
        existingTransaction.setUnofficialCurrencyCode(this.unofficialCurrencyCode);
        existingTransaction.setDate(this.date);
        existingTransaction.setDatetime(this.datetime);
        existingTransaction.setName(this.name);
        existingTransaction.setPaymentChannel(this.paymentChannel);
        existingTransaction.setPending(this.pending);
        existingTransaction.setPendingTransactionId(this.pendingTransactionId);
        existingTransaction.setAccountOwner(this.accountOwner);
        existingTransaction.setMerchantName(this.merchantName);
        existingTransaction.setMerchantCategoryId(this.merchantCategoryId);
        existingTransaction.setMerchantCategory(this.merchantCategory);
        existingTransaction.setUpdatedAt(LocalDateTime.now().toString());
    }
    
    /**
     * Convert to Transaction entity (for new transactions)
     */
    public com.dink3.jooq.tables.pojos.Transaction toTransaction() {
        com.dink3.jooq.tables.pojos.Transaction transaction = new com.dink3.jooq.tables.pojos.Transaction();
        transaction.setId(this.id);
        transaction.setUserId(this.userId);
        transaction.setPlaidTransactionId(this.plaidTransactionId);
        transaction.setPlaidAccountId(this.plaidAccountId);
        transaction.setCategoryId(this.categoryId);
        transaction.setAmount(this.amount);
        transaction.setIsoCurrencyCode(this.isoCurrencyCode);
        transaction.setUnofficialCurrencyCode(this.unofficialCurrencyCode);
        transaction.setDate(this.date);
        transaction.setDatetime(this.datetime);
        transaction.setName(this.name);
        transaction.setPaymentChannel(this.paymentChannel);
        transaction.setPending(this.pending);
        transaction.setPendingTransactionId(this.pendingTransactionId);
        transaction.setAccountOwner(this.accountOwner);
        transaction.setMerchantName(this.merchantName);
        transaction.setMerchantCategoryId(this.merchantCategoryId);
        transaction.setMerchantCategory(this.merchantCategory);
        transaction.setCreatedAt(this.createdAt);
        transaction.setUpdatedAt(this.updatedAt);
        return transaction;
    }
}
