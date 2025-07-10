package com.dink3.transactions.repositories;

import com.dink3.jooq.tables.daos.TransactionDao;
import com.dink3.jooq.tables.pojos.Transaction;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {
    private final TransactionDao transactionsDao;

    public TransactionRepository(Configuration configuration) {
        this.transactionsDao = new TransactionDao(configuration);
    }

    public void save(Transaction transaction) {
        transactionsDao.insert(transaction);
    }

    public void update(Transaction transaction) {
        transactionsDao.update(transaction);
    }

    public Optional<Transaction> findByPlaidTransactionId(String plaidTransactionId) {
        return transactionsDao.findAll().stream()
                .filter(transaction -> transaction.getPlaidTransactionId().equals(plaidTransactionId))
                .findFirst();
    }

    public List<Transaction> findByPlaidAccountId(String plaidAccountId) {
        return transactionsDao.findAll().stream()
                .filter(transaction -> transaction.getPlaidAccountId().equals(plaidAccountId))
                .toList();
    }

    public List<Transaction> findByUserId(String userId) {
        // This would require a join with accounts and plaid_items tables
        // For now, we'll return all transactions and filter in the service layer
        return transactionsDao.findAll();
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactionsDao.findById(id));
    }

    public List<Transaction> findAll() {
        return transactionsDao.findAll();
    }

    public List<Transaction> findRecentTransactions(int limit) {
        return transactionsDao.findAll().stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(limit)
                .toList();
    }
} 