package com.dink3.user.dto;

import com.dink3.jooq.tables.pojos.UserSubscriptions;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for user subscription with Lombok builder support.
 */
@Data
@Builder
public class UserSubscriptionDto {
    private String id;
    private String userId;
    private String tier;
    private String lastSyncAt;
    private String createdAt;
    private String updatedAt;

    /**
     * Convert this DTO to a jOOQ UserSubscriptions POJO.
     */
    public UserSubscriptions toUserSubscriptions() {
        UserSubscriptions subscription = new UserSubscriptions();
        subscription.setId(id);
        subscription.setUserId(userId);
        subscription.setTier(tier);
        subscription.setLastSyncAt(lastSyncAt);
        subscription.setCreatedAt(createdAt);
        subscription.setUpdatedAt(updatedAt);
        return subscription;
    }

    /**
     * Create a UserSubscriptionDto from a jOOQ UserSubscriptions POJO.
     */
    public static UserSubscriptionDto fromUserSubscriptions(UserSubscriptions subscription) {
        return UserSubscriptionDto.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .tier(subscription.getTier())
                .lastSyncAt(subscription.getLastSyncAt())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    /**
     * Convenience method to create a basic subscription for a new user.
     */
    public static UserSubscriptionDto createBasicSubscription(String userId) {
        String timestamp = LocalDateTime.now().toString();
        return UserSubscriptionDto.builder()
                .userId(userId)
                .tier("basic")
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .lastSyncAt(timestamp)
                .build();
    }

    /**
     * Convenience method to create a premium subscription for a user.
     */
    public static UserSubscriptionDto createPremiumSubscription(String userId) {
        String timestamp = LocalDateTime.now().toString();
        return UserSubscriptionDto.builder()
                .userId(userId)
                .tier("premium")
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();
    }
} 