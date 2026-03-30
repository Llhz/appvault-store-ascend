# Module 05 — Testing Integration

> **Goal**: Generate and run tests **per feature** across **all test frameworks** — Robot Framework, Cypress, Playwright, k6 — using the project's **custom test-runner agents** to automate execution and analysis.

> ⏱️ **Duration**: ~15–20 minutes

> 💡 **Each section is independent** — pick only the features you implemented. You don't need to complete all modules to test here.

---

## 🎯 Objectives

- Generate E2E tests for each feature you built — in Robot Framework, Cypress, and/or Playwright
- Use the **custom test-runner agents** to run tests and analyze results
- Generate JUnit 5 unit tests for service-layer code
- Generate k6 load tests for new API endpoints
- See how Copilot produces idiomatic test code for each framework

---

## Test Frameworks & Custom Agents

This project has **four E2E/performance frameworks**, each with a **custom Copilot agent** that knows how to run it:

| Framework | Test Location | Custom Agent | Reference Files |
|-----------|--------------|--------------|-----------------|
| **Robot Framework** | `e2e-robot/tests/` | `@Robot Framework Test Runner` | `home.robot`, `browse_search.robot`, `auth.robot` |
| **Cypress** | `e2e-cypress/cypress/e2e/` | `@Cypress Test Runner` | `home.cy.js`, `browse-search.cy.js`, `auth.cy.js` |
| **Playwright** | `e2e-playwright/tests/` | `@Playwright Test Runner` | `home.spec.ts`, `browse-search.spec.ts`, `app.spec.ts` |
| **k6** | `perf-k6/` | `@k6 Performance Test Runner` | `smoke-test.js`, `load-test.js` |
| **JUnit 5** | `src/test/` | *(run with `mvn test`)* | Existing test classes |

### How the Custom Agents Work

Each agent follows a **3-phase pattern** (defined in `.github/skills/test-runner/SKILL.md`):

1. **Launch** — runs the test command as a background process with `> logfile 2>&1`
2. **Poll** — checks the log file for a framework-specific completion marker from a separate terminal
3. **Analyze** — reads the log file and/or opens the HTML report to extract results

You just tell the agent what to run — it handles the rest:

```
@Robot Framework Test Runner — Run e2e-robot/tests/dark_mode.robot and report results.
```

> 💡 **This is a great demo of custom agents**: defining an `.agent.md` file turns Copilot into a specialized test executor that knows the exact commands, markers, and report locations for each framework.

---

## Pre-requisites

Make sure the app is running before generating and executing tests:

```bash
# Start the app (if not already running)
mvn spring-boot:run
```

Verify each framework's environment:

```bash
# Robot Framework
cd e2e-robot && source .venv/bin/activate && robot --version && cd ..

# Cypress
cd e2e-cypress && npx cypress --version && cd ..

# Playwright
cd e2e-playwright && npx playwright --version && cd ..

# k6
k6 version
```

> If the Robot Framework venv doesn't exist:
> ```bash
> cd e2e-robot && python3 -m venv .venv && source .venv/bin/activate && pip install -r requirements.txt && rfbrowser init
> ```

---

## 5.1 — Module 02: IDE Features

> **Skip this section** if you didn't complete any features from [Module 02](02-ide-features.md).

### 5.1.1 — App of the Day Hero Banner

> This was the **instructor demo** in Module 02 Part A. If the instructor committed the code, generate tests for it. Otherwise, skip to 5.1.2.

**Generate the test** — pick your framework and use the corresponding prompt in Copilot Chat:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the App of the Day hero banner. Create `e2e-robot/tests/app_of_the_day.robot`.*
>
> *Test cases:*
> *1. **App Of The Day Card Visible** — Open the homepage, verify an element with class `.app-of-the-day` is visible*
> *2. **Card Shows App Name** — Verify the App of the Day card contains text (the app name is not empty)*
> *3. **Card Links To App Detail** — Click the App of the Day card, verify the URL contains `/app/`*
> *4. **Card Has Background Image** — Verify the `.app-of-the-day` element has a `background-image` CSS property set*
>
> *Follow patterns in `e2e-robot/tests/home.robot` and `e2e-robot/resources/common.resource`. Use the Browser library."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the App of the Day hero banner at `e2e-cypress/cypress/e2e/app-of-the-day.cy.js`.*
>
> *Test cases:*
> *1. **App Of The Day card is visible** — Visit `/`, verify `.app-of-the-day` exists and is visible*
> *2. **Card shows app name** — Verify the card contains non-empty text*
> *3. **Card links to app detail** — Click the card, verify URL includes `/app/`*
> *4. **Card has background image** — Verify `.app-of-the-day` has `background-image` CSS*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/home.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the App of the Day hero banner at `e2e-playwright/tests/app-of-the-day.spec.ts`.*
>
> *Test cases:*
> *1. **App Of The Day card is visible** — Navigate to `/`, expect `.app-of-the-day` to be visible*
> *2. **Card shows app name** — Expect the card to contain non-empty text*
> *3. **Card links to app detail** — Click the card, expect URL to contain `/app/`*
> *4. **Card has background image** — Expect `.app-of-the-day` to have `background-image` CSS*
>
> *Follow patterns in `e2e-playwright/tests/home.spec.ts`."*

</details>

**Run the test** — use the corresponding agent:

```
@Robot Framework Test Runner — Run e2e-robot/tests/app_of_the_day.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/app-of-the-day.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/app-of-the-day.spec.ts and report results.
```

---

### 5.1.2 — Dark Mode Toggle

> **Skip** if you didn't implement Dark Mode from Module 02 Part B.

**Generate the test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the dark mode toggle. Create `e2e-robot/tests/dark_mode.robot`.*
>
> *Test cases:*
> *1. **Dark Mode Toggle Activates** — Open the homepage, click `#darkModeToggle`, verify `html` has attribute `data-theme` equal to `dark`*
> *2. **Dark Mode Persists Across Pages** — Activate dark mode on homepage, navigate to `/browse`, verify `html` still has `data-theme=dark`*
> *3. **Dark Mode Persists After Refresh** — Activate dark mode, reload the page, verify persistence (localStorage)*
> *4. **Dark Mode Can Be Deactivated** — Activate dark mode, click toggle again, verify `data-theme` is removed*
>
> *Follow patterns in `e2e-robot/tests/home.robot` and `e2e-robot/resources/common.resource`. Use Browser library — `Get Attribute`, `Click`, `Reload`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the dark mode toggle at `e2e-cypress/cypress/e2e/dark-mode.cy.js`.*
>
> *Test cases:*
> *1. **Toggle activates dark mode** — Visit `/`, click `#darkModeToggle`, verify `html` has `[data-theme='dark']`*
> *2. **Persists across pages** — Activate dark mode, visit `/browse`, verify still dark*
> *3. **Persists after reload** — Activate, `cy.reload()`, verify still dark*
> *4. **Can be deactivated** — Activate, click again, verify `[data-theme='dark']` removed*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/home.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the dark mode toggle at `e2e-playwright/tests/dark-mode.spec.ts`.*
>
> *Test cases:*
> *1. **Toggle activates dark mode** — Navigate to `/`, click `#darkModeToggle`, expect `html` to have attribute `data-theme` = `dark`*
> *2. **Persists across pages** — Activate dark mode, navigate to `/browse`, expect still dark*
> *3. **Persists after reload** — Activate, `page.reload()`, expect still dark*
> *4. **Can be deactivated** — Activate, click again, expect `data-theme` removed*
>
> *Follow patterns in `e2e-playwright/tests/home.spec.ts`."*

</details>

**Run the test:**

```
@Robot Framework Test Runner — Run e2e-robot/tests/dark_mode.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/dark-mode.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/dark-mode.spec.ts and report results.
```

---

### 5.1.3 — Screenshot Lightbox Gallery (Optional)

> **Skip** if you didn't implement the Lightbox from Module 02 Part C.

**Generate the test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the screenshot lightbox gallery. Create `e2e-robot/tests/lightbox.robot`.*
>
> *Test cases:*
> *1. **Screenshot Thumbnails Visible** — Navigate to `/app/1`, verify screenshot thumbnail images are visible*
> *2. **Lightbox Opens On Click** — Click the first thumbnail, verify a lightbox/modal overlay becomes visible*
> *3. **Lightbox Can Be Closed** — Open lightbox, close it (click close button or Escape), verify overlay is hidden*
> *4. **Lightbox Navigation** — Open lightbox, click the next arrow, verify the displayed image changes*
>
> *Follow patterns in `e2e-robot/tests/browse_search.robot` and `common.resource`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the screenshot lightbox gallery at `e2e-cypress/cypress/e2e/lightbox.cy.js`.*
>
> *Test cases:*
> *1. **Thumbnails visible** — Visit `/app/1`, verify screenshot thumbnails exist*
> *2. **Lightbox opens** — Click first thumbnail, verify lightbox overlay visible*
> *3. **Lightbox closes** — Open lightbox, click close or press Escape, verify hidden*
> *4. **Navigation works** — Open lightbox, click next arrow, verify image changes*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/browse-search.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the screenshot lightbox gallery at `e2e-playwright/tests/lightbox.spec.ts`.*
>
> *Test cases:*
> *1. **Thumbnails visible** — Navigate to `/app/1`, expect screenshot thumbnails to be visible*
> *2. **Lightbox opens** — Click first thumbnail, expect lightbox overlay visible*
> *3. **Lightbox closes** — Open lightbox, close it, expect overlay hidden*
> *4. **Navigation works** — Open lightbox, click next, expect image changes*
>
> *Follow patterns in `e2e-playwright/tests/app.spec.ts`."*

</details>

**Run the test:**

```
@Robot Framework Test Runner — Run e2e-robot/tests/lightbox.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/lightbox.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/lightbox.spec.ts and report results.
```

---

## 5.2 — Module 03: CLI Features

> **Skip this section** if you didn't complete any features from [Module 03](03-cli-features.md).

### 5.2.1 — Admin Analytics Dashboard

> **Skip** if you didn't implement the Analytics Dashboard from Module 03 Part A.

**Generate the E2E test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the admin analytics dashboard with Chart.js charts. Create `e2e-robot/tests/admin_analytics.robot`.*
>
> *Test cases:*
> *1. **Analytics Dashboard Loads Charts** — Login as admin, navigate to `/admin/dashboard`, verify at least one `canvas` element exists (Chart.js renders to canvas)*
> *2. **Downloads Chart Has Data** — Verify the downloads chart canvas is visible and has non-zero height*
> *3. **Stats Cards Show Numbers** — Verify the stat cards contain numeric values*
> *4. **Analytics API Returns JSON** — Use `Evaluate JavaScript` to fetch `/admin/api/stats/downloads` and verify a non-empty JSON response*
>
> *Suite Setup: `Setup Browser With Recording` then `Login As Admin` (like `e2e-robot/tests/admin.robot`). Follow existing patterns and `common.resource`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the admin analytics dashboard at `e2e-cypress/cypress/e2e/admin-analytics.cy.js`.*
>
> *Test cases:*
> *1. **Dashboard loads charts** — Login as admin (`admin@appvault.com` / `Admin123!`), visit `/admin/dashboard`, verify `canvas` elements exist*
> *2. **Downloads chart rendered** — Verify the downloads chart canvas is visible*
> *3. **Stats cards show numbers** — Verify stat cards contain numeric text*
> *4. **API endpoints return JSON** — `cy.request('/admin/api/stats/downloads')` returns 200 with a non-empty array*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/admin.cy.js`. Login using the same approach as existing admin tests."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the admin analytics dashboard at `e2e-playwright/tests/admin-analytics.spec.ts`.*
>
> *Test cases:*
> *1. **Dashboard loads charts** — Login as admin, navigate to `/admin/dashboard`, expect `canvas` elements to be visible*
> *2. **Downloads chart rendered** — Expect the downloads chart to have non-zero dimensions*
> *3. **Stats cards show numbers** — Expect stat cards to contain numeric text*
> *4. **API returns JSON** — Use `request.get('/admin/api/stats/downloads')` and verify non-empty JSON array*
>
> *Follow patterns in existing `e2e-playwright/tests/` files. Handle login in a `beforeEach` or test setup."*

</details>

**Generate a JUnit 5 unit test** for the analytics endpoints:

> *"Generate a JUnit 5 test class for the AdminController analytics JSON endpoints. Test: GET `/admin/api/stats/downloads` returns 200 with JSON array, GET `/admin/api/stats/ratings` returns rating distribution, GET `/admin/api/stats/categories` returns category counts. Use MockMvc with `@WithMockUser(roles='ADMIN')`. Follow the existing test patterns in `src/test/`."*

**Generate a k6 load test** for the analytics API:

> *"Generate a k6 load test for the admin analytics API at `perf-k6/admin-analytics-test.js`. POST `/auth/login` with form data to establish a session, then GET `/admin/api/stats/downloads`, `/admin/api/stats/ratings`, `/admin/api/stats/categories` with session cookies. 10 VUs for 30s. Check status 200 and p95 < 500ms. Follow patterns in `perf-k6/smoke-test.js`."*

**Run the tests:**

```
@Robot Framework Test Runner — Run e2e-robot/tests/admin_analytics.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/admin-analytics.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/admin-analytics.spec.ts and report results.
```
```
@k6 Performance Test Runner — Run perf-k6/admin-analytics-test.js and report results.
```
```bash
mvn test -Dtest=AdminControllerTest
```

---

### 5.2.2 — Enhanced Search with Live Auto-suggest

> **Skip** if you didn't implement Enhanced Search from Module 03 Part B.

**Generate the E2E test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the enhanced search with live auto-suggest. Create `e2e-robot/tests/search_autosuggest.robot`.*
>
> *Test cases:*
> *1. **Auto-suggest Appears On Typing** — Open homepage, type `Foc` into the search input, wait briefly, verify a suggestion dropdown appears*
> *2. **Suggestions Show Matching Apps** — Type `Focus`, verify suggestion list contains an app name matching "Focus"*
> *3. **Clicking Suggestion Navigates To App** — Type a query, click the first suggestion, verify URL contains `/app/`*
> *4. **Suggestions Dismiss On Outside Click** — Type a query, wait for suggestions, click outside, verify dropdown disappears*
> *5. **Enter Key Submits Full Search** — Type a query, press Enter, verify navigation to `/search` results page*
>
> *Follow patterns in `e2e-robot/tests/browse_search.robot` and `common.resource`. Use `Fill Text`, `Wait For Elements State`, `Click`, `Keyboard Key`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the live search auto-suggest at `e2e-cypress/cypress/e2e/search-autosuggest.cy.js`.*
>
> *Test cases:*
> *1. **Suggestions appear** — Visit `/`, type `Foc` in the search input, verify suggestion dropdown becomes visible*
> *2. **Shows matching apps** — Type `Focus`, verify suggestion contains "Focus"*
> *3. **Click navigates to app** — Type query, click first suggestion, verify URL includes `/app/`*
> *4. **Dismiss on outside click** — Type query, click `body`, verify dropdown hidden*
> *5. **Enter submits search** — Type query, press Enter, verify URL includes `/search`*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/browse-search.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the live search auto-suggest at `e2e-playwright/tests/search-autosuggest.spec.ts`.*
>
> *Test cases:*
> *1. **Suggestions appear** — Navigate to `/`, type `Foc` in search, expect suggestion dropdown visible*
> *2. **Shows matching apps** — Type `Focus`, expect suggestion text to contain "Focus"*
> *3. **Click navigates** — Type query, click first suggestion, expect URL to contain `/app/`*
> *4. **Dismiss on outside click** — Type query, click outside, expect dropdown hidden*
> *5. **Enter submits search** — Type query, press Enter, expect URL to contain `/search`*
>
> *Follow patterns in `e2e-playwright/tests/browse-search.spec.ts`."*

</details>

**Run the tests:**

```
@Robot Framework Test Runner — Run e2e-robot/tests/search_autosuggest.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/search-autosuggest.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/search-autosuggest.spec.ts and report results.
```

---

### 5.2.3 — User Notification System (Optional)

> **Skip** if you didn't implement the Notification System from Module 03 Part C.

**Generate the E2E test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the user notification system. Create `e2e-robot/tests/notifications.robot`.*
>
> *Test cases:*
> *1. **Notification Bell Visible When Logged In** — Login as user (`user@appvault.com` / `User123!`), verify a notification bell icon is visible in the navbar*
> *2. **Notification Dropdown Opens** — Click the bell, verify a dropdown/panel appears*
> *3. **Notifications Show Items Or Empty State** — Verify the dropdown shows notification items or an empty message*
> *4. **Bell Hidden When Anonymous** — Open homepage without login, verify no bell icon*
>
> *Follow patterns in `e2e-robot/tests/user_features.robot` and `common.resource`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the user notification system at `e2e-cypress/cypress/e2e/notifications.cy.js`.*
>
> *Test cases:*
> *1. **Bell visible when logged in** — Login as user, verify bell icon visible in navbar*
> *2. **Dropdown opens** — Click bell, verify dropdown appears*
> *3. **Shows items or empty state** — Verify dropdown has content*
> *4. **Bell hidden when anonymous** — Visit `/` without login, verify no bell*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/user-features.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the user notification system at `e2e-playwright/tests/notifications.spec.ts`.*
>
> *Test cases:*
> *1. **Bell visible when logged in** — Login as user, expect bell icon visible*
> *2. **Dropdown opens** — Click bell, expect dropdown visible*
> *3. **Shows items or empty state** — Expect dropdown has content*
> *4. **Bell hidden when anonymous** — Navigate to `/`, expect no bell icon*
>
> *Follow existing Playwright test patterns."*

</details>

**Run the tests:**

```
@Robot Framework Test Runner — Run e2e-robot/tests/notifications.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/notifications.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/notifications.spec.ts and report results.
```

---

## 5.3 — Module 04: Coding Agent

> **Skip this section** if you didn't implement the Developer Portal from [Module 04](04-coding-agent.md), or if the PR hasn't been merged yet.

### 5.3.1 — Developer Portal E2E Test

**Generate the test** — pick your framework:

<details>
<summary>🤖 Robot Framework</summary>

> *"Generate a Robot Framework E2E test for the developer portal and app submission workflow. Create `e2e-robot/tests/developer_portal.robot`.*
>
> *Also add a new keyword to `e2e-robot/resources/common.resource`:*
> ```
> Login As Developer
>     Open AppVault    /auth/login
>     Fill Text    input[name="username"]    developer@appvault.com
>     Fill Text    input[name="password"]    Dev123!
>     Click    button[type="submit"]
>     Wait For Load State    networkidle
> ```
>
> *Test cases:*
> *1. **Developer Dashboard Loads** — Login as developer, navigate to `/developer/dashboard`, verify page contains "Dashboard" or "My Submissions"*
> *2. **Submit App Form Loads** — Navigate to `/developer/submit`, verify form fields exist*
> *3. **Submit App As Draft** — Fill in name, description, submit as draft, verify it appears with DRAFT status*
> *4. **Submit For Review** — Find a draft, click Submit for Review, verify PENDING_REVIEW status*
> *5. **Admin Sees Pending Submission** — Login as admin, navigate to `/admin/submissions`, verify pending submission listed*
> *6. **Admin Approves Submission** — Approve a pending submission, verify APPROVED status*
>
> *Follow patterns in `e2e-robot/tests/admin.robot` and `common.resource`."*

</details>

<details>
<summary>🌲 Cypress</summary>

> *"Generate a Cypress E2E test for the developer portal at `e2e-cypress/cypress/e2e/developer-portal.cy.js`.*
>
> *Test cases:*
> *1. **Developer dashboard loads** — Login as developer (`developer@appvault.com` / `Dev123!`), visit `/developer/dashboard`, verify content*
> *2. **Submit app form loads** — Visit `/developer/submit`, verify form fields*
> *3. **Submit app as draft** — Fill form, submit as draft, verify DRAFT status*
> *4. **Submit for review** — Find draft, submit for review, verify PENDING_REVIEW*
> *5. **Admin sees pending** — Login as admin, visit `/admin/submissions`, verify listed*
> *6. **Admin approves** — Approve submission, verify APPROVED*
>
> *Follow patterns in `e2e-cypress/cypress/e2e/admin.cy.js`."*

</details>

<details>
<summary>🎭 Playwright</summary>

> *"Generate a Playwright E2E test for the developer portal at `e2e-playwright/tests/developer-portal.spec.ts`.*
>
> *Test cases:*
> *1. **Developer dashboard loads** — Login as developer, navigate to `/developer/dashboard`, expect content*
> *2. **Submit app form loads** — Navigate to `/developer/submit`, expect form fields*
> *3. **Submit app as draft** — Fill form, submit, expect DRAFT status*
> *4. **Submit for review** — Find draft, submit for review, expect PENDING_REVIEW*
> *5. **Admin sees pending** — Login as admin, navigate to `/admin/submissions`*
> *6. **Admin approves** — Approve submission, expect APPROVED*
>
> *Follow existing Playwright patterns."*

</details>

### 5.3.2 — Developer Portal JUnit Test

> *"Generate a JUnit 5 test class for `AppSubmissionServiceImpl`. Test: `createSubmission` saves a draft, `submitForReview` changes DRAFT to PENDING_REVIEW, `approveSubmission` creates an AppListing and sets APPROVED, `rejectSubmission` sets REJECTED with review notes. Mock the repository with `@MockBean`. Follow test patterns in `src/test/`."*

### 5.3.3 — Run the Tests

```
@Robot Framework Test Runner — Run e2e-robot/tests/developer_portal.robot and report results.
```
```
@Cypress Test Runner — Run cypress/e2e/developer-portal.cy.js and report results.
```
```
@Playwright Test Runner — Run tests/developer-portal.spec.ts and report results.
```
```bash
mvn test -Dtest=AppSubmissionServiceTest
```

---

## 5.4 — Run All Tests (Optional)

Once you've generated tests for your features, run each framework's full suite to verify nothing is broken:

```
@Robot Framework Test Runner — Run all tests in e2e-robot/tests/ and give me a summary of pass/fail results.
```
```
@Cypress Test Runner — Run all tests in e2e-cypress/ and give me a summary of pass/fail results.
```
```
@Playwright Test Runner — Run all tests in e2e-playwright/ and give me a summary of pass/fail results.
```
```bash
mvn test
```

---

## 5.5 — Debugging Failed Tests

If any tests fail, use the same agent to debug and fix:

```
@Robot Framework Test Runner — The dark mode test failed. Read the log file and fix the test.
```
```
@Cypress Test Runner — The admin analytics test failed. Read the output and fix the test.
```
```
@Playwright Test Runner — The search auto-suggest test failed. Analyze the report and fix it.
```

The agents read the test output, identify the failure, and propose a fix — often adjusting selectors, waits, or assertions to match the actual implementation.

---

## Viewing Test Reports

| Framework | Report Location | How to View |
|-----------|----------------|-------------|
| Robot Framework | `e2e-robot/results/report.html` | `open e2e-robot/results/report.html` |
| Playwright | `e2e-playwright/playwright-report/` | `cd e2e-playwright && npx playwright show-report` |
| Cypress | `e2e-cypress/cypress/videos/` | Open video files directly |
| JUnit | `target/surefire-reports/` | Read `.txt` files or view in IDE |
| k6 | `test-logs/` | Read JSON output files |

---

## Checkpoint ✅

- [ ] Generated E2E tests for at least one feature you built
- [ ] Used at least one custom test-runner agent (`@Robot Framework Test Runner`, `@Cypress Test Runner`, or `@Playwright Test Runner`) to execute tests
- [ ] Tests are passing (or you debugged and fixed failures with Copilot)
- [ ] (Optional) Generated tests across multiple frameworks for comparison
- [ ] (Optional) Generated JUnit unit tests and/or k6 load tests

---

👉 Continue to **[Module 06 — Comparison & Wrap-Up](06-comparison.md)** for the final analysis.
