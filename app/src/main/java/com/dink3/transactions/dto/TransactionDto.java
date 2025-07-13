package com.dink3.transactions.dto;

import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.TransactionLocation;
import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.jooq.Record;

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

    /**
     * Convert to Transaction entity (for new transactions)
     */
    public Transaction toTransaction() {
        Transaction transaction = new Transaction();
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

    public static TransactionDto fromTransaction(Transaction transaction) {
        return TransactionDto.builder()
            .id(transaction.getId())
            .plaidAccountId(transaction.getPlaidAccountId())
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
            .build();
    }

    public static TransactionDto fromRecord(Record record) {
        // Map to POJOs using JOOQ
        Transaction transaction = record.into(Transaction.class);

        return TransactionDto.fromTransaction(transaction);
    }
}
