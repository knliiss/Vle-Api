# Virtual Learning Environment (VLE) API

📌 Опис

### VLE API — це RESTful API для віртуального навчального середовища, побудоване з використанням Spring Boot і Gradle.
#### API дозволяє керувати користувачами, групами, курсами та забезпечує аутентифікацію через JWT.

⸻

 Запуск проєкту

1. ### Клонування репозиторію

```` 
git clone <repository-url>
cd <repository-directory>
````

⸻

2. ### Налаштування бази даних

Переконайся, що PostgreSQL запущено локально або через Docker.

Налаштуй підключення у src/main/resources/application-postgres.yml:

```yaml
spring:
  datasource:
    url: your-database-url
    username: your-username
    password: your-password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

⸻

3. ### Додай секрет JWT (НЕ коміть у git)

Створи файл ``` src/main/resources/application-secrets.yml```:

```yaml
jwt:
  secret: your-super-secret-key
```

І додай до .gitignore:

/src/main/resources/application-secrets.yml


⸻

4. ### Запуск програми

Через IntelliJ / IDE:

Запусти клас VleApiApplication.java.

Через термінал:

```bash
./gradlew bootRun
```

⸻

🔐 Аутентифікація (JWT)

Логін

POST /api/v1/auth/login

```json
{
    "username": "admin",
    "password": "admin123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Додавай токен у кожен запит:

```
Authorization: Bearer <token>
```

⸻

### Приклад запиту на оновлення користувача

PUT /api/v1/users/1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

```json
{
  "username": "newUsername",
  "password": "newPassword",
  "avatarUrl": "https://example.com/avatar.png",
  "role": "STUDENT"
}
```

## status: 200 OK
```json
{
  "id": 1,
  "username": "newUsername",
  "role": "STUDENT",
  "groupId": 44312,
  "courseIds": []
}
```


