package com.dink3.plaid.repository;

import com.dink3.jooq.tables.daos.PlaidItemsDao;
import com.dink3.jooq.tables.pojos.PlaidItems;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlaidItemRepository {
    private final PlaidItemsDao plaidItemsDao;

    public PlaidItemRepository(Configuration configuration) {
        this.plaidItemsDao = new PlaidItemsDao(configuration);
    }

    public void save(PlaidItems item) {
        plaidItemsDao.insert(item);
    }

    public void update(PlaidItems item) {
        plaidItemsDao.update(item);
    }

    public Optional<PlaidItems> findByPlaidItemId(String plaidItemId) {
        return plaidItemsDao.findAll().stream()
                .filter(item -> item.getPlaidItemId().equals(plaidItemId))
                .findFirst();
    }

    public List<PlaidItems> findByUserId(Integer userId) {
        return plaidItemsDao.findAll().stream()
                .filter(item -> item.getUserId().equals(userId))
                .toList();
    }

    public void deleteByPlaidItemId(String plaidItemId) {
        Optional<PlaidItems> item = findByPlaidItemId(plaidItemId);
        item.ifPresent(plaidItemsDao::delete);
    }
} 