package com.dink3.transactions.repositories;

import com.dink3.jooq.tables.daos.TransactionPaymentMetaDao;
import com.dink3.jooq.tables.pojos.TransactionPaymentMeta;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionPaymentMetaRepository {
    private final TransactionPaymentMetaDao transactionPaymentMetaDao;

    public TransactionPaymentMetaRepository(Configuration configuration) {
        this.transactionPaymentMetaDao = new TransactionPaymentMetaDao(configuration);
    }

    public void save(TransactionPaymentMeta paymentMeta) {
        transactionPaymentMetaDao.insert(paymentMeta);
    }

    public void upsert(TransactionPaymentMeta paymentMeta) {
        transactionPaymentMetaDao.merge(paymentMeta);
    }
}
