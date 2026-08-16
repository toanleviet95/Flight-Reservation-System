# Tripma — Flight Reservation System

A Spring Boot REST API for managing flights, backed by MySQL.

## Tech Stack

- Java 26
- Spring Boot 4.1.0 (Web, Data JPA, Validation)
- Hibernate / MySQL 8+ (via `mysql-connector-j`)
- HikariCP connection pooling
- springdoc-openapi (Swagger UI)
- Maven

## Prerequisites

- JDK 26
- Maven (no wrapper checked in for macOS/Linux — install Maven locally, e.g. `brew install maven`)
- A running MySQL instance

## Setup

### 1. Database

Create a MySQL database for the app to use. For local development, a Docker container works well:

```bash
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=<your-password> \
  -p 3307:3306 \
  mysql:latest

docker exec mysql mysql -uroot -p<your-password> \
  -e "CREATE DATABASE tripma_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. Environment variables

Copy the example env file and fill in your credentials:

```bash
cp .env.example .env
```

`.env` (git-ignored) is loaded automatically at startup via `spring.config.import` in [application.properties](src/main/resources/application.properties):

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:mysql://localhost:3307/tripma_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |

### 3. Schema

`spring.jpa.hibernate.ddl-auto=update` is enabled, so Hibernate creates/updates tables automatically from the JPA entities on startup — no manual schema migration needed for local dev.

### 4. Run

```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

## API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI spec: http://localhost:8080/v3/api-docs


## Project Structure

```
src/main/java/tripma/local/tripma/
├── controller/   REST controllers
├── service/      Business logic
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities
├── dto/          Request/response records
└── exception/    Global exception handling
```

## CI

Pull requests are automatically reviewed by Claude via GitHub Actions ([.github/workflows/claude_pr_review.yml](.github/workflows/claude_pr_review.yml)). Comment `@claude` on a PR to trigger a re-review.
