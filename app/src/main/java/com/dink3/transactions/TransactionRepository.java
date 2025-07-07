package com.dink3.transactions;

import com.dink3.jooq.tables.daos.TransactionsDao;
import com.dink3.jooq.tables.pojos.Transactions;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {
    private final TransactionsDao transactionsDao;

    public TransactionRepository(Configuration configuration) {
        this.transactionsDao = new TransactionsDao(configuration);
    }

    public void save(Transactions transaction) {
        transactionsDao.insert(transaction);
    }

    public void update(Transactions transaction) {
        transactionsDao.update(transaction);
    }

    public Optional<Transactions> findByPlaidTransactionId(String plaidTransactionId) {
        return transactionsDao.findAll().stream()
                .filter(transaction -> transaction.getPlaidTransactionId().equals(plaidTransactionId))
                .findFirst();
    }

    public List<Transactions> findByPlaidAccountId(String plaidAccountId) {
        return transactionsDao.findAll().stream()
                .filter(transaction -> transaction.getPlaidAccountId().equals(plaidAccountId))
                .toList();
    }

    public List<Transactions> findByUserId(Integer userId) {
        // This would require a join with accounts and plaid_items tables
        // For now, we'll return all transactions and filter in the service layer
        return transactionsDao.findAll();
    }

    public Optional<Transactions> findById(Integer id) {
        return Optional.ofNullable(transactionsDao.findById(id));
    }

    public List<Transactions> findAll() {
        return transactionsDao.findAll();
    }

    public List<Transactions> findRecentTransactions(int limit) {
        return transactionsDao.findAll().stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(limit)
                .toList();
    }
} 