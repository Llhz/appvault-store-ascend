# Prerequisites

> **Goal**: Ensure your environment is ready and the application runs before the workshop day. Complete these steps **before arriving**.

---

## 🎯 Objectives

- Install all required tools
- Clone and build the AppVault Store project
- Verify the application starts and loads seed data

---

## Step 0.1 — Required Tools

Ensure the following are installed on your machine:

| Tool | Version | Verify Command |
|------|---------|----------------|
| **Java JDK** | 8 (1.8.x) | `java -version` |
| **Maven** | 3.6+ | `mvn -version` |
| **Node.js** | 16+ (for E2E tests) | `node --version` |
| **VS Code** | Latest | — |
| **Git** | 2.x | `git --version` |
| **GitHub CLI** | 2.x+ | `gh --version` |
| **Copilot CLI** | Latest | `copilot --version` |

### VS Code Extensions

Install these extensions before the workshop:

| Extension | Required For |
|-----------|-------------|
| **GitHub Copilot** | Core AI assistance |
| **GitHub Copilot Chat** | Chat panel + agents |
| **Extension Pack for Java** | Java language support |
| **Spring Boot Extension Pack** | Spring Boot support |

### Copilot CLI (Terminal Agent)

Copilot CLI is a standalone terminal-native agentic coding assistant — separate from `gh copilot`.

```bash
# Install (choose one)
brew install copilot-cli          # macOS/Linux (Homebrew)
npm install -g @github/copilot    # All platforms (requires Node.js 22+)
curl -fsSL https://gh.io/copilot-install | bash   # macOS/Linux (script)

# Verify
copilot --version
```

On first launch, Copilot CLI will prompt you to authenticate via browser OAuth (`/login`).

### GitHub CLI (for auth only)

```bash
# Install if not already present
brew install gh

# Login — we use this only for git auth and verifying your account
gh auth login
gh auth status
```

> 💡 **Instructor Note**: Verify all attendees have an active GitHub Copilot license (Individual, Business, or Enterprise) and a GitHub account that can fork repositories.

---

## Step 0.2 — Clone & Build

```bash
# Clone the repository (you'll fork it in Module 01 on workshop day)
git clone <your-repo-url> appvault-store
cd appvault-store

# Build the project (skip tests for speed)
mvn clean package -DskipTests
```

**Expected output**: `BUILD SUCCESS` — the project compiles with zero errors.

If the build fails, check:
- Java version is 8: `java -version` should show `1.8.x`
- Maven is accessible: `mvn -version`
- You're in the project root (where `pom.xml` lives)

---

## Step 0.3 — Run the Application

```bash
mvn spring-boot:run
```

**Expected output** (last few lines):
```
Started AppVaultApplication in X.XX seconds
DataInitializer: Seeding roles, users, categories, apps, and reviews...
```

### Verify in the browser

Open **http://localhost:8080** and confirm:

| Check | Expected |
|-------|----------|
| Home page loads | Hero banner, featured apps carousel, category bar |
| Browse page | http://localhost:8080/browse — grid of 20 apps with pagination |
| App detail | Click any app — see icon, description, rating, reviews, screenshots |
| Login works | http://localhost:8080/auth/login → use `admin@appvault.com` / `Admin123!` |
| Admin dashboard | After admin login → http://localhost:8080/admin/dashboard — stats panel |

> Press `Ctrl+C` (or `Cmd+C`) in the terminal to stop the server when done verifying.

---

## Step 0.4 — Run Existing Tests

```bash
mvn test
```

**Expected output**: All test classes pass. Look for:

```
Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

> These tests are your safety net throughout the workshop. If you break existing tests, something went wrong.

---

## Checkpoint ✅

Before the workshop, confirm:

- [ ] `mvn clean package -DskipTests` → BUILD SUCCESS
- [ ] `mvn spring-boot:run` → app loads at http://localhost:8080
- [ ] `mvn test` → all tests pass, zero failures
- [ ] You can log in as `admin@appvault.com` / `Admin123!`
- [ ] `copilot --version` works (Copilot CLI installed)
- [ ] `gh auth status` shows you're logged in
- [ ] VS Code has Copilot + Java extensions installed

---

## Workshop Day

👉 On workshop day, start with **[Module 01 — Setup Copilot Workspace](01-setup-copilot-workspace.md)**.
