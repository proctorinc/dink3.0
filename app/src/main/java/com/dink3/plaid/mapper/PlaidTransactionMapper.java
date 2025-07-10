package com.dink3.plaid.mapper;

import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.jooq.tables.pojos.TransactionLocation;
import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import com.dink3.util.UuidGenerator;
import java.time.LocalDateTime;

public class PlaidTransactionMapper {
    public static Transaction toTransaction(com.plaid.client.model.Transaction plaidTransaction, String idOverride) {
        Transaction transaction = new Transaction();
        transaction.setId(idOverride != null ? idOverride : UuidGenerator.generateUuid());
        transaction.setPlaidTransactionId(plaidTransaction.getTransactionId());
        transaction.setPlaidAccountId(plaidTransaction.getAccountId());
        transaction.setAmount(plaidTransaction.getAmount() != null ? plaidTransaction.getAmount().floatValue() : null);
        transaction.setPending(plaidTransaction.getPending());
        transaction.setName(plaidTransaction.getName());
        transaction.setDate(plaidTransaction.getDate() != null ? plaidTransaction.getDate().toString() : null);
        transaction.setDatetime(plaidTransaction.getDatetime() != null ? plaidTransaction.getDatetime().toString() : null);
        transaction.setIsoCurrencyCode(plaidTransaction.getIsoCurrencyCode());
        transaction.setUnofficialCurrencyCode(plaidTransaction.getUnofficialCurrencyCode());
        transaction.setPaymentChannel(plaidTransaction.getPaymentChannel() != null ? plaidTransaction.getPaymentChannel().getValue() : null);
        transaction.setPendingTransactionId(plaidTransaction.getPendingTransactionId());
        transaction.setAccountOwner(plaidTransaction.getAccountOwner());
        transaction.setMerchantName(plaidTransaction.getMerchantName());
        transaction.setCreatedAt(LocalDateTime.now().toString());
        transaction.setUpdatedAt(LocalDateTime.now().toString());
        if (plaidTransaction.getCategory() != null) {
            transaction.setMerchantCategory(String.join(", ", plaidTransaction.getCategory()));
        }
        // Do NOT set categoryId here; it should be set by custom logic elsewhere
        return transaction;
    }

    public static TransactionLocation toTransactionLocation(com.plaid.client.model.Transaction plaidTransaction, String transactionId) {
        if (plaidTransaction.getLocation() == null) return null;
        var loc = plaidTransaction.getLocation();
        TransactionLocation location = new TransactionLocation();
        location.setId(UuidGenerator.generateUuid());
        location.setTransactionId(transactionId);
        location.setAddress(loc.getAddress());
        location.setCity(loc.getCity());
        location.setRegion(loc.getRegion());
        location.setPostalCode(loc.getPostalCode());
        location.setCountry(loc.getCountry());
        location.setLat(loc.getLat() != null ? loc.getLat().floatValue() : null);
        location.setLon(loc.getLon() != null ? loc.getLon().floatValue() : null);
        return location;
    }

    public static TransactionPaymentMeta toTransactionPaymentMeta(com.plaid.client.model.Transaction plaidTransaction, String transactionId) {
        if (plaidTransaction.getPaymentMeta() == null) return null;
        var meta = plaidTransaction.getPaymentMeta();
        TransactionPaymentMeta paymentMeta = new TransactionPaymentMeta();
        paymentMeta.setId(UuidGenerator.generateUuid());
        paymentMeta.setTransactionId(transactionId);
        paymentMeta.setReferenceNumber(meta.getReferenceNumber());
        paymentMeta.setPayer(meta.getPayer());
        paymentMeta.setPaymentMethod(meta.getPaymentMethod());
        paymentMeta.setPaymentProcessor(meta.getPaymentProcessor());
        paymentMeta.setPpdId(meta.getPpdId());
        paymentMeta.setReason(meta.getReason());
        paymentMeta.setByOrderOf(meta.getByOrderOf());
        paymentMeta.setPayee(meta.getPayee());
        return paymentMeta;
    }
} 