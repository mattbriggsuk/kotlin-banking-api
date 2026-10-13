# Kotlin Banking API Playground

This is a Kotlin/JVM conversion of the Java Banking API Playground. It demonstrates Kotlin 2.4.20, JDK 25, Spring Boot 3.5, Jakarta Bean Validation, Maven multi-module projects, in-memory repositories, JUnit 5, and Mockito Kotlin.

> Demonstration only: this learning project is not a complete or secure production banking platform.

## Features

- Register customers with a unique, case-insensitive email address.
- Generate a six-digit customer PIN and store only its salted hash.
- Log in with either a customer number or email address.
- Use expiring bearer sessions and log out to revoke a session.
- Retrieve a customer and list their accounts with ownership checks.
- Open `CURRENT`, `SAVINGS`, and `CREDIT` accounts.
- Deposit, withdraw, and transfer money.
- Generate and validate 16-digit Luhn customer and account numbers.

## Modules

| Module | Responsibility |
|---|---|
| `api` | Spring Boot entry point, controllers, bearer authentication, and HTTP error mapping. |
| `business` | Customer registration, login/session, account, and ownership rules plus unit tests. |
| `domain` | Models, DTOs, enums, exceptions, and service/repository contracts. |
| `repository` | Thread-safe in-memory customer, credential, and account storage. |
| `utils` | Number generation, PIN hashing, and shared validation. |

The root project is a Maven aggregator with `pom` packaging. It contains shared build configuration and lists the modules, but it does not contain application source code or need a root `src` directory. Each top-level module is an independent Maven project with its own `pom.xml` and conventional source tree:

```text
kotlin-banking-api/
├── pom.xml
├── api/
│   ├── pom.xml
│   └── src/main/kotlin/
├── business/
│   ├── pom.xml
│   ├── src/main/kotlin/
│   └── src/test/kotlin/
├── domain/
│   ├── pom.xml
│   └── src/main/kotlin/
├── repository/
│   ├── pom.xml
│   └── src/main/kotlin/
└── utils/
    ├── pom.xml
    └── src/main/kotlin/
```

The conversion uses Kotlin data classes for models and DTOs, nullable types at deserialization boundaries, constructor injection, object declarations for stateless utilities, and `@Synchronized` for the same single-instance coordination as the Java project.

## Requirements and commands

- JDK 25
- Maven 3.9+

```shell
mvn clean package
mvn -pl business -am test
mvn -pl api -am spring-boot:run
```

The API runs at `http://localhost:8080`. IntelliJ IDEA can also run `com.example.banking.api.MainKt`.

## Endpoints

| Method | Route | Authentication |
|---|---|---|
| `POST` | `/api/customer/register` | Public |
| `POST` | `/api/access/login` | Public |
| `POST` | `/api/access/logout` | Bearer token |
| `GET` | `/api/customer/get?customerNumber=...` | Bearer token; own customer only |
| `GET` | `/api/customer/accounts?customerNumber=...` | Bearer token; own customer only |
| `POST` | `/api/account/create` | Bearer token; creates for the signed-in customer |
| `POST` | `/api/account/close` | Bearer token; own account only |
| `POST` | `/api/account/deposit` | Bearer token; own target account only |
| `POST` | `/api/account/withdraw` | Bearer token; own target account only |
| `POST` | `/api/account/transfer` | Bearer token; own source account only |

The `.client/http` files contain ordered request sequences for IntelliJ's HTTP client.

Expected business failures map to HTTP statuses: invalid arguments are `400`, missing records are `404`, and conflicting account state is `409`.

## Scope

State is lost at shutdown. The project intentionally omits durable storage, database transactions, a ledger, idempotency, audit logging, fraud controls, and operational resilience. It is a compact teaching example rather than a financial system.

## Account types

`POST /api/account/create` accepts an `accountType` of `CURRENT`, `SAVINGS`, or `CREDIT`:

```json
{"accountType":"SAVINGS"}
```

The customer number comes from the authenticated `UserSession`; clients cannot create an account for another customer. The selected type is stored on the account and returned as `accountType` in all account DTOs. Omitting the field defaults to `CURRENT`. Explicit null and unrecognised values are rejected with HTTP 400. Types currently share the same balance rules.

## Customer PIN login and sessions

Registration returns `customer`, a generated six-digit `pin`, and a `sessionToken`. Keep the PIN as a string, including leading zeros. The PIN is returned only by registration and stored only as a salted PBKDF2-HMAC-SHA256 hash (600,000 iterations); customer lookup, account lists and login responses do not disclose it.

`POST /api/access/login` accepts `{"identifier":"customer number or email","pin":"012345"}` and returns `customer` plus a new `sessionToken`. Emails are trimmed and matched without case sensitivity; duplicate email registration returns HTTP 409. Incorrect credentials use one generic error message.

Send `Authorization: Bearer <sessionToken>` with customer lookup, account listing, and all account operations. Sessions expire 30 minutes after creation. `POST /api/access/logout` revokes the supplied session and returns HTTP 204. Missing, invalid or expired sessions return HTTP 401. Access to another customer's records or source accounts returns HTTP 403. Transfers can still credit another customer's account.

Controllers declare authentication with `@RequireAccess`. A Spring Security filter validates the bearer token and exposes its `UserSession` through `@AuthenticationPrincipal`. Business services use that trusted session to enforce customer and account ownership.

All backend state, including PIN hashes and sessions, disappears on restart. Register again after restarting the API.

Hashing reference: [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).
