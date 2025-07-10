package com.dink3.transactions;

import com.dink3.jooq.tables.pojos.Transaction;
import com.dink3.transactions.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<Transaction> getTransactionByIdForUser(String transactionId, String userId) {
        return transactionRepository.findById(transactionId)
                .filter(transaction -> userId.equals(transaction.getUserId()));
    }
} 