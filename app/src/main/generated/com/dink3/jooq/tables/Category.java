/*
 * This file is generated by jOOQ.
 */
package com.dink3.jooq.tables;


import com.dink3.jooq.DefaultSchema;
import com.dink3.jooq.Keys;
import com.dink3.jooq.tables.Transaction.TransactionPath;
import com.dink3.jooq.tables.User.UserPath;
import com.dink3.jooq.tables.records.CategoryRecord;

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
public class Category extends TableImpl<CategoryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>category</code>
     */
    public static final Category CATEGORY = new Category();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CategoryRecord> getRecordType() {
        return CategoryRecord.class;
    }

    /**
     * The column <code>category.id</code>.
     */
    public final TableField<CategoryRecord, String> ID = createField(DSL.name("id"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>category.user_id</code>.
     */
    public final TableField<CategoryRecord, String> USER_ID = createField(DSL.name("user_id"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>category.name</code>.
     */
    public final TableField<CategoryRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>category.description</code>.
     */
    public final TableField<CategoryRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>category.ignore</code>.
     */
    public final TableField<CategoryRecord, Boolean> IGNORE = createField(DSL.name("ignore"), SQLDataType.BOOLEAN.defaultValue(DSL.field(DSL.raw("false"), SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>category.hide</code>.
     */
    public final TableField<CategoryRecord, Boolean> HIDE = createField(DSL.name("hide"), SQLDataType.BOOLEAN.defaultValue(DSL.field(DSL.raw("false"), SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>category.startDate</code>.
     */
    public final TableField<CategoryRecord, String> STARTDATE = createField(DSL.name("startDate"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>category.endDate</code>.
     */
    public final TableField<CategoryRecord, String> ENDDATE = createField(DSL.name("endDate"), SQLDataType.CLOB, this, "");

    private Category(Name alias, Table<CategoryRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Category(Name alias, Table<CategoryRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>category</code> table reference
     */
    public Category(String alias) {
        this(DSL.name(alias), CATEGORY);
    }

    /**
     * Create an aliased <code>category</code> table reference
     */
    public Category(Name alias) {
        this(alias, CATEGORY);
    }

    /**
     * Create a <code>category</code> table reference
     */
    public Category() {
        this(DSL.name("category"), null);
    }

    public <O extends Record> Category(Table<O> path, ForeignKey<O, CategoryRecord> childPath, InverseForeignKey<O, CategoryRecord> parentPath) {
        super(path, childPath, parentPath, CATEGORY);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class CategoryPath extends Category implements Path<CategoryRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> CategoryPath(Table<O> path, ForeignKey<O, CategoryRecord> childPath, InverseForeignKey<O, CategoryRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private CategoryPath(Name alias, Table<CategoryRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public CategoryPath as(String alias) {
            return new CategoryPath(DSL.name(alias), this);
        }

        @Override
        public CategoryPath as(Name alias) {
            return new CategoryPath(alias, this);
        }

        @Override
        public CategoryPath as(Table<?> alias) {
            return new CategoryPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<CategoryRecord> getPrimaryKey() {
        return Keys.CATEGORY__PK_CATEGORY;
    }

    @Override
    public List<ForeignKey<CategoryRecord, ?>> getReferences() {
        return Arrays.asList(Keys.CATEGORY__FK_CATEGORY_PK_USER);
    }

    private transient UserPath _user;

    /**
     * Get the implicit join path to the <code>user</code> table.
     */
    public UserPath user() {
        if (_user == null)
            _user = new UserPath(this, Keys.CATEGORY__FK_CATEGORY_PK_USER, null);

        return _user;
    }

    private transient TransactionPath _transaction;

    /**
     * Get the implicit to-many join path to the <code>transaction</code> table
     */
    public TransactionPath transaction() {
        if (_transaction == null)
            _transaction = new TransactionPath(this, null, Keys.TRANSACTION__FK_TRANSACTION_PK_CATEGORY.getInverseKey());

        return _transaction;
    }

    @Override
    public Category as(String alias) {
        return new Category(DSL.name(alias), this);
    }

    @Override
    public Category as(Name alias) {
        return new Category(alias, this);
    }

    @Override
    public Category as(Table<?> alias) {
        return new Category(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Category rename(String name) {
        return new Category(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Category rename(Name name) {
        return new Category(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Category rename(Table<?> name) {
        return new Category(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Condition condition) {
        return new Category(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Category where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Category whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
