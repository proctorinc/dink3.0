package com.dink3.user;

import com.dink3.jooq.tables.pojos.Users;
import com.dink3.jooq.tables.pojos.UserSubscriptions;
import com.dink3.plaid.repository.UserSubscriptionRepository;
import com.dink3.user.dto.UserProfileDto;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserService(UserRepository userRepository, UserSubscriptionRepository userSubscriptionRepository) {
        this.userRepository = userRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    public Optional<Users> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<Users> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserProfileDto> getUserProfile(String userId) {
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        Users user = userOpt.get();
        Optional<UserSubscriptions> subscriptionOpt = userSubscriptionRepository.findByUserId(userId);
        UserProfileDto profile = new UserProfileDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            subscriptionOpt.orElse(null)
        );
        return Optional.of(profile);
    }
} 