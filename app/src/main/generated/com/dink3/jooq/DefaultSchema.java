/*
 * This file is generated by jOOQ.
 */
package com.dink3.jooq;


import com.dink3.jooq.tables.Account;
import com.dink3.jooq.tables.Category;
import com.dink3.jooq.tables.Institution;
import com.dink3.jooq.tables.PlaidItem;
import com.dink3.jooq.tables.RefreshToken;
import com.dink3.jooq.tables.Transaction;
import com.dink3.jooq.tables.TransactionLocation;
import com.dink3.jooq.tables.TransactionPaymentMeta;
import com.dink3.jooq.tables.User;
import com.dink3.jooq.tables.UserSubscription;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * The table <code>account</code>.
     */
    public final Account ACCOUNT = Account.ACCOUNT;

    /**
     * The table <code>category</code>.
     */
    public final Category CATEGORY = Category.CATEGORY;

    /**
     * The table <code>institution</code>.
     */
    public final Institution INSTITUTION = Institution.INSTITUTION;

    /**
     * The table <code>plaid_item</code>.
     */
    public final PlaidItem PLAID_ITEM = PlaidItem.PLAID_ITEM;

    /**
     * The table <code>refresh_token</code>.
     */
    public final RefreshToken REFRESH_TOKEN = RefreshToken.REFRESH_TOKEN;

    /**
     * The table <code>transaction</code>.
     */
    public final Transaction TRANSACTION = Transaction.TRANSACTION;

    /**
     * The table <code>transaction_location</code>.
     */
    public final TransactionLocation TRANSACTION_LOCATION = TransactionLocation.TRANSACTION_LOCATION;

    /**
     * The table <code>transaction_payment_meta</code>.
     */
    public final TransactionPaymentMeta TRANSACTION_PAYMENT_META = TransactionPaymentMeta.TRANSACTION_PAYMENT_META;

    /**
     * The table <code>user</code>.
     */
    public final User USER = User.USER;

    /**
     * The table <code>user_subscription</code>.
     */
    public final UserSubscription USER_SUBSCRIPTION = UserSubscription.USER_SUBSCRIPTION;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Account.ACCOUNT,
            Category.CATEGORY,
            Institution.INSTITUTION,
            PlaidItem.PLAID_ITEM,
            RefreshToken.REFRESH_TOKEN,
            Transaction.TRANSACTION,
            TransactionLocation.TRANSACTION_LOCATION,
            TransactionPaymentMeta.TRANSACTION_PAYMENT_META,
            User.USER,
            UserSubscription.USER_SUBSCRIPTION
        );
    }
}
