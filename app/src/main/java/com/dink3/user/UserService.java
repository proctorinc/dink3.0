package com.dink3.user;

import com.dink3.jooq.tables.pojos.User;
import com.dink3.jooq.tables.pojos.UserSubscription;
import com.dink3.plaid.repository.UserSubscriptionRepository;
import com.dink3.user.dto.UserProfileDto;
import com.dink3.user.dto.UserSubscriptionDto;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserService(
        UserRepository userRepository,
        UserSubscriptionRepository userSubscriptionRepository
    ) {
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
        Optional<UserSubscription> subscriptionOpt =
            userSubscriptionRepository.findByUserId(userId);
        UserSubscriptionDto subscription = null;

        if (subscriptionOpt.isPresent()) {
            subscription = UserSubscriptionDto.fromUserSubscriptions(
                subscriptionOpt.get()
            );
        }

        UserProfileDto profile = UserProfileDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .subscription(subscription)
            .build();
        return Optional.of(profile);
    }
}
