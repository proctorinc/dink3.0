package com.dink3.transactions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionLocationDto {
    private String id;
    private String transactionId;
    private String address;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private Float lat;
    private Float lon;
} 