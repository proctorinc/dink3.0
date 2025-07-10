package com.dink3.institutions;

import com.dink3.jooq.tables.daos.InstitutionDao;
import com.dink3.jooq.tables.pojos.Institution;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InstitutionRepository {
    private final InstitutionDao institutionsDao;

    public InstitutionRepository(Configuration configuration) {
        this.institutionsDao = new InstitutionDao(configuration);
    }

    public void save(Institution institution) {
        institutionsDao.insert(institution);
    }

    public void update(Institution institution) {
        institutionsDao.update(institution);
    }

    public Optional<Institution> findByPlaidInstitutionId(String plaidInstitutionId) {
        return institutionsDao.findAll().stream()
                .filter(institution -> institution.getPlaidInstitutionId().equals(plaidInstitutionId))
                .findFirst();
    }

    public List<Institution> findAll() {
        return institutionsDao.findAll();
    }

    public Optional<Institution> findById(String id) {
        return Optional.ofNullable(institutionsDao.findById(id));
    }
} 