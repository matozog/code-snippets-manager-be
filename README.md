# Code Snippets Manager – backend

REST API for the [Snippets Manager](https://matozog.github.io/code-snippets-manager-fe/) application.
Spring Boot 4, Java 21, PostgreSQL with Flyway migrations.

Users sign in with Google: every request carries the user's Google ID token as a Bearer token, the API verifies it
(signature, issuer, expiry and the OAuth client id) and every user sees only their own snippets and tags.

## Running locally

Requirements: JDK 21, Docker and the Google OAuth client id (see below).

```bash
GOOGLE_CLIENT_ID=<client-id>.apps.googleusercontent.com ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile starts PostgreSQL from `compose.yaml` automatically (exposed on `localhost:55432`,
database/user/password: `snippets`), enables Swagger UI at http://localhost:8090/swagger-ui/index.html
and allows requests from the frontend dev server (`http://localhost:3000`).
In IntelliJ set *Active profiles* to `dev`, *Working directory* to this folder (`code-snippets-manager-be`, otherwise
`compose.yaml` is not found) and the `GOOGLE_CLIENT_ID` environment variable in the run configuration.
Without `GOOGLE_CLIENT_ID` the application does not start.

To remove the local database: `docker compose down -v`.

## Tests

```bash
./mvnw verify
```

Tests run against PostgreSQL started with Testcontainers, so Docker has to be running.
Authentication is simulated in tests (`TestUsers`), no Google account is needed.

## Google sign-in setup

1. In [Google Cloud Console](https://console.cloud.google.com/apis/credentials) create an *OAuth client ID*
   of type *Web application*.
2. Add `http://localhost:3000` and `https://matozog.github.io` to *Authorized JavaScript origins*.
3. Pass the client id to the API in the `GOOGLE_CLIENT_ID` environment variable and set the same value as
   `VITE_GOOGLE_CLIENT_ID` in the frontend. There is no client secret in this setup.

## Database schema

The schema is managed by Flyway migrations in `src/main/resources/db/migration`; Hibernate only validates it
(`ddl-auto=validate`). Every schema change needs a new migration file (`V2__description.sql`, ...).

## Deployment (Render + Neon)

The service runs on Render from the `Dockerfile`; the database is a free PostgreSQL on [Neon](https://neon.com).
Environment variables to set on Render:

| Variable      | Example                                                                   |
| ------------- | ------------------------------------------------------------------------- |
| `DB_URL`      | `jdbc:postgresql://ep-example-123456.eu-central-1.aws.neon.tech/neondb?sslmode=require` |
| `DB_USER`     | `neondb_owner`                                                            |
| `DB_PASSWORD` | password from the Neon console                                            |
| `GOOGLE_CLIENT_ID` | `1234567890-abc.apps.googleusercontent.com`                          |

Neon shows a `postgresql://user:password@host/db?sslmode=require` connection string – for `DB_URL` use
`jdbc:postgresql://host/db?sslmode=require` and pass the user and password separately.
`PORT` is provided by Render automatically. Migrations are applied on application start.
