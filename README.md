# Bank management Api

![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-black?logo=jsonwebtokens)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Ready-2496ED?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-informational)

A Spring Boot REST API for a simple bank simulation with JWT authentication, role-based access, card management, transfer operations, and email notifications.

## 1. Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven Wrapper (`./mvnw`)
- OpenAPI / Swagger UI

## 2. What This Project Does

- User registration and login with JWT token issuance.
- Role-aware authorization (`USER`, `ADMIN`).
- User card operations:
  - add card
  - delete own card
  - deposit
  - withdraw
  - transfer between cards
  - view own profile with linked cards
- Admin operations:
  - list users with cards (paginated)
  - update card status (`ACTIVE`, `BLOCKED`, `OUTDATED`)
- Email notifications for deposit, withdraw, and transfer events.

## 3. Configuration

The project is configured through environment variables in `src/main/resources/application.yml`.

### 3.1 Required Environment Variables

| Variable | Required | Default | Description |
|---|---|---|---|
| `APP_PORT` | No | `8080` | Spring Boot server port |
| `DB_HOST` | No | `localhost` | PostgreSQL host |
| `DB_PORT` | No | `5432` | PostgreSQL port |
| `DB_NAME` | No | `postgres` | Database name |
| `DB_USERNAME` | Yes | none | Database username |
| `DB_PASSWORD` | Yes | none | Database password |
| `MAIL_HOST` | No | `smtp.gmail.com` | SMTP host |
| `MAIL_PORT` | No | `587` | SMTP port |
| `MAIL_USERNAME` | Yes | none | Sender email account |
| `MAIL_PASSWORD` | Yes | none | SMTP app password |
| `JWT_SECRET` | Yes | none | JWT signing key |
| `JWT_EXPIRATION` | No | `86400000` | JWT TTL in milliseconds |

Important for Gmail SMTP:
- `MAIL_PASSWORD` is not your regular Gmail login password.
- You must generate a Google App Password and use that value.
- Create it here: [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)

### 3.2 Create Local `.env`

Use `.env.example` as a template:

```bash
cp .env.example .env
```

Then fill real values in `.env`.

## 4. Run PostgreSQL with Docker Compose

`docker-compose.yml` uses environment variables for DB credentials.

### 4.1 Start PostgreSQL

```bash
docker compose up -d
```

### 4.2 Check Container

```bash
docker compose ps
```

### 4.3 Stop Container

```bash
docker compose down
```

### 4.4 Remove Volumes (clean reset)

```bash
docker compose down -v
```

## 4.5 Run the App in Docker

Build the image:

```bash
docker build -t bank-management .
```

Run the container (uses your `.env` file):

```bash
docker run --env-file .env -p 8080:8080 bank-management
```

If the database is running in Docker, make sure `DB_HOST` in `.env` points to the DB container name from `docker-compose.yml`.

## 5. Run the Application

### 5.1 Build

```bash
./mvnw clean package
```

### 5.2 Start

```bash
./mvnw spring-boot:run
```

Application base URL:

- `http://localhost:8080`

## 6. API Documentation (Swagger)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 6.1 Swagger Quick Start

1. Start PostgreSQL:
```bash
docker compose up -d
```
2. Start the application:
```bash
./mvnw spring-boot:run
```
3. Open Swagger UI:
   - `http://localhost:8080/swagger-ui.html`
4. Register a user at `POST /api/v1/auth/register` (or login if already created).
5. Copy the JWT token from the response.
6. In Swagger, click `Authorize` and paste:
```text
Bearer <your-jwt-token>
```
7. Call protected endpoints (user/admin) directly from Swagger.

## 7. Authentication and Authorization

### 7.1 Login Model

1. Register or login at `/api/v1/auth/**`.
2. Receive token in response.
3. Send token as header:

```http
Authorization: Bearer <your-jwt-token>
```

### 7.2 Security Rules

- Public endpoints:
  - `/api/v1/auth/**`
  - Swagger/OpenAPI endpoints
- All other endpoints require authentication.
- Admin endpoints are guarded with method security:
  - `@PreAuthorize("hasRole('ADMIN')")`

### 7.3 Roles

| Role | Capabilities |
|---|---|
| `USER` | Manage own cards, do balance operations, transfer, view own profile |
| `ADMIN` | Everything a user can do (if authenticated) + admin APIs for user list and card status updates |

## 8. API Endpoints

Base path: `/api/v1`

### 8.1 Auth Endpoints

#### `POST /api/v1/auth/register`

Registers a user and returns JWT.

Request body:

```json
{
  "email": "user@example.com",
  "fullName": "John Doe",
  "password": "secret123",
  "role": "USER"
}
```

#### `POST /api/v1/auth/login`

Authenticates and returns JWT.

Request body:

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

Response shape:

```json
{
  "token": "<jwt>",
  "bearer": "Bearer"
}
```

### 8.2 User Banking Endpoints

Require `Authorization: Bearer <token>`.

#### `POST /api/v1/users/user/addCard`

Request:

```json
{
  "cardNum": 8600123412341234,
  "balance": 100000,
  "executesAt": "2028-12-31"
}
```

#### `PATCH /api/v1/users/user/deposit`

Request:

```json
{
  "cardNum": 8600123412341234,
  "amount": 5000
}
```

#### `PATCH /api/v1/users/user/withdraw`

Request:

```json
{
  "cardNum": 8600123412341234,
  "amount": 3000
}
```

#### `PATCH /api/v1/users/user/transfer`

Request:

```json
{
  "fromCard": 8600123412341234,
  "toCard": 8600432112345678,
  "amount": 10000
}
```

#### `GET /api/v1/users/user/me`

Returns current user profile + cards.

#### `DELETE /api/v1/users/user/delete/{cardNum}`

Deletes one of the authenticated user cards.

### 8.3 Admin Endpoints

Require authenticated user with `ADMIN` role.

#### `GET /api/v1/admin/users?page=0&size=10`

Returns paginated users with cards.

#### `PATCH /api/v1/admin/users/update`

Request:

```json
{
  "cardNum": 8600123412341234,
  "status": "BLOCKED"
}
```

Allowed status values:

- `ACTIVE`
- `BLOCKED`
- `OUTDATED`

## 9. Business Flow Summary

### 9.1 Register/Login Flow

1. User registers with email/password/role.
2. Password is encoded with BCrypt.
3. JWT token is generated with email subject and role claim.
4. User uses token for authenticated endpoints.

### 9.2 Deposit/Withdraw/Transfer

1. Current user is resolved from `SecurityContext`.
2. Card ownership checks are performed for the source card.
3. Balance is updated in DB.
4. Notification email is sent.
5. Operation result returns current card balance summary.

### 9.3 Admin Card Moderation

1. Admin requests card status update.
2. Card is fetched by `cardNum`.
3. Status is changed and persisted.

## 10. Data Model Overview

### 10.1 `User`

- `id`
- `email` (unique)
- `fullName`
- `password`
- `role` (`USER` / `ADMIN`)
- timestamps
- one-to-many relation to `Card`

### 10.2 `Card`

- `id`
- `cardNumber` (unique)
- `balance`
- `status`
- `executesAt`
- many-to-one relation to `User`

## 11. Project Structure

```text
src/main/java/org/example/email_entity
├── controller
├── dto
├── entity
├── repository
├── security
└── service
```

## 12. Error Behavior and Notes

- Global error handling is centralized in:
  - `org.example.email_entity.exception.GlobalExceptionHandler`
  - `org.example.email_entity.exception.ApiErrorResponse`
- Service methods that throw exceptions are now returned as a consistent JSON error response shape.
- Runtime business errors (for example: card not found, negative amount, not enough money) are returned as `400 Bad Request`.
- Admin access violations are returned as `403 Forbidden`.
- Authentication failures are returned as `401 Unauthorized`.
- Validation and malformed payload issues are returned as `400 Bad Request`.
- Unexpected server-side failures are returned as `500 Internal Server Error`.
- Registration-related DB constraint errors are returned as `409 Conflict`.

Standard error response format:

```json
{
  "timestamp": "2026-03-05T13:20:30.123+05:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Card not found",
  "path": "/api/v1/users/user/deposit",
  "details": null
}
```

## 13. Publishing Checklist

Before pushing to GitHub:

1. Confirm real credentials are only in local `.env`.
2. Confirm `.env` is ignored and not tracked.
3. Keep `.env.example` with placeholders only.
4. Verify `JWT_SECRET` is strong and at least 32+ bytes for HS256 safety.
5. Run:

```bash
./mvnw clean test
```

## 14. Useful Commands

```bash
# Start database
docker compose up -d

# Run app
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build jar
./mvnw clean package
```
