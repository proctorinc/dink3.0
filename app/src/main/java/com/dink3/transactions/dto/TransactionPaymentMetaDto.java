package com.dink3.transactions.dto;

import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import com.plaid.client.model.PaymentMeta;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionPaymentMetaDto {

    private String referenceNumber;
    private String payer;
    private String paymentMethod;
    private String paymentProcessor;
    private String ppdId;
    private String reason;
    private String byOrderOf;
    private String payee;

    public TransactionPaymentMeta toPaymentMeta() {
        TransactionPaymentMeta paymentMeta = new TransactionPaymentMeta();
        paymentMeta.setReferenceNumber(referenceNumber);
        paymentMeta.setPayer(payer);
        paymentMeta.setPaymentMethod(paymentMethod);
        paymentMeta.setPaymentProcessor(paymentProcessor);
        paymentMeta.setPpdId(ppdId);
        paymentMeta.setReason(reason);
        paymentMeta.setByOrderOf(byOrderOf);
        paymentMeta.setPayee(payee);
        return paymentMeta;
    }

    public static TransactionPaymentMetaDto fromPlaidPaymentMeta(
        PaymentMeta paymentMeta
    ) {
        return TransactionPaymentMetaDto.builder()
            .referenceNumber(paymentMeta.getReferenceNumber())
            .payer(paymentMeta.getPayer())
            .paymentMethod(paymentMeta.getPaymentMethod())
            .paymentProcessor(paymentMeta.getPaymentProcessor())
            .ppdId(paymentMeta.getPpdId())
            .reason(paymentMeta.getReason())
            .byOrderOf(paymentMeta.getByOrderOf())
            .payee(paymentMeta.getPayee())
            .build();
    }

    public static TransactionPaymentMetaDto fromPaymentMeta(
        TransactionPaymentMeta paymentMeta
    ) {
        return TransactionPaymentMetaDto.builder()
            .referenceNumber(paymentMeta.getReferenceNumber())
            .payer(paymentMeta.getPayer())
            .paymentMethod(paymentMeta.getPaymentMethod())
            .paymentProcessor(paymentMeta.getPaymentProcessor())
            .ppdId(paymentMeta.getPpdId())
            .reason(paymentMeta.getReason())
            .byOrderOf(paymentMeta.getByOrderOf())
            .payee(paymentMeta.getPayee())
            .build();
    }
}
