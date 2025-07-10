package com.dink3.transactions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionPaymentMetaDto {
    private String id;
    private String transactionId;
    private String referenceNumber;
    private String payer;
    private String paymentMethod;
    private String paymentProcessor;
    private String ppdId;
    private String reason;
    private String byOrderOf;
    private String payee;
} 