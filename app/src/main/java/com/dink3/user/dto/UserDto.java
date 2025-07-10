package com.dink3.user.dto;

import com.dink3.jooq.tables.pojos.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for user creation with Lombok builder support.
 */
@Data
@Builder
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private String createdAt;

    /**
     * Convert this DTO to a jOOQ User POJO.
     */
    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setPasswordHash(this.passwordHash);
        user.setRole(this.role);
        user.setCreatedAt(this.createdAt);
        return user;
    }

    /**
     * Create a UserDto from a jOOQ User POJO.
     */
    public static UserDto fromUsers(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Convenience method to create a user DTO with required fields.
     */
    public static UserDto createUser(String email, String username, String passwordHash) {
        return UserDto.builder()
                .email(email)
                .username(username)
                .passwordHash(passwordHash)
                .role("user")
                .createdAt(LocalDateTime.now().toString())
                .build();
    }
} 