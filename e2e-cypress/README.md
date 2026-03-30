# AppVault Store — Cypress E2E Tests

## Prerequisites

- Node.js 18+
- AppVault application running on `http://localhost:8080`

## Setup

```bash
cd e2e-cypress
npm install
```

## Running Tests

```bash
# Start the application first (in another terminal)
cd .. && mvn spring-boot:run

# Interactive mode (opens Cypress Test Runner UI)
npm run cy:open

# Headless mode (CI-friendly)
npm run cy:run

# Headed mode (visible browser)
npm run cy:run:headed
```

## Test Structure

| File | Description |
|------|-------------|
| `home.cy.js` | Home page loads, categories, navigation |
| `browse-search.cy.js` | App browsing, category filter, search |
| `auth.cy.js` | Login, register, auth redirects |
| `user-features.cy.js` | Profile, reviews (authenticated) |
| `admin.cy.js` | Dashboard, app CRUD, user list (admin) |

## Custom Commands

- `cy.login(email, password)` — Log in via the login form
- `cy.loginAsAdmin()` — Log in as `admin@appvault.com`
- `cy.loginAsUser()` — Log in as `user@appvault.com`
- `cy.logout()` — Log out
