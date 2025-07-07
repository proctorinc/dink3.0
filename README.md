# dink3

A scalable, feature-based Java backend using Spring Boot, SQLite (Turso), jOOQ, and JWT authentication with Plaid integration for financial data.

## Features
- RESTful API (Spring Boot)
- SQLite (local dev) / Turso (prod) with jOOQ for type-safe SQL
- JWT-based authentication
- **Plaid integration for financial data (accounts, transactions, institutions)**
- Feature-based package structure (e.g., user, auth, accounts, transactions, plaid)
- YAML configuration for dev, prod, and test
- JUnit for unit, integration, and E2E tests

## Project Structure
```
app/
  src/main/java/com/dink3/
    user/        # User feature (controllers, services, repos)
    auth/        # Auth feature (JWT, login, etc.)
    accounts/    # Account operations
    institutions/ # Institution operations
    transactions/ # Transaction operations
    plaid/       # Plaid integration (link tokens, sync, webhooks)
    common/      # Shared code (middleware, error handling, etc.)
    jooq/        # jOOQ generated code (do not edit manually)
  src/main/resources/
    application.yml
    application-dev.yml
    application-prod.yml
    schema.sql   # SQLite schema for dev
```

## Prerequisites
- Java 21+
- Gradle 8+
- SQLite3 CLI (for local DB management)
- **Plaid API credentials (for financial data integration)**

## Running the Application

**From the project root directory:**

```sh
./gradlew :app:bootRun
```

- The server will start on port 8080 by default.
- Config is in `src/main/resources/application.yml` (or `-dev.yml`, `-prod.yml`).

## Plaid Integration Setup

### 1. Get Plaid API Credentials
1. Sign up at [Plaid Dashboard](https://dashboard.plaid.com/)
2. Create a new app
3. Get your `client_id` and `secret` (use sandbox for development)

### 2. Configure Plaid Settings
Update `application.yml` with your Plaid credentials:

```yaml
plaid:
  environment: sandbox
  client-id: "your-plaid-client-id"
  secret: "your-plaid-secret"
  webhook-url: "http://localhost:8080/api/v1/plaid/webhook"
```

### 3. Database Setup
The schema includes tables for:
- `plaid_items` - Plaid connections per user
- `institutions` - Financial institutions
- `accounts` - Bank accounts
- `transactions` - Transaction history
- `user_subscriptions` - User sync frequency tiers

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` — Register a new user
- `POST /api/v1/auth/login` — Login and receive JWT
- `POST /api/v1/auth/refresh` — Refresh JWT
- `GET /api/v1/users/me` — Get current user info (JWT required)

### Plaid Integration
- `POST /api/v1/plaid/link-token` — Create Link token for account connection
- `POST /api/v1/plaid/exchange-token` — Exchange public token for access token
- `POST /api/v1/plaid/sync` — Manual data sync (respects subscription tiers)

### Accounts
- `GET /api/v1/accounts` — Get all user accounts
- `GET /api/v1/accounts/{id}` — Get specific account

### Institutions
- `GET /api/v1/institutions` — Get all institutions
- `GET /api/v1/institutions/{id}` — Get specific institution

### Transactions
- `GET /api/v1/transactions` — Get all user transactions
- `GET /api/v1/transactions/{id}` — Get specific transaction

## Subscription Tiers

The application supports two subscription tiers that affect sync frequency:

- **Basic**: Can sync once every 24 hours
- **Premium**: Can sync anytime (real-time updates via webhooks)

Users start with Basic tier by default.

## Account Linking Flow

1. **Create Link Token**: `POST /api/v1/plaid/link-token`
2. **User connects account** via Plaid Link (frontend)
3. **Exchange Token**: `POST /api/v1/plaid/exchange-token` with public token
4. **Data is synced** automatically and stored in local database
5. **Access data** via `/accounts`, `/transactions`, `/institutions` endpoints

## Running Tests

**From the project root directory:**

- **All tests:**
  ```sh
  ./gradlew :app:test
  ```
- **Unit tests only:**
  (By convention, unit tests are in `src/test/java` and do not require DB)
- **Integration/E2E tests:**
  (Integration tests use MockMvc and a local SQLite DB)

## jOOQ Code Generation

Whenever you change the database schema (edit `schema.sql`):

1. Recreate the dev database:
   ```sh
   sqlite3 app/dink3-dev.db < app/src/main/resources/schema.sql
   ```
2. Generate jOOQ types:
   ```sh
   ./gradlew :app:generateJooq
   ```
- Generated code will appear in `app/src/main/java/com/dink3/jooq/`

## Adding Migrations
- For development, edit `src/main/resources/schema.sql` and recreate the dev DB as above.
- For production, use a migration tool (e.g., Flyway or Liquibase) and apply migrations to Turso.
- **Flyway example:**
  - Add Flyway dependency to `build.gradle.kts`
  - Place migration scripts in `src/main/resources/db/migration/`
  - Run: `./gradlew :app:flywayMigrate`

## Useful Gradle Commands

- **Build the project:**
  ```sh
  ./gradlew :app:build
  ```
- **Run the app:**
  ```sh
  ./gradlew :app:bootRun
  ```
- **Run tests:**
  ```sh
  ./gradlew :app:test
  ```
- **Generate jOOQ types:**
  ```sh
  ./gradlew :app:generateJooq
  ```

## Environment Configuration
- Edit `application.yml`, `application-dev.yml`, or `application-prod.yml` for DB, JWT, and Plaid settings.
- Use `--spring.profiles.active=dev` or `prod` to select config.

---
MIT License
