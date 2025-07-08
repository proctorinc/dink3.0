package com.dink3.user.dto;

import com.dink3.jooq.tables.pojos.UserSubscriptions;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile information returned by the /me endpoint.
 * Excludes sensitive information like password hash.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {
    private String id;
    private String username;
    private String email;
    private String role;
    private String createdAt;
    private UserSubscriptions subscription;
} 