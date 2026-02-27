# Bank Management

## Description
This is a Spring Boot application that provides a simple banking API. It allows users to register, log in, and perform basic banking operations such as depositing, withdrawing, and transferring funds. The application uses a PostgreSQL database to store user and account information and secures the API endpoints using JWT.

## Features
- User registration and authentication
- JWT-based security
- Deposit, withdraw, and transfer funds
- API documentation with OpenAPI

## Technologies Used
- Java 21
- Spring Boot 3.2.5
- Spring Data JPA
- Spring Security
- PostgreSQL
- Lombok
- JWT
- OpenAPI

## API Endpoints
### Authentication
- `POST /auth/register`: Register a new user.
- `POST /auth/login`: Log in an existing user and receive a JWT token.

### Business Logic
- `PATCH /api/users/user/deposit/{amount}`: Deposit a specified amount into the user's account.
- `PATCH /api/users/user/withdraw/{id}/{amount}`: Withdraw a specified amount from the user's account.
- `PATCH /api/users/user/transfer/{email2}/{amount}`: Transfer a specified amount to another user's account.

## How to Get Started
### Prerequisites
- Java 21
- Maven
- PostgreSQL

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/email_entity.git
   ```
2. Configure the database in `src/main/resources/application.yml`.
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   java -jar target/email_entity-0.0.1-SNAPSHOT.jar
   ```
