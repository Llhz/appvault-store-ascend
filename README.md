# AppVault Store

A server-rendered app store marketplace built with Spring Boot, Thymeleaf, and Spring Security. Features role-based access control, app browsing/search, user reviews, and an admin dashboard.

## Prerequisites

- Java 8
- Maven 3.6+
- Node.js (for Cypress and Playwright E2E tests)
- Python 3.9+ (for Robot Framework E2E tests)
- k6 (for performance tests — `brew install k6` on macOS)

## Running the Application

```bash
# Start the app on http://localhost:8080
mvn spring-boot:run
```

The H2 in-memory database is seeded automatically on startup with sample users, categories, apps, and reviews.

### Default Users

| Role  | Email                | Password    |
|-------|----------------------|-------------|
| Admin | admin@appvault.com   | Admin123!   |
| User  | user@appvault.com    | User123!    |
| User  | alice@example.com    | Alice123!   |
| User  | bob@example.com      | Bob12345!   |
| User  | carol@example.com    | Carol123!   |

## Build

```bash
# Build (skip tests)
mvn clean package -DskipTests
```

## Test Suites

### JUnit (Unit Tests)

```bash
# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run a single test method
mvn test -Dtest=UserServiceTest#registerNewUser
```

### Cypress (E2E)

Requires the app to be running on `http://localhost:8080`.

```bash
cd e2e-cypress
npm install          # first time only
npx cypress run      # headless
npx cypress open     # interactive mode
```

### Playwright (E2E)

Requires the app to be running on `http://localhost:8080`. Runs across Chromium, Firefox, and WebKit.

```bash
cd e2e-playwright
npm install                   # first time only
npx playwright install        # install browsers (first time only)
npx playwright test           # headless, all browsers
npx playwright test --ui      # interactive UI mode
npx playwright show-report    # view HTML report after a run
```

### Robot Framework (E2E)

Requires the app to be running on `http://localhost:8080`. Uses the Browser library (Playwright-based).

```bash
cd e2e-robot
python3 -m venv .venv                          # first time only
source .venv/bin/activate                      # activate venv
pip install -r requirements.txt                # first time only
rfbrowser init                                 # install browser binaries (first time only)
robot --outputdir results tests/               # run all tests
```

HTML reports are generated in `e2e-robot/results/` (`report.html`, `log.html`).

### k6 (Performance)

Requires the app to be running on `http://localhost:8080`.

```bash
cd perf-k6

# Smoke test (~50s, 10 VUs)
k6 run smoke-test.js

# Load test (~3.5min, up to 30 VUs)
k6 run load-test.js

# Stress test (~7min, up to 100 VUs)
k6 run stress-test.js

# Auth flow test (~1.5min, 5 VUs)
k6 run auth-flow-test.js
```

## Project Structure

```
src/                    Java source (Spring Boot MVC)
e2e-cypress/            Cypress E2E tests
e2e-playwright/         Playwright E2E tests (TypeScript)
e2e-robot/              Robot Framework E2E tests
perf-k6/                k6 performance test scripts
test-logs/              Test run output logs (git-ignored)
```