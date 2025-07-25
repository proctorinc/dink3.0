/*
 * This file is generated by jOOQ.
 */
package com.dink3.jooq.tables;


import com.dink3.jooq.DefaultSchema;
import com.dink3.jooq.Keys;
import com.dink3.jooq.tables.Transaction.TransactionPath;
import com.dink3.jooq.tables.records.TransactionLocationRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TransactionLocation extends TableImpl<TransactionLocationRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>transaction_location</code>
     */
    public static final TransactionLocation TRANSACTION_LOCATION = new TransactionLocation();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TransactionLocationRecord> getRecordType() {
        return TransactionLocationRecord.class;
    }

    /**
     * The column <code>transaction_location.id</code>.
     */
    public final TableField<TransactionLocationRecord, String> ID = createField(DSL.name("id"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.transaction_id</code>.
     */
    public final TableField<TransactionLocationRecord, String> TRANSACTION_ID = createField(DSL.name("transaction_id"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>transaction_location.address</code>.
     */
    public final TableField<TransactionLocationRecord, String> ADDRESS = createField(DSL.name("address"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.city</code>.
     */
    public final TableField<TransactionLocationRecord, String> CITY = createField(DSL.name("city"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.region</code>.
     */
    public final TableField<TransactionLocationRecord, String> REGION = createField(DSL.name("region"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.postal_code</code>.
     */
    public final TableField<TransactionLocationRecord, String> POSTAL_CODE = createField(DSL.name("postal_code"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.country</code>.
     */
    public final TableField<TransactionLocationRecord, String> COUNTRY = createField(DSL.name("country"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>transaction_location.lat</code>.
     */
    public final TableField<TransactionLocationRecord, Float> LAT = createField(DSL.name("lat"), SQLDataType.REAL, this, "");

    /**
     * The column <code>transaction_location.lon</code>.
     */
    public final TableField<TransactionLocationRecord, Float> LON = createField(DSL.name("lon"), SQLDataType.REAL, this, "");

    private TransactionLocation(Name alias, Table<TransactionLocationRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private TransactionLocation(Name alias, Table<TransactionLocationRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>transaction_location</code> table reference
     */
    public TransactionLocation(String alias) {
        this(DSL.name(alias), TRANSACTION_LOCATION);
    }

    /**
     * Create an aliased <code>transaction_location</code> table reference
     */
    public TransactionLocation(Name alias) {
        this(alias, TRANSACTION_LOCATION);
    }

    /**
     * Create a <code>transaction_location</code> table reference
     */
    public TransactionLocation() {
        this(DSL.name("transaction_location"), null);
    }

    public <O extends Record> TransactionLocation(Table<O> path, ForeignKey<O, TransactionLocationRecord> childPath, InverseForeignKey<O, TransactionLocationRecord> parentPath) {
        super(path, childPath, parentPath, TRANSACTION_LOCATION);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class TransactionLocationPath extends TransactionLocation implements Path<TransactionLocationRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> TransactionLocationPath(Table<O> path, ForeignKey<O, TransactionLocationRecord> childPath, InverseForeignKey<O, TransactionLocationRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private TransactionLocationPath(Name alias, Table<TransactionLocationRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public TransactionLocationPath as(String alias) {
            return new TransactionLocationPath(DSL.name(alias), this);
        }

        @Override
        public TransactionLocationPath as(Name alias) {
            return new TransactionLocationPath(alias, this);
        }

        @Override
        public TransactionLocationPath as(Table<?> alias) {
            return new TransactionLocationPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<TransactionLocationRecord> getPrimaryKey() {
        return Keys.TRANSACTION_LOCATION__PK_TRANSACTION_LOCATION;
    }

    @Override
    public List<ForeignKey<TransactionLocationRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TRANSACTION_LOCATION__FK_TRANSACTION_LOCATION_PK_TRANSACTION);
    }

    private transient TransactionPath _transaction;

    /**
     * Get the implicit join path to the <code>transaction</code> table.
     */
    public TransactionPath transaction() {
        if (_transaction == null)
            _transaction = new TransactionPath(this, Keys.TRANSACTION_LOCATION__FK_TRANSACTION_LOCATION_PK_TRANSACTION, null);

        return _transaction;
    }

    @Override
    public TransactionLocation as(String alias) {
        return new TransactionLocation(DSL.name(alias), this);
    }

    @Override
    public TransactionLocation as(Name alias) {
        return new TransactionLocation(alias, this);
    }

    @Override
    public TransactionLocation as(Table<?> alias) {
        return new TransactionLocation(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionLocation rename(String name) {
        return new TransactionLocation(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionLocation rename(Name name) {
        return new TransactionLocation(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionLocation rename(Table<?> name) {
        return new TransactionLocation(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation where(Condition condition) {
        return new TransactionLocation(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TransactionLocation where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TransactionLocation where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TransactionLocation where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public TransactionLocation where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public TransactionLocation whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
