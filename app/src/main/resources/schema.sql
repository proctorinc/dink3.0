CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'user',
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    token TEXT NOT NULL,
    expires_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Plaid Items (represents a connection to a financial institution)
CREATE TABLE IF NOT EXISTS plaid_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    plaid_item_id TEXT NOT NULL UNIQUE,
    plaid_access_token TEXT NOT NULL,
    plaid_institution_id TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'good',
    last_webhook TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Institutions
CREATE TABLE IF NOT EXISTS institutions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plaid_institution_id TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    logo TEXT,
    primary_color TEXT,
    url TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- Accounts
CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
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
    FOREIGN KEY (plaid_item_id) REFERENCES plaid_items(plaid_item_id) ON DELETE CASCADE,
    UNIQUE(plaid_item_id, plaid_account_id)
);

-- Transactions
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plaid_transaction_id TEXT NOT NULL UNIQUE,
    plaid_account_id TEXT NOT NULL,
    amount REAL NOT NULL,
    iso_currency_code TEXT,
    unofficial_currency_code TEXT,
    date TEXT NOT NULL,
    datetime TEXT,
    name TEXT NOT NULL,
    merchant_name TEXT,
    payment_channel TEXT,
    pending BOOLEAN NOT NULL DEFAULT 0,
    pending_transaction_id TEXT,
    account_owner TEXT,
    category_id TEXT,
    category TEXT,
    location_address TEXT,
    location_city TEXT,
    location_region TEXT,
    location_postal_code TEXT,
    location_country TEXT,
    location_lat REAL,
    location_lon REAL,
    payment_meta_reference_number TEXT,
    payment_meta_payer TEXT,
    payment_meta_payment_method TEXT,
    payment_meta_payment_processor TEXT,
    payment_meta_ppd_id TEXT,
    payment_meta_reason TEXT,
    payment_meta_by_order_of TEXT,
    payment_meta_payee TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (plaid_account_id) REFERENCES accounts(plaid_account_id) ON DELETE CASCADE
);

-- User subscription tiers for sync frequency
CREATE TABLE IF NOT EXISTS user_subscriptions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    tier TEXT NOT NULL DEFAULT 'basic', -- 'basic' or 'premium'
    last_sync_at TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_plaid_items_user_id ON plaid_items(user_id);
CREATE INDEX IF NOT EXISTS idx_accounts_plaid_item_id ON accounts(plaid_item_id);
CREATE INDEX IF NOT EXISTS idx_transactions_plaid_account_id ON transactions(plaid_account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);
CREATE INDEX IF NOT EXISTS idx_user_subscriptions_user_id ON user_subscriptions(user_id); 