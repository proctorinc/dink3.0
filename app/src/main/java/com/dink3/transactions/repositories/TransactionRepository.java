package com.dink3.transactions.repositories;

import static com.dink3.jooq.tables.Transaction.TRANSACTION;
import static com.dink3.jooq.tables.TransactionLocation.TRANSACTION_LOCATION;
import static com.dink3.jooq.tables.TransactionPaymentMeta.TRANSACTION_PAYMENT_META;

import com.dink3.jooq.tables.daos.TransactionDao;
import com.dink3.jooq.tables.daos.TransactionLocationDao;
import com.dink3.jooq.tables.daos.TransactionPaymentMetaDao;
import com.dink3.transactions.dto.TransactionFullDto;
import java.util.List;
import java.util.Optional;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final TransactionLocationDao transactionLocationDao;
    private final TransactionPaymentMetaDao transactionPaymentMetaDao;
    private final DSLContext dsl;

    public TransactionRepository(Configuration configuration, DSLContext dsl) {
        this.transactionDao = new TransactionDao(configuration);
        this.transactionLocationDao = new TransactionLocationDao(configuration);
        this.transactionPaymentMetaDao = new TransactionPaymentMetaDao(
            configuration
        );
        this.dsl = dsl;
    }

    public void save(TransactionFullDto transaction) {
        transactionDao.insert(transaction.toTransaction());
        transactionLocationDao.insert(transaction.getLocation().toLocation());
        transactionPaymentMetaDao.insert(
            transaction.getPaymentMeta().toPaymentMeta()
        );
    }

    public void update(TransactionFullDto transaction) {
        transactionDao.update(transaction.toTransaction());
        transactionLocationDao.update(transaction.getLocation().toLocation());
        transactionPaymentMetaDao.update(
            transaction.getPaymentMeta().toPaymentMeta()
        );
    }

    public Optional<TransactionFullDto> findByPlaidTransactionId(
        String plaidTransactionId
    ) {
        return dsl
            .select()
            .from(TRANSACTION)
            .join(TRANSACTION_LOCATION)
            .on(TRANSACTION.ID.eq(TRANSACTION_LOCATION.TRANSACTION_ID))
            .join(TRANSACTION_PAYMENT_META)
            .on(TRANSACTION.ID.eq(TRANSACTION_PAYMENT_META.TRANSACTION_ID))
            .where(TRANSACTION.PLAID_TRANSACTION_ID.eq(plaidTransactionId))
            .orderBy(TRANSACTION.DATE.desc())
            .fetchOptional()
            .map(TransactionFullDto::fromRecord);
    }

    public List<TransactionFullDto> findByPlaidAccountId(
        String plaidAccountId
    ) {
        return dsl
            .select()
            .from(TRANSACTION)
            .join(TRANSACTION_LOCATION)
            .on(TRANSACTION.ID.eq(TRANSACTION_LOCATION.TRANSACTION_ID))
            .join(TRANSACTION_PAYMENT_META)
            .on(TRANSACTION.ID.eq(TRANSACTION_PAYMENT_META.TRANSACTION_ID))
            .where(TRANSACTION.PLAID_ACCOUNT_ID.eq(plaidAccountId))
            .orderBy(TRANSACTION.DATE.desc())
            .fetch()
            .map(TransactionFullDto::fromRecord);
    }

    public List<TransactionFullDto> findByUserId(String userId) {
        return dsl
            .select()
            .from(TRANSACTION)
            .join(TRANSACTION_LOCATION)
            .on(TRANSACTION.ID.eq(TRANSACTION_LOCATION.TRANSACTION_ID))
            .join(TRANSACTION_PAYMENT_META)
            .on(TRANSACTION.ID.eq(TRANSACTION_PAYMENT_META.TRANSACTION_ID))
            .where(TRANSACTION.USER_ID.eq(userId))
            .orderBy(TRANSACTION.DATE.desc())
            .fetch()
            .map(TransactionFullDto::fromRecord);
    }

    public Optional<TransactionFullDto> findById(String id) {
        return dsl
            .select()
            .from(TRANSACTION)
            .join(TRANSACTION_LOCATION)
            .on(TRANSACTION.ID.eq(TRANSACTION_LOCATION.TRANSACTION_ID))
            .join(TRANSACTION_PAYMENT_META)
            .on(TRANSACTION.ID.eq(TRANSACTION_PAYMENT_META.TRANSACTION_ID))
            .where(TRANSACTION.ID.eq(id))
            .orderBy(TRANSACTION.DATE.desc())
            .fetchOptional()
            .map(TransactionFullDto::fromRecord);
    }
}
