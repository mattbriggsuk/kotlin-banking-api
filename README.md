# Kotlin Banking API Playground

This is a Kotlin/JVM conversion of the Java Banking API Playground. It demonstrates Kotlin 2.4.20, JDK 25, Spring Boot 3.5, Jakarta Bean Validation, Maven multi-module projects, in-memory repositories, JUnit 5, and Mockito Kotlin.

> Demonstration only: this learning project is not a complete or secure production banking platform.

## Features

- Register and retrieve customers.
- List a customer's accounts.
- Open and close accounts.
- Deposit, withdraw, and transfer money.
- Generate and validate 16-digit Luhn customer and account numbers.

## Modules

| Module | Responsibility |
|---|---|
| `api` | Spring Boot entry point, controllers, and HTTP exception mapping. |
| `business` | Customer and account rules plus unit tests. |
| `domain` | Models, DTOs, enums, and service/repository contracts. |
| `repository` | Thread-safe in-memory storage. |
| `utils` | Number generation and shared validation. |

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

| Method | Route |
|---|---|
| `POST` | `/api/customer/register` |
| `GET` | `/api/customer/get?customerNumber=...` |
| `GET` | `/api/customer/accounts?customerNumber=...` |
| `POST` | `/api/account/create` |
| `POST` | `/api/account/close` |
| `POST` | `/api/account/deposit` |
| `POST` | `/api/account/withdraw` |
| `POST` | `/api/account/transfer` |

The `.client/http` files contain ordered request sequences for IntelliJ's HTTP client.

Expected business failures map to HTTP statuses: invalid arguments are `400`, missing records are `404`, and conflicting account state is `409`.

## Scope

State is lost at shutdown. The project intentionally omits authentication, authorization, durable storage, database transactions, a ledger, idempotency, audit logging, fraud controls, and operational resilience. It is a compact teaching example rather than a financial system.
