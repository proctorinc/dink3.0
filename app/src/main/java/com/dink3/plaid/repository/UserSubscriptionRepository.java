package com.dink3.plaid.repository;

import com.dink3.jooq.tables.daos.UserSubscriptionsDao;
import com.dink3.jooq.tables.pojos.UserSubscriptions;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserSubscriptionRepository {
    private final UserSubscriptionsDao userSubscriptionsDao;

    public UserSubscriptionRepository(Configuration configuration) {
        this.userSubscriptionsDao = new UserSubscriptionsDao(configuration);
    }

    public void save(UserSubscriptions subscription) {
        userSubscriptionsDao.insert(subscription);
    }

    public void update(UserSubscriptions subscription) {
        userSubscriptionsDao.update(subscription);
    }

    public Optional<UserSubscriptions> findByUserId(String userId) {
        return userSubscriptionsDao.findAll().stream()
                .filter(subscription -> subscription.getUserId().equals(userId))
                .findFirst();
    }

    public Optional<UserSubscriptions> findById(String id) {
        return Optional.ofNullable(userSubscriptionsDao.findById(id));
    }
} 