# AppVault Store — Playwright E2E Tests

## Prerequisites

- Node.js 18+
- AppVault application running on `http://localhost:8080`

## Setup

```bash
cd e2e-playwright
npm install
npx playwright install
```

## Running Tests

```bash
# Start the application first (in another terminal)
cd .. && mvn spring-boot:run

# Run all tests (headless, all browsers)
npm test

# Run with visible browser
npm run test:headed

# Run with Playwright UI mode (interactive)
npm run test:ui

# Run in debug mode (step through tests)
npm run test:debug

# Open HTML report after running
npm run report
```

## Test Structure

| File | Description |
|------|-------------|
| `home.spec.ts` | Home page rendering, categories, navigation |
| `browse-search.spec.ts` | App browsing, filtering, search |
| `app.spec.ts` | Auth flows, user features, admin CRUD, security |

## Browser Coverage

Tests run on three browsers by default:
- Chromium
- Firefox
- WebKit (Safari)

## CI Configuration

In CI, tests run with:
- 1 worker (sequential)
- 2 retries on failure
- Traces captured on first retry
