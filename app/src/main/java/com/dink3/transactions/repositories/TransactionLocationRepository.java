package com.dink3.transactions.repositories;

import com.dink3.jooq.tables.daos.TransactionLocationDao;
import com.dink3.jooq.tables.pojos.TransactionLocation;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionLocationRepository {
    private final TransactionLocationDao transactionLocationDao;

    public TransactionLocationRepository(Configuration configuration) {
        this.transactionLocationDao = new TransactionLocationDao(configuration);
    }

    public void save(TransactionLocation location) {
        transactionLocationDao.insert(location);
    }
} 