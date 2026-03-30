# GitHub Copilot Workshop — From Chat to Coding Agent

> **Build visually impactful features in a live app** to experience how GitHub Copilot transforms the software development lifecycle — from prompt engineering to fully autonomous coding agents, integrated with JIRA and the GitHub platform.

---

## What You'll Build

You will implement **up to 7 features** in **AppVault Store**, a Spring Boot app-store marketplace, across three sessions using progressively more powerful Copilot workflows:

### Session 1 — IDE Copilot Chat (~45 min)

| # | Feature | How You'll Build It | Visual Impact |
|---|---------|--------------------|----|
| 1 | **App of the Day Hero Banner** | Copilot Chat — instructor demo of prompt engineering | 🟢 Homepage transformation |
| 2 | **Dark Mode Toggle** | Copilot Chat — hands-on, full UI theme switch | 🟢 Entire app transforms |
| 3 | **Screenshot Lightbox Gallery** *(optional)* | Copilot Chat — hands-on, interactive JS/CSS | 🟡 App detail page enhancement |

### Session 2 — Copilot CLI (~45 min)

| # | Feature | How You'll Build It | Visual Impact |
|---|---------|--------------------|----|
| 4 | **Admin Analytics Dashboard with Charts** | Copilot CLI plan + autopilot | 🟢 Chart.js data visualizations |
| 5 | **Enhanced Search with Live Auto-suggest** | Copilot CLI autopilot | 🟢 Real-time interactive search |
| 6 | **User Notification System** *(optional)* | Copilot CLI autopilot | 🟡 Bell icon + dropdown |

### Session 3 — Coding Agent (~30 min)

| # | Feature | How You'll Build It | Visual Impact |
|---|---------|--------------------|----|
| 7 | **Developer Portal with App Submission Workflow** | GitHub Issues + Coding Agent — 10–15 new files | 🟢🟢 Entire new portal |

---

## Why These Features?

Every feature is **visually observable** in the browser. No more invisible backend plumbing — attendees can *see* the impact immediately.

| Session | Before | After |
|---------|--------|-------|
| **IDE** | Generic homepage | App of the Day hero banner + dark mode toggle |
| **CLI** | Plain admin stats + basic search | Interactive charts + live auto-suggest |
| **Coding Agent** | Users only | Developer portal with submission workflow |

---

## Workshop Schedule (~2 hours)

### Pre-Workshop

| Step | Topic | Duration |
|------|-------|----------|
| [00 — Prerequisites](00-prerequisites.md) | Install tools, build project, verify app runs | Self-paced |
| [00 — App Modernization](../workshop-walkthrough/00-optional-app-modernization.md) *(optional)* | Upgrade Java 8 → 21 with Copilot AppMod Extension | +30 min |

### Module 1 — Setup (10 min)

| Step | Topic | Duration |
|------|-------|----------|
| [01 — Setup Copilot Workspace](01-setup-copilot-workspace.md) | Fork repo, enable Copilot, enable Advanced Security | 10 min |

### Module 2 — IDE Feature Implementation (45 min)

| Step | Topic | Duration |
|------|-------|----------|
| [02 — IDE Features](02-ide-features.md) | App of the Day (demo) + Dark Mode (hands-on) + Lightbox (optional) | 45 min |

### Module 3 — Copilot CLI (45 min)

| Step | Topic | Duration |
|------|-------|----------|
| [03 — Copilot CLI](03-cli-features.md) | Analytics Dashboard + Live Search via Copilot CLI plan/autopilot (MCP optional) | 45 min |

### Module 4 — Coding Agent (30 min)

| Step | Topic | Duration |
|------|-------|----------|
| [04 — Coding Agent](04-coding-agent.md) | Developer Portal via GitHub Issues + Coding Agent | 30 min |

### Module 5 — Testing & Wrap-Up (30 min)

| Step | Topic | Duration |
|------|-------|----------|
| [05 — Testing Integration](05-testing-integration.md) | Generate & run Robot Framework / E2E tests per feature | 15–20 min |
| [06 — Comparison & Wrap-Up](06-comparison.md) | Side-by-side comparison, GHAS, takeaways | 10 min |

**Total: ~2.5 hours** (with optional features) / **~2 hours** (core path)

---

## Application Overview

**AppVault Store** is a server-rendered Spring Boot MVC application — an app-store marketplace similar to the Apple App Store.

### Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 8 |
| Framework | Spring Boot 2.4.13 |
| Templates | Thymeleaf + Bootstrap 5 + Font Awesome 6 |
| Security | Spring Security (form login, BCrypt, role-based access) |
| Database | H2 in-memory (recreated on each startup via `create-drop`) |
| Build | Maven |

### Architecture

```
Controller → Service (interface + impl) → Repository → Entity
```

- **6 Controllers** — Home, Auth, App, User, Review, Admin
- **3 Services** — UserService, AppListingService, ReviewService (each with interface + implementation)
- **6 Entities** — User, AppListing, Review, Category, Screenshot, Role
- **6 Repositories** — Spring Data JPA with derived queries + JPQL

### Security Model

| Role | Access |
|------|--------|
| Public | `/`, `/browse/**`, `/app/**`, `/search`, `/auth/**` |
| `ROLE_USER` | `/user/**`, `/review/**` |
| `ROLE_ADMIN` | `/admin/**` |

### Default Accounts (seeded on startup)

| Email | Password | Role |
|-------|----------|------|
| `admin@appvault.com` | `Admin123!` | Admin + User |
| `user@appvault.com` | `User123!` | User |

### Copilot Customization Already Configured

The repository comes pre-loaded with:
- **19 custom agents** in `.github/agents/` — TDD workflow, test runners, architecture specialists
- **18 custom skills** in `.github/skills/` — Java, Spring Boot, testing, refactoring, documentation
