package com.dink3.plaid.repository;

import com.dink3.jooq.tables.daos.PlaidItemDao;
import com.dink3.jooq.tables.pojos.PlaidItem;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlaidItemRepository {
    private final PlaidItemDao plaidItemsDao;

    public PlaidItemRepository(Configuration configuration) {
        this.plaidItemsDao = new PlaidItemDao(configuration);
    }

    public void save(PlaidItem item) {
        plaidItemsDao.insert(item);
    }

    public void update(PlaidItem item) {
        plaidItemsDao.update(item);
    }

    public Optional<PlaidItem> findByPlaidItemId(String plaidItemId) {
        return plaidItemsDao.findAll().stream()
                .filter(item -> item.getPlaidItemId().equals(plaidItemId))
                .findFirst();
    }

    public List<PlaidItem> findByUserId(String userId) {
        return plaidItemsDao.findAll().stream()
                .filter(item -> item.getUserId().equals(userId))
                .toList();
    }

    public void deleteByPlaidItemId(String plaidItemId) {
        Optional<PlaidItem> item = findByPlaidItemId(plaidItemId);
        item.ifPresent(plaidItemsDao::delete);
    }
} 