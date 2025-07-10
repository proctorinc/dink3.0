package com.dink3.accounts;

import com.dink3.jooq.tables.daos.AccountDao;
import com.dink3.jooq.tables.pojos.Account;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {
    private final AccountDao accountDao;

    public AccountRepository(Configuration configuration) {
        this.accountDao = new AccountDao(configuration);
    }

    public void save(Account account) {
        accountDao.insert(account);
    }

    public void upsert(Account account) {
        accountDao.merge(account);
    }

    public void saveOrUpdate(Account account) {
        // Check if account exists
        Optional<Account> existing = findByPlaidAccountId(account.getPlaidAccountId());
        if (existing.isPresent()) {
            // Update existing account
            Account existingAccount = existing.get();
            existingAccount.setName(account.getName());
            existingAccount.setMask(account.getMask());
            existingAccount.setOfficialName(account.getOfficialName());
            existingAccount.setType(account.getType());
            existingAccount.setSubtype(account.getSubtype());
            existingAccount.setCurrentBalance(account.getCurrentBalance());
            existingAccount.setAvailableBalance(account.getAvailableBalance());
            existingAccount.setIsoCurrencyCode(account.getIsoCurrencyCode());
            existingAccount.setUnofficialCurrencyCode(account.getUnofficialCurrencyCode());
            existingAccount.setUpdatedAt(account.getUpdatedAt());
            accountDao.update(existingAccount);
        } else {
            // Insert new account
            accountDao.insert(account);
        }
    }

    public void update(Account account) {
        accountDao.update(account);
    }

    public Optional<Account> findByPlaidAccountId(String plaidAccountId) {
        return accountDao.findAll().stream()
                .filter(account -> account.getPlaidAccountId().equals(plaidAccountId))
                .findFirst();
    }

    public List<Account> findByPlaidItemId(String plaidItemId) {
        return accountDao.findAll().stream()
                .filter(account -> account.getPlaidItemId().equals(plaidItemId))
                .toList();
    }

    public List<Account> findByUserId(String userId) {
        // This would require a join with plaid_items table
        // For now, we'll return all accounts and filter in the service layer
        return accountDao.findAll();
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountDao.findById(id));
    }

    public List<Account> findAll() {
        return accountDao.findAll();
    }
} 