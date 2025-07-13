package com.dink3.transactions.dto;

import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.TransactionLocation;
import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.jooq.Record;

@Data
@Builder
public class TransactionFullDto {

    private String id;

    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String plaidTransactionId;

    @JsonIgnore
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
     * Get the transaction details.
     * @return Transaction object
     */
    public Transaction toTransaction() {
        return TransactionDto.builder()
            .id(id)
            .userId(userId)
            .plaidTransactionId(plaidTransactionId)
            .plaidAccountId(plaidAccountId)
            .categoryId(categoryId)
            .amount(amount)
            .isoCurrencyCode(isoCurrencyCode)
            .unofficialCurrencyCode(unofficialCurrencyCode)
            .date(date)
            .datetime(datetime)
            .name(name)
            .paymentChannel(paymentChannel)
            .pending(pending)
            .pendingTransactionId(pendingTransactionId)
            .accountOwner(accountOwner)
            .merchantName(merchantName)
            .merchantCategoryId(merchantCategoryId)
            .merchantCategory(merchantCategory)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build()
            .toTransaction();
    }

    /**
     * Update an existing Transaction entity with Plaid data (preserves ID and user-specific fields)
     *
     * @param existingTransaction The existing Transaction entity to update.
     */
    public TransactionFullDto updateExistingTransaction(
        TransactionFullDto existingTransaction
    ) {
        // Transaction
        existingTransaction.setPlaidAccountId(this.plaidAccountId);
        existingTransaction.setCategoryId(this.categoryId);
        existingTransaction.setAmount(this.amount);
        existingTransaction.setIsoCurrencyCode(this.isoCurrencyCode);
        existingTransaction.setUnofficialCurrencyCode(
            this.unofficialCurrencyCode
        );
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

        // TransactionLocation
        TransactionLocationDto location = existingTransaction.getLocation();
        location.setAddress(this.location.getAddress());
        location.setLat(this.location.getLat());
        location.setLon(this.location.getLon());
        location.setCity(this.location.getCity());
        location.setCountry(this.location.getCountry());
        location.setPostalCode(this.location.getPostalCode());
        location.setRegion(this.location.getRegion());

        existingTransaction.setLocation(location);

        // TransactionPaymentMeta
        TransactionPaymentMetaDto payment =
            existingTransaction.getPaymentMeta();
        payment.setReferenceNumber(this.paymentMeta.getReferenceNumber());
        payment.setPayer(this.paymentMeta.getPayer());
        payment.setPaymentMethod(this.paymentMeta.getPaymentMethod());
        payment.setPaymentProcessor(this.paymentMeta.getPaymentProcessor());
        payment.setPpdId(this.paymentMeta.getPpdId());
        payment.setReason(this.paymentMeta.getReason());
        payment.setByOrderOf(this.paymentMeta.getByOrderOf());
        payment.setPayee(this.paymentMeta.getPayee());

        existingTransaction.setPaymentMeta(payment);

        return existingTransaction;
    }

    /**
     * Creates a TransactionFullDto from a JOOQ Record.
     *
     * @param record The JOOQ Record to map to a TransactionFullDto.
     * @return The TransactionFullDto created from the Record.
     */
    public static TransactionFullDto createTransactionFullDto(
        Transaction transaction,
        TransactionLocation transactionLocation,
        TransactionPaymentMeta paymentMeta
    ) {
        TransactionLocationDto locationDto =
            TransactionLocationDto.fromLocation(transactionLocation);
        TransactionPaymentMetaDto paymentMetaDto =
            TransactionPaymentMetaDto.fromPaymentMeta(paymentMeta);

        return TransactionFullDto.builder()
            .id(transaction.getId())
            .plaidTransactionId(transaction.getPlaidTransactionId())
            .userId(transaction.getUserId())
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
            .merchantCategory(transaction.getMerchantCategory())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .location(locationDto)
            .paymentMeta(paymentMetaDto)
            .build();
    }

    /**
     * Creates a TransactionFullDto from a JOOQ Record.
     *
     * @param record The JOOQ Record to map to a TransactionFullDto.
     * @return The TransactionFullDto created from the Record.
     */
    public static TransactionFullDto fromRecord(Record record) {
        Transaction transaction = record.into(Transaction.class);
        TransactionLocation transactionLocation = record.into(
            TransactionLocation.class
        );
        TransactionPaymentMeta transactionPaymentMeta = record.into(
            TransactionPaymentMeta.class
        );

        return createTransactionFullDto(
            transaction,
            transactionLocation,
            transactionPaymentMeta
        );
    }
}
