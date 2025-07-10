package com.dink3.plaid.repository;

import com.dink3.jooq.tables.daos.UserSubscriptionDao;
import com.dink3.jooq.tables.pojos.UserSubscription;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserSubscriptionRepository {
    private final UserSubscriptionDao userSubscriptionsDao;

    public UserSubscriptionRepository(Configuration configuration) {
        this.userSubscriptionsDao = new UserSubscriptionDao(configuration);
    }

    public void save(UserSubscription subscription) {
        userSubscriptionsDao.insert(subscription);
    }

    public void update(UserSubscription subscription) {
        userSubscriptionsDao.update(subscription);
    }

    public Optional<UserSubscription> findByUserId(String userId) {
        return userSubscriptionsDao.findAll().stream()
                .filter(subscription -> subscription.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<UserSubscription> findById(String id) {
        return Optional.ofNullable(userSubscriptionsDao.findById(id));
    }
} 