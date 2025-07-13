package com.dink3.user.dto;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.jooq.tables.pojos.UserSubscription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for user profile information returned by the /me endpoint.
 * Excludes sensitive information like password hash.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {

    private String id;
    private String username;
    private String email;
    private String role;
    private String createdAt;
    private UserSubscriptionDto subscription;

    public static UserProfileDto fromUser(
        User user,
        UserSubscription subscription
    ) {
        return UserProfileDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .subscription(
                UserSubscriptionDto.fromUserSubscriptions(subscription)
            )
            .build();
    }
}
