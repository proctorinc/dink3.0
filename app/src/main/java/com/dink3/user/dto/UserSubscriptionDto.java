package com.dink3.user.dto;

import com.dink3.jooq.tables.pojos.UserSubscription;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for user subscription with Lombok builder support.
 */
@Data
@Builder
public class UserSubscriptionDto {

    private String tier;
    private String lastSyncAt;
    private String createdAt;
    private String updatedAt;

    /**
     * Convert this DTO to a jOOQ UserSubscription POJO.
     */
    public UserSubscription toUserSubscription() {
        UserSubscription subscription = new UserSubscription();
        subscription.setTier(tier);
        subscription.setLastSyncAt(lastSyncAt);
        subscription.setCreatedAt(createdAt);
        subscription.setUpdatedAt(updatedAt);
        return subscription;
    }

    /**
     * Create a UserSubscriptionDto from a jOOQ UserSubscriptions POJO.
     */
    public static UserSubscriptionDto fromUserSubscriptions(
        UserSubscription subscription
    ) {
        return UserSubscriptionDto.builder()
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
            .tier("premium")
            .createdAt(timestamp)
            .updatedAt(timestamp)
            .build();
    }
}
