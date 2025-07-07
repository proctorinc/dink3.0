package com.dink3.accounts;

import com.dink3.jooq.tables.daos.AccountsDao;
import com.dink3.jooq.tables.pojos.Accounts;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {
    private final AccountsDao accountsDao;

    public AccountRepository(Configuration configuration) {
        this.accountsDao = new AccountsDao(configuration);
    }

    public void save(Accounts account) {
        accountsDao.insert(account);
    }

    public void saveOrUpdate(Accounts account) {
        // Check if account exists
        Optional<Accounts> existing = findByPlaidAccountId(account.getPlaidAccountId());
        if (existing.isPresent()) {
            // Update existing account
            Accounts existingAccount = existing.get();
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
            accountsDao.update(existingAccount);
        } else {
            // Insert new account
            accountsDao.insert(account);
        }
    }

    public void update(Accounts account) {
        accountsDao.update(account);
    }

    public Optional<Accounts> findByPlaidAccountId(String plaidAccountId) {
        return accountsDao.findAll().stream()
                .filter(account -> account.getPlaidAccountId().equals(plaidAccountId))
                .findFirst();
    }

    public List<Accounts> findByPlaidItemId(String plaidItemId) {
        return accountsDao.findAll().stream()
                .filter(account -> account.getPlaidItemId().equals(plaidItemId))
                .toList();
    }

    public List<Accounts> findByUserId(Integer userId) {
        // This would require a join with plaid_items table
        // For now, we'll return all accounts and filter in the service layer
        return accountsDao.findAll();
    }

    public Optional<Accounts> findById(Integer id) {
        return Optional.ofNullable(accountsDao.findById(id));
    }

    public List<Accounts> findAll() {
        return accountsDao.findAll();
    }
} 