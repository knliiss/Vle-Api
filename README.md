# VleApi

This repository contains the VLE backend service. It uses PostgreSQL for relational data and MongoDB for submission documents. OpenAPI (swagger) is enabled.

Quick start (local with Docker Compose)

1. Build the JAR and Docker image:

```bash
./gradlew build
docker compose -f compose.yaml build
```

2. Start the full stack (Postgres, Mongo, PgAdmin, app):

```bash
docker compose -f compose.yaml up
```

3. Open the app and docs:

- App: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- PgAdmin: http://localhost:5050 (use credentials from compose.yaml)

Configuration

The project uses a single `src/main/resources/application.yml` with profile support. Environment variables used in `compose.yaml` map to properties accepted by Spring Boot. Notable envs:

- SPRING_DATASOURCE_URL / SPRING_DATASOURCE_USERNAME / SPRING_DATASOURCE_PASSWORD
- SPRING_DATA_MONGODB_URI
- JWT_SECRET
- CLOUD_S3_BUCKET
- CLOUD_AWS_CREDENTIALS_ACCESS_KEY
- CLOUD_AWS_CREDENTIALS_SECRET_KEY

Security

- JWT authentication is used. The `JwtFilter` reads the token from `Authorization: Bearer <token>` header.
- Roles: ADMINISTRATOR, TEACHER, STUDENT. Grading endpoints are restricted to TEACHER and ADMINISTRATOR.

Development notes

- Run unit tests: `./gradlew test`
- To run the app without Docker for development, ensure Postgres and MongoDB are running locally and set the environment variables accordingly.

What I implemented

- Unified `application.yml` and Docker Compose with `Dockerfile`.
- OpenAPI (springdoc) configuration + Swagger UI.
- DTOs and mappers for main entities; controllers that expose a DTO-based REST API.
- Partial Mongo integration for submissions and cleanup behavior on deletes.
- Basic validation on DTOs and a sample integration test for user create validation.

Next improvements (recommended)

- Add more integration tests covering security and all controllers.
- Replace manual mappers with MapStruct.
- Add pagination and filtering for list endpoints.
- Harden Docker Compose for production (secrets, volumes, healthchecks).

If you want, I can proceed to add full OpenAPI examples, more integration tests, and replace mappers with MapStruct next — tell me "continue" and I'll proceed to the next phase.

