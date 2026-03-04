# A demo-wiremock-app

This `demo-wiremock-app` is a compact Spring Boot service that demonstrates how to test and integrate outbound HTTP calls with WireMock. Think of it as a small "API lab": your app exposes endpoints, forwards requests to a mocked upstream service, and gives you repeatable behavior for local development and testing.

It is designed for engineers who want a practical starter for:
- building REST endpoints with Spring Boot,
- mocking external dependencies with WireMock,
- toggling rich request/response logging when troubleshooting.

## Project Requirements

To build and run this project locally, make sure you have:
- Java 25
- Docker and Docker Compose (for WireMock and containerized runs)
- A Unix-like shell (`bash`/`zsh`) or compatible terminal on Windows

The project includes Maven Wrapper (`./mvnw`), so a global Maven install is optional.

## Dependencies

Core dependencies are managed in `pom.xml`:
- `spring-boot-starter-web` for REST APIs
- `spring-boot-starter-actuator` for health/info endpoints
- `springdoc-openapi-starter-webmvc-ui` for API docs in `dev`
- `spring-boot-starter-test`, `spring-boot-testcontainers`, and JUnit Jupiter for testing

Runtime behavior also depends on:
- WireMock container: `wiremock/wiremock:2.32.0`
- Spring Boot 3.5.10

## Getting Started

To get started with this project, you can clone the repository to your local machine. Once you have cloned the repository, you can import it into your favorite IDE.

### Environment Setup

* The project uses SDKMAN for managing Java and Maven versions.
* Initialize your development environment using **SDKMAN** CLI and sdkman env file [`sdkmanrc`](.sdkmanrc)

```shell
sdk env install
sdk env
```

#### Note: To install SDKMAN refer: [sdkman.io](https://sdkman.io/install)

---

### 1. Build the Application

Use Maven Wrapper to compile and package:

```bash
sdk env
export DOCKER_DEFAULT_PLATFORM=linux/amd64
./mvnw clean package
```

### 2. Run Unit/Context Tests

```bash
sdk env
export DOCKER_DEFAULT_PLATFORM=linux/amd64
./mvnw clean test
```

Current tests verify that the Spring application context loads successfully.

## How to Run the Application

### Option A: Run Locally (App + WireMock via Compose)

Because `spring.docker.compose.enabled=true` is set in `application.properties`, Spring Boot can manage `compose.yml` lifecycle when the app starts.

```bash
sdk env
export DOCKER_DEFAULT_PLATFORM=linux/amd64
./mvnw spring-boot:run
```

App base URL:

```text
http://localhost:8080/app
```

### Option B: Run in Dev Profile (OpenAPI + verbose logging)

The `dev` profile enables Swagger UI and richer logging settings.

```bash
sdk env
export DOCKER_DEFAULT_PLATFORM=linux/amd64
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Useful links in `dev` profile:

Swagger UI: http://localhost:8080/app/swagger-ui.html

Actuator:   http://localhost:8080/app/actuator

### Option C: Run Fully Containerized

`compose-dev.yml` starts both the app and WireMock as containers.

```bash
docker compose -f compose-dev.yml up --build
```

## API Overview

The controller is mounted at `/api` under the app context path `/app`.

- `GET /app/api/data` retrieves JSON from the external mock (`/api/json`)
- `POST /app/api/data` forwards JSON to the external mock (`/api/anything`)

### Example: GET Data

```bash
curl -s http://localhost:8080/app/api/data
```

Output:

```json
{
  "slideshow": {
    "title": "Sample Slide Show"
  }
}
```

### Example: POST Data

```bash
curl -s -X POST http://localhost:8080/app/api/data \
  -H 'Content-Type: application/json' \
  -d '{"id":101,"data":"Test data"}'
```

Output:

```json
{
  "json": {
    "data": "Test data",
    "id": 101
  },
  "method": "POST"
}
```

## Configuration Notes

Key default settings from `application.properties`:

- Server port: `8080`
- Context path: `/app`
- WireMock base URL: [HTTP] `http://localhost:8081/api` and for [HTTPS] `https://localhost:8443/api`
- OpenAPI disabled by default, enabled in `dev`

In practice, this separation keeps production-like defaults clean while making local debugging easier in the `dev` profile.

---

## Conclusion

This project gives you a practical template for building a Spring Boot API that depends on external HTTP services while staying testable and predictable. With WireMock-backed responses, profile-based configuration, and optional deep logging, you can debug behavior quickly without depending on unstable third-party environments.

If you want to extend it, good next steps are adding contract tests for endpoint payloads and broadening WireMock mappings for error-path simulation.

Happy coding! ✌️

---
