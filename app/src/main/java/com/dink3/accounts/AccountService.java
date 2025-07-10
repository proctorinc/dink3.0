package com.dink3.accounts;

import com.dink3.jooq.tables.pojos.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccountsByUserId(String userId) {
        // Ideally, AccountRepository should filter by userId, but for now, filter here
        return accountRepository.findByUserId(userId).stream()
                .filter(account -> userId.equals(account.getUserId()))
                .toList();
    }

    public Optional<Account> getAccountByIdForUser(String accountId, String userId) {
        return accountRepository.findById(accountId)
                .filter(account -> userId.equals(account.getUserId()));
    }
} 