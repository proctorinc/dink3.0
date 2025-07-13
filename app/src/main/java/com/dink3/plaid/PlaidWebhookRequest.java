package com.dink3.plaid;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PlaidWebhookRequest {
    @JsonProperty("webhook_type")
    private String webhookType;
    @JsonProperty("webhook_code")
    private String webhookCode;
    @JsonProperty("item_id")
    private String itemId;
    @JsonProperty("removed_transactions")
    private List<String> removedTransactions;

    public String getWebhookType() { return webhookType; }
    public void setWebhookType(String webhookType) { this.webhookType = webhookType; }

    public String getWebhookCode() { return webhookCode; }
    public void setWebhookCode(String webhookCode) { this.webhookCode = webhookCode; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public List<String> getRemovedTransactions() { return removedTransactions; }
    public void setRemovedTransactions(List<String> removedTransactions) { this.removedTransactions = removedTransactions; }
} 