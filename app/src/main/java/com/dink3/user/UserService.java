package com.dink3.user;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.jooq.tables.pojos.UserSubscription;
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

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserProfileDto> getUserProfile(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        Optional<UserSubscription> subscriptionOpt = userSubscriptionRepository.findByUserId(userId);
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