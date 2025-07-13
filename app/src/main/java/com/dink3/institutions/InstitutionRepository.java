package com.dink3.institutions;

import static com.dink3.jooq.tables.Account.ACCOUNT;
import static com.dink3.jooq.tables.Institution.INSTITUTION;
import static com.dink3.jooq.tables.PlaidItem.PLAID_ITEM;

import com.dink3.accounts.dto.AccountDto;
import com.dink3.institutions.dto.InstitutionDto;
import com.dink3.institutions.dto.InstitutionFullDto;
import com.dink3.jooq.tables.daos.InstitutionDao;
import com.dink3.jooq.tables.pojos.Institution;
import com.dink3.jooq.tables.records.AccountRecord;
import com.dink3.jooq.tables.records.InstitutionRecord;
import com.dink3.jooq.tables.records.PlaidItemRecord;
import com.dink3.plaid.dto.PlaidItemDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

@Repository
public class InstitutionRepository {

    private final InstitutionDao institutionsDao;
    private final DSLContext dsl;

    public InstitutionRepository(Configuration configuration, DSLContext dsl) {
        this.institutionsDao = new InstitutionDao(configuration);
        this.dsl = dsl;
    }

    public void save(Institution institution) {
        institutionsDao.insert(institution);
    }

    public void upsert(Institution institution) {
        institutionsDao.merge(institution);
    }

    public void update(Institution institution) {
        institutionsDao.update(institution);
    }

    public Optional<Institution> findByPlaidInstitutionId(
        String plaidInstitutionId
    ) {
        return institutionsDao
            .findAll()
            .stream()
            .filter(institution ->
                institution.getPlaidInstitutionId().equals(plaidInstitutionId)
            )
            .findFirst();
    }

    public List<InstitutionFullDto> findAllByUserId(String userId) {
        List<Record> records = dsl
            .select()
            .from(PLAID_ITEM)
            .join(INSTITUTION)
            .on(
                PLAID_ITEM.PLAID_INSTITUTION_ID.eq(
                    INSTITUTION.PLAID_INSTITUTION_ID
                )
            )
            .where(PLAID_ITEM.USER_ID.eq(userId))
            .fetch();

        return records
            .stream()
            .map((Record record) -> {
                InstitutionRecord institution = record.into(InstitutionRecord.class);
                PlaidItemRecord plaidItem = record.into(PlaidItemRecord.class);
        
                // Get accounts for this plaid item
                List<AccountRecord> accounts = dsl
                    .selectFrom(ACCOUNT)
                    .where(ACCOUNT.PLAID_ITEM_ID.eq(plaidItem.getPlaidItemId()))
                    .fetchInto(AccountRecord.class);
        
                return InstitutionFullDto.builder()
                    .institution(InstitutionDto.fromRecord(institution))
                    .plaidItem(PlaidItemDto.fromRecord(plaidItem))
                    .accounts(accounts.stream()
                        .map(AccountDto::fromRecord)
                        .collect(Collectors.toList()))
                    .build();
            })
            .collect(Collectors.toList());
    }

    public Optional<InstitutionFullDto> findByUserIdAndId(
        String userId,
        String id
    ) {
        return dsl
            .select()
            .from(PLAID_ITEM)
            .join(INSTITUTION)
            .on(INSTITUTION.ID.eq(PLAID_ITEM.PLAID_INSTITUTION_ID))
            .join(ACCOUNT)
            .on(ACCOUNT.PLAID_ITEM_ID.eq(PLAID_ITEM.PLAID_ITEM_ID))
            .where(
                INSTITUTION.PLAID_INSTITUTION_ID.eq(id).and(
                    PLAID_ITEM.USER_ID.eq(userId)
                )
            )
            .fetchOptionalInto(InstitutionFullDto.class);
    }
}
