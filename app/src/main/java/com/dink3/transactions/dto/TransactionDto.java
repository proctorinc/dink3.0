package com.dink3.transactions.dto;

import com.dink3.transactions.dto.TransactionLocationDto;
import com.dink3.transactions.dto.TransactionPaymentMetaDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDto {
    private String id;
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
}
