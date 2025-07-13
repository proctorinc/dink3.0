package com.dink3.transactions;

import com.dink3.transactions.dto.TransactionFullDto;
import com.dink3.transactions.repositories.TransactionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionFullDto> getTransactionsByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Optional<TransactionFullDto> getTransactionByIdForUser(
        String transactionId,
        String userId
    ) {
        return transactionRepository
            .findById(transactionId)
            .filter(transaction -> userId.equals(transaction.getUserId()));
    }
}
