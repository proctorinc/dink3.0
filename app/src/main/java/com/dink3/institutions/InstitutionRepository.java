package com.dink3.institutions;

import com.dink3.jooq.tables.daos.InstitutionsDao;
import com.dink3.jooq.tables.pojos.Institutions;
import org.jooq.Configuration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InstitutionRepository {
    private final InstitutionsDao institutionsDao;

    public InstitutionRepository(Configuration configuration) {
        this.institutionsDao = new InstitutionsDao(configuration);
    }

    public void save(Institutions institution) {
        institutionsDao.insert(institution);
    }

    public void update(Institutions institution) {
        institutionsDao.update(institution);
    }

    public Optional<Institutions> findByPlaidInstitutionId(String plaidInstitutionId) {
        return institutionsDao.findAll().stream()
                .filter(institution -> institution.getPlaidInstitutionId().equals(plaidInstitutionId))
                .findFirst();
    }

    public List<Institutions> findAll() {
        return institutionsDao.findAll();
    }

    public Optional<Institutions> findById(Integer id) {
        return Optional.ofNullable(institutionsDao.findById(id));
    }
} 