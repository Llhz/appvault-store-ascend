---
name: cy2pw
description: "Convert Cypress E2E tests to Playwright. Use when migrating test files from Cypress (*.cy.js) to Playwright (*.spec.ts), or when the user asks about Cypress-to-Playwright equivalents."
---

# Cypress to Playwright Conversion Skill

Converts Cypress E2E test code to idiomatic Playwright `@playwright/test` code. Based on migration methodology from https://www.cy2pw.com/ and the official Playwright converter at https://demo.playwright.dev/cy2pw/.

## When to Use

- User asks to convert a `.cy.js` / `.cy.ts` file to Playwright
- User asks "what's the Playwright equivalent of …?"
- User mentions "migrate", "convert", "cy2pw", or "cypress to playwright"
- User references Cypress commands and wants Playwright alternatives

## Conversion Reference

### Test Structure

| Cypress | Playwright |
|---------|-----------|
| `describe("Suite", () => {})` | `test.describe("Suite", () => {})` |
| `it("test", () => {})` | `test("test", async ({ page }) => {})` |
| `before(() => {})` | `test.beforeAll(async () => {})` |
| `beforeEach(() => {})` | `test.beforeEach(async ({ page }) => {})` |
| `after(() => {})` | `test.afterAll(async () => {})` |
| `afterEach(() => {})` | `test.afterEach(async ({ page }) => {})` |
| `it.only()` | `test.only()` |
| `it.skip()` | `test.skip()` |

### Navigation & URL

| Cypress | Playwright |
|---------|-----------|
| `cy.visit("/path")` | `await page.goto("/path")` |
| `cy.url().should("include", x)` | `await expect(page).toHaveURL(new RegExp(x))` |
| `cy.go("back")` | `await page.goBack()` |
| `cy.reload()` | `await page.reload()` |

### Selectors & Elements

| Cypress | Playwright |
|---------|-----------|
| `cy.get("selector")` | `page.locator("selector")` |
| `cy.get("sel").first()` | `page.locator("sel").first()` |
| `cy.get("sel").last()` | `page.locator("sel").last()` |
| `cy.get("sel").eq(n)` | `page.locator("sel").nth(n)` |
| `cy.contains("text")` | `page.getByText("text")` |
| `cy.get("sel").find("child")` | `page.locator("sel").locator("child")` |
| `cy.get("sel").parent()` | `page.locator("sel").locator("..")` |
| `cy.focused()` | `page.locator(":focus")` |

### Actions

| Cypress | Playwright |
|---------|-----------|
| `cy.get(s).click()` | `await page.locator(s).click()` |
| `cy.get(s).click({ force: true })` | `await page.locator(s).click({ force: true })` |
| `.dblclick()` | `await page.locator(s).dblclick()` |
| `.type("text")` | `await page.locator(s).fill("text")` |
| `.type("text{enter}")` | `await page.locator(s).fill("text")` then `await page.locator(s).press("Enter")` |
| `.type("text", { delay: 100 })` | `await page.locator(s).pressSequentially("text", { delay: 100 })` |
| `.clear()` | `await page.locator(s).clear()` |
| `.check()` | `await page.locator(s).check()` |
| `.uncheck()` | `await page.locator(s).uncheck()` |
| `.select("value")` | `await page.locator(s).selectOption("value")` |
| `.trigger("mouseover")` | `await page.locator(s).hover()` |
| `.scrollIntoView()` | `await page.locator(s).scrollIntoViewIfNeeded()` |

### Assertions

| Cypress | Playwright |
|---------|-----------|
| `.should("exist")` | `await expect(page.locator(s)).toBeAttached()` |
| `.should("be.visible")` | `await expect(page.locator(s)).toBeVisible()` |
| `.should("not.exist")` | `await expect(page.locator(s)).not.toBeAttached()` |
| `.should("not.be.visible")` | `await expect(page.locator(s)).not.toBeVisible()` |
| `.should("have.text", "x")` | `await expect(page.locator(s)).toHaveText("x")` |
| `.should("contain", "x")` | `await expect(page.locator(s)).toContainText("x")` |
| `.should("have.value", "x")` | `await expect(page.locator(s)).toHaveValue("x")` |
| `.should("have.class", "x")` | `await expect(page.locator(s)).toHaveClass(/x/)` |
| `.should("have.attr", "href", "x")` | `await expect(page.locator(s)).toHaveAttribute("href", "x")` |
| `.should("have.length", n)` | `await expect(page.locator(s)).toHaveCount(n)` |
| `.should("have.length.greaterThan", 0)` | `await expect(page.locator(s).first()).toBeVisible()` |
| `.should("be.checked")` | `await expect(page.locator(s)).toBeChecked()` |
| `.should("be.disabled")` | `await expect(page.locator(s)).toBeDisabled()` |
| `cy.url().should("include", x)` | `await expect(page).toHaveURL(new RegExp(x))` |
| `cy.title().should("eq", x)` | `await expect(page).toHaveTitle(x)` |

### Waiting & Timing

| Cypress | Playwright |
|---------|-----------|
| `cy.wait(1000)` | `await page.waitForTimeout(1000)` *(avoid — use assertions instead)* |
| `cy.wait("@alias")` | `await page.waitForResponse(urlPattern)` |
| `.should("be.visible")` (auto-retry) | Playwright assertions auto-retry by default |
| `cy.intercept().as("alias")` | `await page.route(url, handler)` |

### Network / Intercept

| Cypress | Playwright |
|---------|-----------|
| `cy.intercept("GET", "/api/**").as("req")` | `const resp = page.waitForResponse("/api/**")` |
| `cy.intercept("POST", url, { body: {} })` | `await page.route(url, r => r.fulfill({ body: "{}" }))` |
| `cy.wait("@req")` | `await resp` |

### Fixtures & Data

| Cypress | Playwright |
|---------|-----------|
| `cy.fixture("data.json")` | `JSON.parse(fs.readFileSync("fixtures/data.json", "utf-8"))` |
| `cy.readFile("path")` | `fs.readFileSync("path", "utf-8")` |

### Cookies & Storage

| Cypress | Playwright |
|---------|-----------|
| `cy.setCookie("name", "val")` | `await context.addCookies([{ name, value, url }])` |
| `cy.getCookie("name")` | `(await context.cookies()).find(c => c.name === "name")` |
| `cy.clearCookies()` | `await context.clearCookies()` |

### Custom Commands

| Cypress | Playwright |
|---------|-----------|
| `Cypress.Commands.add("login", fn)` | Create a fixture or helper: `async function login(page) { ... }` |
| `cy.login()` | `await login(page)` |

## Conversion Workflow

When converting a Cypress file:

1. **Read the source** — Read the `.cy.js` / `.cy.ts` file completely
2. **Analyze patterns** — Identify Cypress commands, custom commands, fixtures, intercepts
3. **Check for custom commands** — Read `cypress/support/commands.js` for custom `Cypress.Commands.add()` and create equivalent Playwright helpers or fixtures
4. **Convert structure** — `describe` → `test.describe`, `it` → `test`, add `async ({ page })`
5. **Convert commands** — Apply the mapping tables above for each Cypress command
6. **Add TypeScript imports** — `import { test, expect } from "@playwright/test"`
7. **Handle waits** — Remove explicit `cy.wait(ms)` calls; rely on Playwright auto-waiting
8. **Review assertions** — Convert `.should()` chains to `await expect()` calls
9. **Handle `{enter}` in `.type()`** — Split into `.fill()` + `.press("Enter")`
10. **Validate** — Run the converted test to verify it passes

## Key Differences to Explain to Users

- **Async/await**: Playwright is fully async; every interaction needs `await`
- **Auto-waiting**: Playwright auto-waits for elements, no need for `.should("be.visible")` before clicking
- **Test isolation**: Playwright creates a fresh browser context per test by default
- **Multi-browser**: Playwright runs across Chromium, Firefox, WebKit by default
- **No chaining**: Playwright doesn't chain like Cypress; use separate `await` statements
- **Locator vs Element**: Playwright locators are lazy — they don't query the DOM until an action is performed
- **Network handling**: Playwright uses `page.route()` / `page.waitForResponse()` instead of `cy.intercept()` / `cy.wait()`

## Example Conversion

### Before (Cypress) — `e2e-cypress/cypress/e2e/browse-search.cy.js`
```javascript
describe("Browse & Search", () => {
  it("displays app listing on browse page", () => {
    cy.visit("/browse");
    cy.get("[class*=card], [class*=app]").should("have.length.greaterThan", 0);
  });

  it("filters apps by category", () => {
    cy.visit("/browse");
    cy.get('a[href*="/browse/category/"]').first().click({ force: true });
    cy.url().should("include", "category");
  });

  it("searches for apps by keyword", () => {
    cy.visit("/");
    cy.get('input[name="q"], input[type="search"]').first().type("Focus{enter}");
    cy.url().should("include", "/search");
    cy.contains("Focus").should("exist");
  });
});
```

### After (Playwright) — `e2e-playwright/tests/browse-search.spec.ts`
```typescript
import { test, expect } from "@playwright/test";

test.describe("Browse & Search", () => {
  test("browse page shows apps", async ({ page }) => {
    await page.goto("/browse");
    await expect(page.locator(".card, [class*=app]").first()).toBeVisible();
  });

  test("category filter works", async ({ page }) => {
    await page.goto("/browse");
    await page.locator(".category-pill", { hasText: "Productivity" }).click();
    await expect(page).toHaveURL(/category/);
  });

  test("search returns results for known app", async ({ page }) => {
    await page.goto("/search?q=Focus");
    await expect(page.locator("body")).toContainText("Focus");
  });
});
```

## Resources

- Official Playwright converter: https://demo.playwright.dev/cy2pw/
- Migration curated resources: https://www.cy2pw.com/
- Migration tutorials: https://www.cy2pw.com/tutorials
- Migration tools: https://www.cy2pw.com/tools
- Cypress-to-Playwright OSS tool: https://github.com/11joselu/cypress-to-playwright
