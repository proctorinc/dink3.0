package com.dink3.accounts.dto;

import com.plaid.client.model.AccountBase;
import com.dink3.jooq.tables.pojos.Account;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AccountDto {
    private String id;
    private String userId;
    private String plaidItemId;
    private String plaidAccountId;
    private String name;
    private String mask;
    private String officialName;
    private String type;
    private String subtype;
    private Float currentBalance;
    private Float availableBalance;
    private String isoCurrencyCode;
    private String unofficialCurrencyCode;
    private String createdAt;
    private String updatedAt;
    
    /**
     * Convert from Plaid AccountBase to AccountDto
     */
    public static AccountDto fromPlaidAccount(AccountBase plaidAccount, String userId, String plaidItemId) {
        return AccountDto.builder()
                .plaidAccountId(plaidAccount.getAccountId())
                .userId(userId)
                .plaidItemId(plaidItemId)
                .name(plaidAccount.getName())
                .mask(plaidAccount.getMask())
                .officialName(plaidAccount.getOfficialName())
                .type(plaidAccount.getType() != null ? plaidAccount.getType().getValue() : null)
                .subtype(plaidAccount.getSubtype() != null ? plaidAccount.getSubtype().getValue() : null)
                .isoCurrencyCode(plaidAccount.getBalances().getIsoCurrencyCode())
                .unofficialCurrencyCode(plaidAccount.getBalances().getUnofficialCurrencyCode())
                .availableBalance(plaidAccount.getBalances().getAvailable() != null ? plaidAccount.getBalances().getAvailable().floatValue() : null)
                .currentBalance(plaidAccount.getBalances().getCurrent() != null ? plaidAccount.getBalances().getCurrent().floatValue() : null)
                .build();
    }
    
    /**
     * Update an existing Account entity with Plaid data (preserves ID and user-specific fields)
     */
    public void updateExistingAccount(Account existingAccount) {
        existingAccount.setName(this.name);
        existingAccount.setMask(this.mask);
        existingAccount.setOfficialName(this.officialName);
        existingAccount.setType(this.type);
        existingAccount.setSubtype(this.subtype);
        existingAccount.setIsoCurrencyCode(this.isoCurrencyCode);
        existingAccount.setUnofficialCurrencyCode(this.unofficialCurrencyCode);
        existingAccount.setAvailableBalance(this.availableBalance);
        existingAccount.setCurrentBalance(this.currentBalance);
        existingAccount.setUpdatedAt(LocalDateTime.now().toString());
    }
    
    /**
     * Convert to Account entity (for new accounts)
     */
    public Account toAccount() {
        Account account = new Account();
        account.setId(this.id);
        account.setUserId(this.userId);
        account.setPlaidItemId(this.plaidItemId);
        account.setPlaidAccountId(this.plaidAccountId);
        account.setName(this.name);
        account.setMask(this.mask);
        account.setOfficialName(this.officialName);
        account.setType(this.type);
        account.setSubtype(this.subtype);
        account.setIsoCurrencyCode(this.isoCurrencyCode);
        account.setUnofficialCurrencyCode(this.unofficialCurrencyCode);
        account.setAvailableBalance(this.availableBalance);
        account.setCurrentBalance(this.currentBalance);
        account.setCreatedAt(this.createdAt);
        account.setUpdatedAt(this.updatedAt);
        return account;
    }
}
