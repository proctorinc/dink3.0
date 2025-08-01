/*
 * This file is generated by jOOQ.
 */
package com.dink3.jooq.tables.daos;


import com.dink3.jooq.tables.Institution;
import com.dink3.jooq.tables.records.InstitutionRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class InstitutionDao extends DAOImpl<InstitutionRecord, com.dink3.jooq.tables.pojos.Institution, String> {

    /**
     * Create a new InstitutionDao without any configuration
     */
    public InstitutionDao() {
        super(Institution.INSTITUTION, com.dink3.jooq.tables.pojos.Institution.class);
    }

    /**
     * Create a new InstitutionDao with an attached configuration
     */
    public InstitutionDao(Configuration configuration) {
        super(Institution.INSTITUTION, com.dink3.jooq.tables.pojos.Institution.class, configuration);
    }

    @Override
    public String getId(com.dink3.jooq.tables.pojos.Institution object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfId(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchById(String... values) {
        return fetch(Institution.INSTITUTION.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.dink3.jooq.tables.pojos.Institution fetchOneById(String value) {
        return fetchOne(Institution.INSTITUTION.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.dink3.jooq.tables.pojos.Institution> fetchOptionalById(String value) {
        return fetchOptional(Institution.INSTITUTION.ID, value);
    }

    /**
     * Fetch records that have <code>plaid_institution_id BETWEEN lowerInclusive
     * AND upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfPlaidInstitutionId(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.PLAID_INSTITUTION_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>plaid_institution_id IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByPlaidInstitutionId(String... values) {
        return fetch(Institution.INSTITUTION.PLAID_INSTITUTION_ID, values);
    }

    /**
     * Fetch a unique record that has <code>plaid_institution_id = value</code>
     */
    public com.dink3.jooq.tables.pojos.Institution fetchOneByPlaidInstitutionId(String value) {
        return fetchOne(Institution.INSTITUTION.PLAID_INSTITUTION_ID, value);
    }

    /**
     * Fetch a unique record that has <code>plaid_institution_id = value</code>
     */
    public Optional<com.dink3.jooq.tables.pojos.Institution> fetchOptionalByPlaidInstitutionId(String value) {
        return fetchOptional(Institution.INSTITUTION.PLAID_INSTITUTION_ID, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByName(String... values) {
        return fetch(Institution.INSTITUTION.NAME, values);
    }

    /**
     * Fetch records that have <code>logo BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfLogo(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.LOGO, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>logo IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByLogo(String... values) {
        return fetch(Institution.INSTITUTION.LOGO, values);
    }

    /**
     * Fetch records that have <code>primary_color BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfPrimaryColor(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.PRIMARY_COLOR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>primary_color IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByPrimaryColor(String... values) {
        return fetch(Institution.INSTITUTION.PRIMARY_COLOR, values);
    }

    /**
     * Fetch records that have <code>url BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfUrl(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.URL, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>url IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByUrl(String... values) {
        return fetch(Institution.INSTITUTION.URL, values);
    }

    /**
     * Fetch records that have <code>created_at BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfCreatedAt(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.CREATED_AT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>created_at IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByCreatedAt(String... values) {
        return fetch(Institution.INSTITUTION.CREATED_AT, values);
    }

    /**
     * Fetch records that have <code>updated_at BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchRangeOfUpdatedAt(String lowerInclusive, String upperInclusive) {
        return fetchRange(Institution.INSTITUTION.UPDATED_AT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>updated_at IN (values)</code>
     */
    public List<com.dink3.jooq.tables.pojos.Institution> fetchByUpdatedAt(String... values) {
        return fetch(Institution.INSTITUTION.UPDATED_AT, values);
    }
}
