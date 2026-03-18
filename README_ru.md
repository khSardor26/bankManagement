# Bank Management API

![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-black?logo=jsonwebtokens)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Ready-2496ED?logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-informational)

REST API на Spring Boot для простой банковской симуляции с JWT-аутентификацией, ролевым доступом, управлением картами, переводами и email-уведомлениями.

## 1. Технологический стек

- Java 21
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven Wrapper (`./mvnw`)
- OpenAPI / Swagger UI

## 2. Что делает проект

- Регистрация и вход пользователей с выдачей JWT-токена.
- Авторизация с учетом ролей (`USER`, `ADMIN`).
- Операции пользователя с картами:
  - добавление карты
  - удаление своей карты
  - пополнение
  - снятие
  - перевод между картами
  - просмотр своего профиля с привязанными картами
- Операции администратора:
  - получение списка пользователей с картами (пагинация)
  - обновление статуса карты (`ACTIVE`, `BLOCKED`, `OUTDATED`)
- Email-уведомления для операций пополнения, снятия и перевода.

## 3. Конфигурация

Проект настраивается через переменные окружения в `src/main/resources/application.yml`.

### 3.1 Обязательные переменные окружения

| Переменная | Обязательна | Значение по умолчанию | Описание |
|---|---|---|---|
| `APP_PORT` | Нет | `8080` | Порт Spring Boot приложения |
| `DB_HOST` | Нет | `localhost` | Хост PostgreSQL |
| `DB_PORT` | Нет | `5432` | Порт PostgreSQL |
| `DB_NAME` | Нет | `postgres` | Имя базы данных |
| `DB_USERNAME` | Да | нет | Имя пользователя БД |
| `DB_PASSWORD` | Да | нет | Пароль БД |
| `MAIL_HOST` | Нет | `smtp.gmail.com` | SMTP хост |
| `MAIL_PORT` | Нет | `587` | SMTP порт |
| `MAIL_USERNAME` | Да | нет | Email аккаунт отправителя |
| `MAIL_PASSWORD` | Да | нет | SMTP app password |
| `JWT_SECRET` | Да | нет | Ключ подписи JWT |
| `JWT_EXPIRATION` | Нет | `86400000` | Время жизни JWT в миллисекундах |

Важно для Gmail SMTP:
- `MAIL_PASSWORD` это не обычный пароль от Gmail-аккаунта.
- Нужно сгенерировать Google App Password и использовать именно его.
- Создается здесь: [https://myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)

### 3.2 Создание локального `.env`

Используйте `.env.example` как шаблон:

```bash
cp .env.example .env
```

После этого заполните `.env` реальными значениями.

## 4. Запуск PostgreSQL через Docker Compose

`docker-compose.yml` использует переменные окружения для учетных данных БД.

### 4.1 Запуск PostgreSQL

```bash
docker compose up -d
```

### 4.2 Проверка контейнера

```bash
docker compose ps
```

### 4.3 Остановка контейнера

```bash
docker compose down
```

### 4.4 Удаление volume (чистый сброс)

```bash
docker compose down -v
```

## 4.5 Запуск приложения в Docker

Сборка образа:

```bash
docker build -t bank-management .
```

Запуск контейнера (использует ваш `.env` файл):

```bash
docker run --env-file .env -p 8080:8080 bank-management
```

Если база данных запущена в Docker, убедитесь что `DB_HOST` в `.env` указывает на имя контейнера БД из `docker-compose.yml`.

## 5. Запуск приложения

### 5.1 Сборка

```bash
./mvnw clean package
```

### 5.2 Запуск

```bash
./mvnw spring-boot:run
```

Базовый URL приложения:

- `http://localhost:8080`

## 6. Документация API (Swagger)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 6.1 Быстрый старт через Swagger

1. Запустите PostgreSQL:
```bash
docker compose up -d
```
2. Запустите приложение:
```bash
./mvnw spring-boot:run
```
3. Откройте Swagger UI:
   - `http://localhost:8080/swagger-ui.html`
4. Зарегистрируйте пользователя через `POST /api/v1/auth/register` (или войдите, если пользователь уже есть).
5. Скопируйте JWT токен из ответа.
6. В Swagger нажмите `Authorize` и вставьте:
```text
Bearer <your-jwt-token>
```
7. Вызывайте защищенные endpoint'ы (user/admin) прямо из Swagger.

## 7. Аутентификация и авторизация

### 7.1 Модель входа

1. Зарегистрируйтесь или войдите через `/api/v1/auth/**`.
2. Получите токен в ответе.
3. Передавайте токен в заголовке:

```http
Authorization: Bearer <your-jwt-token>
```

### 7.2 Правила безопасности

- Публичные endpoint'ы:
  - `/api/v1/auth/**`
  - endpoint'ы Swagger/OpenAPI
- Все остальные endpoint'ы требуют аутентификации.
- Endpoint'ы администратора защищены method security:
  - `@PreAuthorize("hasRole('ADMIN')")`

### 7.3 Роли

| Роль | Возможности |
|---|---|
| `USER` | Управление своими картами, операции с балансом, переводы, просмотр своего профиля |
| `ADMIN` | Все возможности пользователя (при аутентификации) + admin API для списка пользователей и обновления статуса карт |

## 8. API Endpoint'ы

Базовый префикс: `/api/v1`

### 8.1 Endpoint'ы аутентификации

#### `POST /api/v1/auth/register`

Регистрирует пользователя и возвращает JWT.

Тело запроса:

```json
{
  "email": "user@example.com",
  "fullName": "John Doe",
  "password": "secret123",
  "role": "USER"
}
```

#### `POST /api/v1/auth/login`

Аутентифицирует пользователя и возвращает JWT.

Тело запроса:

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

Формат ответа:

```json
{
  "token": "<jwt>",
  "bearer": "Bearer"
}
```

### 8.2 Пользовательские банковские endpoint'ы

Требуют `Authorization: Bearer <token>`.

#### `POST /api/v1/users/user/addCard`

Запрос:

```json
{
  "cardNum": 8600123412341234,
  "balance": 100000,
  "executesAt": "2028-12-31"
}
```

#### `PATCH /api/v1/users/user/deposit`

Запрос:

```json
{
  "cardNum": 8600123412341234,
  "amount": 5000
}
```

#### `PATCH /api/v1/users/user/withdraw`

Запрос:

```json
{
  "cardNum": 8600123412341234,
  "amount": 3000
}
```

#### `PATCH /api/v1/users/user/transfer`

Запрос:

```json
{
  "fromCard": 8600123412341234,
  "toCard": 8600432112345678,
  "amount": 10000
}
```

#### `GET /api/v1/users/user/me`

Возвращает профиль текущего пользователя и его карты.

#### `DELETE /api/v1/users/user/delete/{cardNum}`

Удаляет одну из карт аутентифицированного пользователя.

### 8.3 Endpoint'ы администратора

Требуют аутентифицированного пользователя с ролью `ADMIN`.

#### `GET /api/v1/admin/users?page=0&size=10`

Возвращает страницу пользователей с картами.

#### `PATCH /api/v1/admin/users/update`

Запрос:

```json
{
  "cardNum": 8600123412341234,
  "status": "BLOCKED"
}
```

Допустимые значения статуса:

- `ACTIVE`
- `BLOCKED`
- `OUTDATED`

## 9. Кратко о бизнес-потоках

### 9.1 Поток регистрации/входа

1. Пользователь регистрируется с email/паролем/ролью.
2. Пароль кодируется через BCrypt.
3. JWT генерируется с email в subject и claim роли.
4. Пользователь использует токен для защищенных endpoint'ов.

### 9.2 Пополнение/Снятие/Перевод

1. Текущий пользователь определяется из `SecurityContext`.
2. Выполняются проверки владения картой-источником.
3. Баланс обновляется в БД.
4. Отправляется email-уведомление.
5. В ответе возвращается актуальное состояние баланса карты.

### 9.3 Администрирование карт

1. Администратор отправляет запрос на изменение статуса карты.
2. Карта ищется по `cardNum`.
3. Статус изменяется и сохраняется.

## 10. Обзор модели данных

### 10.1 `User`

- `id`
- `email` (unique)
- `fullName`
- `password`
- `role` (`USER` / `ADMIN`)
- timestamps
- связь one-to-many с `Card`

### 10.2 `Card`

- `id`
- `cardNumber` (unique)
- `balance`
- `status`
- `executesAt`
- связь many-to-one с `User`

## 11. Структура проекта

```text
src/main/java/org/example/email_entity
├── controller
├── dto
├── entity
├── repository
├── security
├── service
└── exception
```

## 12. Обработка ошибок и примечания

- Глобальная обработка ошибок централизована в:
  - `org.example.email_entity.exception.GlobalExceptionHandler`
  - `org.example.email_entity.exception.ApiErrorResponse`
- Исключения из сервисов возвращаются в едином JSON-формате.
- Runtime-бизнес ошибки (например: карта не найдена, отрицательная сумма, недостаточно средств) возвращаются как `400 Bad Request`.
- Ошибки доступа администратора возвращаются как `403 Forbidden`.
- Ошибки аутентификации возвращаются как `401 Unauthorized`.
- Ошибки валидации и некорректного тела запроса возвращаются как `400 Bad Request`.
- Непредвиденные серверные ошибки возвращаются как `500 Internal Server Error`.
- Ошибки ограничений БД при регистрации возвращаются как `409 Conflict`.

Стандартный формат ошибки:

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

## 13. Чеклист перед публикацией

Перед `git push` на GitHub:

1. Убедитесь, что реальные креды хранятся только в локальном `.env`.
2. Убедитесь, что `.env` игнорируется и не отслеживается Git.
3. Оставьте в `.env.example` только плейсхолдеры.
4. Проверьте, что `JWT_SECRET` достаточно сильный (минимум 32+ байта для HS256).
5. Выполните:

```bash
./mvnw clean test
```

## 14. Полезные команды

```bash
# Запуск базы данных
docker compose up -d

# Запуск приложения
./mvnw spring-boot:run

# Запуск тестов
./mvnw test

# Сборка jar
./mvnw clean package
```

## 15. Лицензия

Проект распространяется по лицензии MIT.
