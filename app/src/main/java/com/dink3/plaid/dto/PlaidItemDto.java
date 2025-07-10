package com.dink3.plaid.dto;

import com.dink3.jooq.tables.pojos.PlaidItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlaidItemDto {
    private String id;
    private String userId;
    private String plaidItemId;
    private String plaidAccessToken;
    private String plaidInstitutionId;
    private String status;
    private String lastWebhook;
    private String createdAt;
    private String updatedAt;
    
    /**
     * Convert from Plaid Item to PlaidItemDto
     */
    public static PlaidItemDto fromPlaidItem(String plaidItemId, String plaidAccessToken, String userId, String plaidInstitutionId) {
        return PlaidItemDto.builder()
                .plaidItemId(plaidItemId)
                .plaidAccessToken(plaidAccessToken)
                .userId(userId)
                .plaidInstitutionId(plaidInstitutionId)
                .status("good")
                .build();
    }
    
    /**
     * Update an existing PlaidItem entity with Plaid data (preserves ID and user-specific fields)
     */
    public void updateExistingPlaidItem(PlaidItem existingItem) {
        existingItem.setPlaidAccessToken(this.plaidAccessToken);
        existingItem.setPlaidInstitutionId(this.plaidInstitutionId);
        existingItem.setStatus(this.status);
        existingItem.setLastWebhook(this.lastWebhook);
        existingItem.setUpdatedAt(LocalDateTime.now().toString());
    }
    
    /**
     * Convert to PlaidItem entity (for new items)
     */
    public PlaidItem toPlaidItem() {
        PlaidItem item = new PlaidItem();
        item.setId(this.id);
        item.setUserId(this.userId);
        item.setPlaidItemId(this.plaidItemId);
        item.setPlaidAccessToken(this.plaidAccessToken);
        item.setPlaidInstitutionId(this.plaidInstitutionId);
        item.setStatus(this.status);
        item.setLastWebhook(this.lastWebhook);
        item.setCreatedAt(this.createdAt);
        item.setUpdatedAt(this.updatedAt);
        return item;
    }
} 