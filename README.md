# dink3

A scalable, feature-based Java backend using Spring Boot, SQLite (Turso), jOOQ, and JWT authentication.

## Features
- RESTful API (Spring Boot)
- SQLite (local dev) / Turso (prod) with jOOQ for type-safe SQL
- JWT-based authentication
- Feature-based package structure (e.g., user, auth)
- YAML configuration for dev, prod, and test
- JUnit for unit, integration, and E2E tests

## Project Structure
```
app/
  src/main/java/com/dink3/
    user/        # User feature (controllers, services, repos)
    auth/        # Auth feature (JWT, login, etc.)
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

## Running the Application

**From the project root directory:**

```sh
./gradlew :app:bootRun
```

- The server will start on port 8080 by default.
- Config is in `src/main/resources/application.yml` (or `-dev.yml`, `-prod.yml`).

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
- Edit `application.yml`, `application-dev.yml`, or `application-prod.yml` for DB, JWT, and other settings.
- Use `--spring.profiles.active=dev` or `prod` to select config.

## API Endpoints
- `POST /api/v1/auth/register` — Register a new user
- `POST /api/v1/auth/login` — Login and receive JWT
- `POST /api/v1/auth/refresh` — Refresh JWT
- `GET /api/v1/users/me` — Get current user info (JWT required)

---
MIT License
