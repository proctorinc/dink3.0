CREATE TABLE IF NOT EXISTS user (
    id TEXT PRIMARY KEY,
    username TEXT,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'user',
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    token TEXT NOT NULL,
    expires_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Plaid Items (represents a connection to a financial institution)
CREATE TABLE IF NOT EXISTS plaid_item (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    plaid_item_id TEXT NOT NULL UNIQUE,
    plaid_access_token TEXT NOT NULL,
    plaid_institution_id TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'good',
    last_webhook TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Institutions
CREATE TABLE IF NOT EXISTS institution (
    id TEXT PRIMARY KEY,
    plaid_institution_id TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    logo TEXT,
    primary_color TEXT,
    url TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- Accounts
CREATE TABLE IF NOT EXISTS account (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    plaid_item_id TEXT NOT NULL,
    plaid_account_id TEXT NOT NULL,
    name TEXT NOT NULL,
    mask TEXT,
    official_name TEXT,
    type TEXT NOT NULL,
    subtype TEXT,
    current_balance REAL,
    available_balance REAL,
    iso_currency_code TEXT,
    unofficial_currency_code TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (plaid_item_id) REFERENCES plaid_item(plaid_item_id) ON DELETE CASCADE,
    UNIQUE(plaid_item_id, plaid_account_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    name  TEXT NOT NULL UNIQUE,
    description TEXT,
    ignore BOOLEAN DEFAULT false,
    hide BOOLEAN DEFAULT false,
    startDate TEXT,
    endDate TEXT,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Transactions
CREATE TABLE IF NOT EXISTS `transaction` (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    plaid_transaction_id TEXT NOT NULL UNIQUE,
    plaid_account_id TEXT NOT NULL,
    category_id TEXT,
    amount REAL NOT NULL,
    iso_currency_code TEXT,
    unofficial_currency_code TEXT,
    date TEXT NOT NULL,
    datetime TEXT,
    name TEXT NOT NULL,
    payment_channel TEXT,
    pending BOOLEAN NOT NULL DEFAULT 0,
    pending_transaction_id TEXT,
    account_owner TEXT,
    merchant_name TEXT,
    merchant_category_id TEXT,
    merchant_category TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    FOREIGN KEY (plaid_account_id) REFERENCES account(plaid_account_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction_location (
    id TEXT PRIMARY KEY,
    transaction_id TEXT NOT NULL UNIQUE,
    address TEXT,
    city TEXT,
    region TEXT,
    postal_code TEXT,
    country TEXT,
    lat REAL,
    lon REAL,
    FOREIGN KEY (transaction_id) REFERENCES `transaction`(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction_payment_meta (
    id TEXT PRIMARY KEY,
    transaction_id TEXT NOT NULL UNIQUE,
    reference_number TEXT,
    payer TEXT,
    payment_method TEXT,
    payment_processor TEXT,
    ppd_id TEXT,
    reason TEXT,
    by_order_of TEXT,
    payee TEXT,
    FOREIGN KEY (transaction_id) REFERENCES `transaction`(id) ON DELETE CASCADE
);

-- User subscription tiers for sync frequency
CREATE TABLE IF NOT EXISTS user_subscription (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL UNIQUE,
    tier TEXT NOT NULL DEFAULT 'basic', -- 'basic' or 'premium'
    last_sync_at TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_plaid_items_user_id ON plaid_item(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON account(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_plaid_item_id ON account(plaid_item_id);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON `transaction`(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_plaid_account_id ON `transaction`(plaid_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON `transaction`(date);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_id ON user_subscription(user_id); 