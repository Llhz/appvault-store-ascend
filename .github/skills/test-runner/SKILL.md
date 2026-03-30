---
name: test-runner
description: "Shared methodology for running test suites (Cypress, Playwright, Robot Framework, k6) with reliable output capture via log files and HTML reports. Use this skill when executing any test framework."
---

# Test Runner Skill — Reliable Test Execution & Analysis

This skill defines the **reliable workflow for running and analyzing tests** across all test frameworks. The core problem it solves: terminal output is often truncated or the process gets interrupted, so we **always run as background processes**, **capture output to files**, and **use HTML reports via browser**.

## Golden Rules

1. **NEVER interrupt a running terminal** — killing a test run wastes all progress and forces a full restart
2. **ALWAYS run tests as a BACKGROUND process** — use `> logfile 2>&1` redirection, set `isBackground: true` in terminal tool
3. **NEVER use `| tee`** — it requires a foreground terminal which can be interrupted or timeout
4. **Poll for completion from a SEPARATE terminal** — check the log file for completion markers, never touch the test terminal
5. **ALWAYS read the log file** after the run completes — use the file read tool, not terminal output
6. **Prefer HTML reports** (Playwright, Robot Framework) opened via browser tools
7. **Create the `test-logs/` directory** at the workspace root before any test run
8. **Check pre-requisites** (app running, dependencies installed) before running tests

## Discovery

Before running any test suite, auto-discover the project layout by searching for config files:

- **Cypress**: `cypress.config.js` or `cypress.config.ts` — read `baseUrl` from it
- **Playwright**: `playwright.config.ts` or `playwright.config.js` — read `baseURL` from it
- **Robot Framework**: `*.robot` files and `requirements.txt` — find the URL variable in `.resource` files
- **k6**: `*.js` files with `import http from 'k6/http'` — find the base URL in the scripts
- **JUnit/Maven**: `pom.xml` — standard Maven test structure

Also discover test users and credentials from:
- Data seed files / initializers in the source code
- Test fixture files or config within each test framework directory

## Execution Pattern

Every test run follows this 3-phase pattern:

### Phase 1: Launch (Background)
```bash
# Foreground terminal: create log dir
mkdir -p test-logs

# Background terminal (isBackground: true): run tests
cd <workspace> && <test command> > test-logs/<framework>-run.log 2>&1
```

### Phase 2: Poll (Separate Foreground Terminal)
```bash
# Check if the run has finished by looking for completion markers
while ! grep -q '<MARKER>' test-logs/<framework>-run.log 2>/dev/null; do sleep 15; done && echo 'DONE'
```

Completion markers by framework:
- **Cypress**: `(Run Finished)`
- **Playwright**: `passed` or `failed` in summary line
- **Robot Framework**: `^Output:`
- **k6**: `iteration_duration`
- **JUnit/Maven**: `BUILD SUCCESS` or `BUILD FAILURE`

### Phase 3: Analyze (Read Log File)
```bash
# Use the file read tool to read test-logs/<framework>-run.log
```

## Pre-requisites Check

Before running any test suite, verify in a foreground terminal:

```bash
# 1. Check the app is reachable (discover the URL from config files first)
curl -s -o /dev/null -w "%{http_code}" <baseUrl>

# 2. Create test-logs directory
mkdir -p test-logs
```

If the app is not reachable, inform the user and stop.

## Framework Quick Reference

### Cypress

```bash
# BACKGROUND: Run all
cd <workspace>/<cypress-dir> && npx cypress run > ../test-logs/cypress-run.log 2>&1

# BACKGROUND: Run one spec
cd <workspace>/<cypress-dir> && npx cypress run --spec "cypress/e2e/<spec>.cy.js" > ../test-logs/cypress-run.log 2>&1

# POLL: Wait for completion
while ! grep -q '(Run Finished)' test-logs/cypress-run.log 2>/dev/null; do sleep 15; done && echo 'CYPRESS DONE'

# Artifacts: screenshots/, videos/, test-logs/cypress-run.log
```

### Playwright

```bash
# BACKGROUND: Run all
cd <workspace>/<playwright-dir> && npx playwright test > ../test-logs/playwright-run.log 2>&1

# BACKGROUND: Run one spec
cd <workspace>/<playwright-dir> && npx playwright test tests/<spec>.spec.ts > ../test-logs/playwright-run.log 2>&1

# POLL: Wait for completion
while ! grep -qE '\d+ passed|\d+ failed' test-logs/playwright-run.log 2>/dev/null; do sleep 15; done && echo 'PLAYWRIGHT DONE'

# View HTML report (BEST for debugging)
cd <playwright-dir> && npx playwright show-report --port 9323 &

# Artifacts: playwright-report/, test-results/, test-logs/playwright-run.log
```

### Robot Framework

```bash
# BACKGROUND: Run all
cd <workspace>/<robot-dir> && source .venv/bin/activate && robot --outputdir results tests/ > ../test-logs/robot-run.log 2>&1

# BACKGROUND: Run one file
cd <workspace>/<robot-dir> && source .venv/bin/activate && robot --outputdir results tests/<file>.robot > ../test-logs/robot-run.log 2>&1

# POLL: Wait for completion
while ! grep -q '^Output:' test-logs/robot-run.log 2>/dev/null; do sleep 15; done && echo 'ROBOT DONE'

# View HTML report (BEST for debugging)
cd <robot-dir>/results && python3 -m http.server 9324 &

# Artifacts: results/report.html, results/log.html, test-logs/robot-run.log
```

### k6

```bash
# BACKGROUND: Run a test
cd <workspace>/<k6-dir> && k6 run <script>.js --out json=../test-logs/k6-<name>-results.json > ../test-logs/k6-<name>-run.log 2>&1

# POLL: Wait for completion
while ! grep -q 'iteration_duration' test-logs/k6-<name>-run.log 2>/dev/null; do sleep 15; done && echo 'K6 DONE'

# Artifacts: test-logs/k6-<name>-run.log, test-logs/k6-<name>-results.json
```

### JUnit (Maven)

```bash
# BACKGROUND: Run all unit tests
cd <workspace> && mvn test -B > test-logs/junit-run.log 2>&1

# BACKGROUND: Run specific test class
cd <workspace> && mvn test -Dtest=<TestClass> -B > test-logs/junit-run.log 2>&1

# POLL: Wait for completion
while ! grep -qE 'BUILD SUCCESS|BUILD FAILURE' test-logs/junit-run.log 2>/dev/null; do sleep 10; done && echo 'JUNIT DONE'

# Artifacts: test-logs/junit-run.log, target/surefire-reports/*.txt
```

## Analysis Workflow

After every test run, follow this sequence:

### 1. Read the log file

Use the file read tool to open the appropriate log file (e.g., `test-logs/cypress-run.log`). Extract:
- Total / passing / failing test counts
- Names of failed tests
- Error messages and stack traces

### 2. For HTML-based frameworks, open the report

For Playwright, Robot Framework: serve the report directory and open it in a browser using browser tools. These reports are far more informative than log files.

### 3. Inspect failure artifacts

- **Screenshots**: Use `view_image` tool to see failure screenshots
- **Videos**: Note their paths for the user
- **Traces** (Playwright): Note trace file paths — user can open with `npx playwright show-trace`

### 4. Provide structured results

Always format results as a clear summary table with:
- Pass/fail status
- Test counts (total, passing, failing)
- List of failures with error messages
- Paths to artifacts (logs, reports, screenshots)

## Running All Test Suites

Launch each as a background process. **Wait for each to complete before starting the next** (they share the same app and may conflict):

1. JUnit → poll → analyze
2. Cypress → poll → analyze
3. Playwright → poll → analyze
4. Robot Framework → poll → analyze
5. k6 → poll → analyze

**Never start the next suite until the current one's poll confirms completion.**
