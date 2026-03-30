# Module 03 — Copilot CLI

> **Goal**: Implement data-rich, multi-file features using **Copilot CLI's plan mode + autopilot** — the terminal-native agentic workflow. Optionally connect external tools via **MCP** (e.g., JIRA, Slack, Notion) to show how Copilot extends beyond code.

> ⏱️ **Duration**: ~45 minutes (30 min hands-on + 15 min optional)

---

## 🎯 Objectives

- Use **plan mode** to design a multi-file feature before coding
- Use **autopilot mode** for fully autonomous implementation
- Build interactive Chart.js visualizations and live search suggestions
- Experience the "plan → approve → autopilot" local agentic workflow
- (Optional) Connect external tools via MCP and use them from within Copilot

---

## What is Copilot CLI?

Copilot CLI is a standalone terminal agent (separate from `gh copilot`) with three modes:

| Mode | How It Works | When to Use |
|------|-------------|-------------|
| **Normal (interactive)** | Back-and-forth conversation | Quick questions, single-file changes |
| **Plan mode** | Creates structured plan, asks questions, waits for approval | Complex multi-file features |
| **Autopilot mode** | Fully autonomous — reads, writes, runs commands | Well-defined tasks with clear criteria |

Switch between modes by pressing `Shift+Tab` during an interactive session.

> 📖 **Docs**: [Copilot CLI](https://docs.github.com/en/copilot/how-tos/copilot-cli/use-copilot-cli) · [Autopilot mode](https://docs.github.com/en/copilot/concepts/agents/copilot-cli/autopilot)

---

## MCP — Extending Copilot with External Tools (Optional)

**Model Context Protocol (MCP)** lets Copilot connect to external services — project trackers, wikis, messaging tools, databases — so you can interact with them using natural language without leaving your terminal or editor.

| MCP Server | What It Enables |
|------------|-----------------|
| **Atlassian (JIRA / Confluence)** | Create issues, transition tickets, search backlogs, read wiki pages |
| **Slack** | Post messages, search channels, summarize threads |
| **Notion** | Create/update pages, query databases |
| **Linear** | Create issues, manage sprints |
| **Custom / Internal** | Any REST API wrapped as an MCP server |

MCP is configured in `~/.copilot/mcp-config.json` for the CLI (or `.vscode/mcp.json` for the IDE). In this workshop we demonstrate JIRA integration as one example, but the pattern applies to any MCP-compatible service.

> 💡 **Key idea**: MCP turns Copilot from a code assistant into a **workflow hub** — you don't need to context-switch between terminals, browsers, and ticketing systems.

### Custom Agent — `@Jira Project Manager`

This workspace includes a **custom agent** at `.github/agents/jira-project-manager.agent.md` that specializes in Jira operations. Instead of remembering CLI syntax or Jira API calls, you interact with it using natural language:

```
@Jira Project Manager create a Story for admin analytics dashboard
```

The agent detects whether `jira` CLI or Atlassian MCP tools are available, executes read operations immediately, and always asks for approval before making changes.

Throughout this module, you'll use the `@Jira Project Manager` agent to **create tickets before coding** and **update them after implementation** — a realistic developer workflow.

> **🔀 Two paths**: Each Jira step below offers **two choices** — one if you have Jira access configured, and one if you don't. Pick whichever applies to you; the coding exercises work the same either way.

---

## Part A — Admin Analytics Dashboard with Charts (~30 min)

### The Feature

Transform the basic admin dashboard (which currently shows just 4 numbers) into a rich analytics page with **interactive Chart.js charts**:

- **Downloads-per-app** horizontal bar chart (top 10 apps)
- **Ratings distribution** pie/donut chart
- **Category breakdown** donut chart (apps per category)
- **Stats cards** redesigned with icons and colors

### Current State

The admin dashboard (`/admin/dashboard`) currently has:
- `totalApps`, `totalUsers`, `totalReviews`, `featuredApps` as plain numbers
- A "Recent Apps" table
- No charts, no visualizations

### Step 3A.1 — Explore Current Admin Dashboard

Start a Copilot CLI session:

```bash
cd /path/to/appvault-store
copilot
```

Explore the existing code:

```
Read @src/main/java/com/appvault/controller/AdminController.java and 
@src/main/resources/templates/admin/dashboard.html to understand the current 
admin dashboard structure. What data is currently being passed to the template?
```

Copilot CLI will summarize:
- The `dashboard()` method passes `totalApps`, `totalUsers`, `totalReviews`, `featuredApps`, `recentApps`
- The template shows them as simple number cards
- There are no chart libraries or visualizations

### Step 3A.2 — Create a Tracking Ticket

Before writing any code, create a tracking ticket. Choose the path that matches your setup:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong></summary>

Use the **`@Jira Project Manager`** agent in VS Code Copilot Chat:

```
@Jira Project Manager Create a Story for "Admin Analytics Dashboard — Interactive Charts".
Acceptance criteria:
- 3 Chart.js charts: downloads bar chart, ratings donut, categories donut
- 3 JSON endpoints in AdminController
- Redesigned stat cards with colored icons
- All existing tests pass
Priority: Medium.
```

The agent will show you the proposed issue details and ask for confirmation. Approve it, and note the issue key (e.g., `AV-42`) — you'll reference it later.

> 💡 The `@Jira Project Manager` agent auto-detects whether to use `jira` CLI or Atlassian MCP tools. You don't need to know the backend.

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong></summary>

Use Copilot CLI to create a local tracking note instead:

```
Create a file TASKS.md with a new task:
## Admin Analytics Dashboard — Interactive Charts
Status: In Progress
Acceptance criteria:
- 3 Chart.js charts: downloads bar chart, ratings donut, categories donut
- 3 JSON endpoints in AdminController
- Redesigned stat cards with colored icons
- All existing tests pass
```

This gives you the same "document requirements before coding" workflow without needing Jira.

</details>

### Step 3A.3 — Create a Plan (Plan Mode)

Switch to plan mode (`Shift+Tab` or `/plan`). Reference the tracking ticket you created in Step 3A.2 so the plan aligns with the requirements:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong> (reference Jira ticket)</summary>

```
/plan Implement the Jira ticket AV-42 (Admin Analytics Dashboard — Interactive Charts).
Read the acceptance criteria from the ticket. The current dashboard at /admin/dashboard 
shows 4 plain numbers. I want to add:

1. Three new JSON endpoints in AdminController (all @ResponseBody):
   - GET /admin/api/stats/downloads → top 10 apps by downloadCount as JSON array
   - GET /admin/api/stats/ratings → rating distribution (1-5 stars with counts) 
   - GET /admin/api/stats/categories → apps per category with counts

2. Update admin/dashboard.html:
   - Redesign stat cards with colored icons (Bootstrap + Font Awesome)
   - Add 3 Chart.js canvas elements in a responsive grid
   - Include Chart.js from CDN: https://cdn.jsdelivr.net/npm/chart.js
   - Add inline <script> that fetches JSON from the 3 endpoints and renders:
     a. Horizontal bar chart for downloads (blue gradient)
     b. Donut chart for ratings distribution (green/yellow/red spectrum)
     c. Donut chart for categories (rainbow palette)
   - Charts should have proper labels, tooltips, and legends
   - Each chart canvas must be wrapped in a fixed-height container so Chart.js can size correctly

3. Use existing repositories — AppListingRepository, ReviewRepository, 
   CategoryRepository — they're already @Autowired in AdminController.
   Use JPQL @Query in repositories if needed for aggregation.

Technical constraints:
- This is Spring Boot 2.4 / Java 8 — use javax.* imports
- Follow copilot-instructions.md conventions
- Don't modify SecurityConfig — /admin/** is already ADMIN-only
- All existing tests must pass (mvn test)
```

> 💡 Replace `AV-42` with the actual issue key from Step 3A.2. Copilot will fetch the ticket's acceptance criteria from Jira and incorporate them into the plan.

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong> (reference TASKS.md)</summary>

```
/plan Implement the "Admin Analytics Dashboard — Interactive Charts" task from TASKS.md.
Read the acceptance criteria from that file. The current dashboard at /admin/dashboard 
shows 4 plain numbers. I want to add:

1. Three new JSON endpoints in AdminController (all @ResponseBody):
   - GET /admin/api/stats/downloads → top 10 apps by downloadCount as JSON array
   - GET /admin/api/stats/ratings → rating distribution (1-5 stars with counts) 
   - GET /admin/api/stats/categories → apps per category with counts

2. Update admin/dashboard.html:
   - Redesign stat cards with colored icons (Bootstrap + Font Awesome)
   - Add 3 Chart.js canvas elements in a responsive grid
   - Include Chart.js from CDN: https://cdn.jsdelivr.net/npm/chart.js
   - Add inline <script> that fetches JSON from the 3 endpoints and renders:
     a. Horizontal bar chart for downloads (blue gradient)
     b. Donut chart for ratings distribution (green/yellow/red spectrum)
     c. Donut chart for categories (rainbow palette)
   - Charts should have proper labels, tooltips, and legends
   - Each chart canvas must be wrapped in a fixed-height container so Chart.js can size correctly

3. Use existing repositories — AppListingRepository, ReviewRepository, 
   CategoryRepository — they're already @Autowired in AdminController.
   Use JPQL @Query in repositories if needed for aggregation.

Technical constraints:
- This is Spring Boot 2.4 / Java 8 — use javax.* imports
- Follow copilot-instructions.md conventions
- Don't modify SecurityConfig — /admin/** is already ADMIN-only
- All existing tests must pass (mvn test)
```

</details>

**Review the plan** — Copilot CLI will present a structured plan with checkboxes. Check that it:
- Creates new data endpoints (not new controllers)
- Uses Chart.js from CDN (not npm)
- Keeps existing dashboard functionality intact

You can refine the plan:

```
The plan looks good, but also:
- Add the total download count across all apps to the stats cards
- Make sure CSRF token is included in fetch headers (follow the helpful-btn AJAX pattern in app.js)
- Use the app's primary blue (#007AFF) as the main chart color
```

### Step 3A.4 — Execute with Autopilot

Accept the plan and let autopilot implement it:

> **"Accept plan and build on autopilot"**

Choose **option 1** (enable all permissions) or use `/allow-all`.

**Watch Copilot CLI:**
1. Read `copilot-instructions.md` to discover conventions
2. Add repository query methods (possibly with `@Query` JPQL)
3. Add JSON endpoints to `AdminController.java`
4. Redesign `admin/dashboard.html` with charts
5. Run `mvn test` to verify

Alternatively, use programmatic autopilot:

```bash
copilot --autopilot --yolo --max-autopilot-continues 15 -p "Enhance the admin dashboard with Chart.js analytics charts. Add 3 JSON data endpoints to AdminController (@ResponseBody): top 10 apps by downloads, rating distribution (1-5 star counts), apps per category. Redesign admin/dashboard.html with styled stat cards and 3 Chart.js charts (horizontal bar for downloads, donut for ratings, donut for categories). Load Chart.js from CDN. Wrap each canvas in a fixed-height container div for proper Chart.js sizing. Use existing autowired repositories. Follow copilot-instructions.md. Run mvn test to verify."
```

### Step 3A.5 — Verify the Result

```bash
# Run tests
mvn test

# Start the app
mvn spring-boot:run
```

1. Login as admin: http://localhost:8080/auth/login (`admin@appvault.com` / `Admin123!`)
2. Go to: http://localhost:8080/admin/dashboard
3. **Expected**: Styled stat cards + 3 interactive charts with real data
4. Hover over chart elements — tooltips should show values
5. Resize browser — charts should be responsive

| Check | Expected |
|-------|----------|
| Stat cards redesigned | Colored icons, larger numbers, card format |
| Downloads bar chart | Top 10 apps with horizontal bars |
| Ratings donut chart | 1-5 star distribution with colors |
| Categories donut chart | Apps per category with legend |
| Charts interactive | Hover tooltips, click interactions |
| Charts properly sized | Each canvas fits within its container, no runaway growth |
| All tests pass | `mvn test` → BUILD SUCCESS |

### Step 3A.6 — Update the Tracking Ticket

Close the loop on your ticket. Choose the path that matches your setup:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong></summary>

Back in VS Code Copilot Chat:

```
@Jira Project Manager Add a comment to AV-42 summarizing:
- Added 3 JSON endpoints to AdminController
- Redesigned dashboard.html with Chart.js charts (CDN)
- All acceptance criteria met, tests passing
Then transition it to Done.
```

The agent will show the comment and transition before applying — approve both.

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong></summary>

Use Copilot CLI to update your local tracking note:

```
Update the "Admin Analytics Dashboard" task in TASKS.md:
- Change status to Done
- Add summary: Added 3 JSON endpoints to AdminController, redesigned dashboard.html with Chart.js charts (CDN), all acceptance criteria met, tests passing
```

</details>

> 💡 This is the developer workflow: **ticket → plan → code → verify → update ticket** — all without leaving your editor or terminal.

---

## Part B — Enhanced Search with Live Auto-suggest (~15 min)

### The Feature

Transform the basic search bar into a **smart search with live suggestions**:

- As you type → dropdown appears with matching app suggestions
- Each suggestion shows the app icon, name, and category
- Click a suggestion → navigate to app detail page
- **Debounced** requests (300ms) to avoid spamming the server
- Recent searches saved in `localStorage`

### Current State

The search bar in the navbar (`fragments/navbar.html`) submits a form to `/search` on Enter. There's no live search or suggestions.

### Step 3B.1 — Create a Tracking Ticket

Create a ticket for this feature. Choose the path that matches your setup:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong></summary>

Use the `@Jira Project Manager` agent in VS Code Copilot Chat:

```
@Jira Project Manager Create a Story for "Live Search Auto-suggest".
Acceptance criteria:
- Dropdown appears after typing 2+ characters
- Shows matching apps with icons and category badges
- 300ms debounce on requests
- Click suggestion navigates to app detail
- Recent searches saved in localStorage
- Dark mode compatible
Priority: Medium.
```

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong></summary>

Use Copilot CLI to add a task to your local tracking file:

```
Add a new task to TASKS.md:
## Live Search Auto-suggest
Status: In Progress
Acceptance criteria:
- Dropdown appears after typing 2+ characters
- Shows matching apps with icons and category badges
- 300ms debounce on requests
- Click suggestion navigates to app detail
- Recent searches saved in localStorage
- Dark mode compatible
```

</details>

### Step 3B.2 — Plan and Execute

Start Copilot CLI (or continue your session). Reference the tracking ticket from Step 3B.1:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong> (reference Jira ticket)</summary>

```
copilot --autopilot --yolo --max-autopilot-continues 15 -p "Implement the Jira ticket AV-43 (Live Search Auto-suggest). Read the acceptance criteria from the ticket. Add live search auto-suggest to the AppVault search bar. 

1. New endpoint in AppController: GET /search/suggest?q={query} returns @ResponseBody JSON array of top 5 matching apps with fields: id, name, iconUrl, categoryName. Make this public (it's under the already-permitted /search path). Use the existing AppListingRepository#searchByQuery method or create a simple derived query.

2. Update fragments/navbar.html: Add a suggestion dropdown div below the search input (position: absolute, z-index high). Should be hidden by default.

3. Update static/css/style.css: Style the suggestion dropdown — white background (dark mode compatible), rounded corners, shadow, each suggestion is a row with app icon (24px), name, category badge. Hover highlight.

4. Update static/js/app.js: Add inside DOMContentLoaded:
   - On input event on the search field (debounce 300ms), if query length >= 2, fetch /search/suggest?q=... and render results in dropdown
   - Click on suggestion → window.location = /app/{id}
   - Escape or click outside → close dropdown  
   - On focus when empty → show recent searches from localStorage
   - On search submission → save query to localStorage recent searches (max 5)
   - Include CSRF token in fetch headers

Follow copilot-instructions.md conventions. This is Java 8 / Spring Boot 2.4. Run mvn test to verify."
```

> 💡 Replace `AV-43` with the actual issue key from Step 3B.1.

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong> (reference TASKS.md)</summary>

```
copilot --autopilot --yolo --max-autopilot-continues 15 -p "Implement the 'Live Search Auto-suggest' task from TASKS.md. Read the acceptance criteria from that file. Add live search auto-suggest to the AppVault search bar. 

1. New endpoint in AppController: GET /search/suggest?q={query} returns @ResponseBody JSON array of top 5 matching apps with fields: id, name, iconUrl, categoryName. Make this public (it's under the already-permitted /search path). Use the existing AppListingRepository#searchByQuery method or create a simple derived query.

2. Update fragments/navbar.html: Add a suggestion dropdown div below the search input (position: absolute, z-index high). Should be hidden by default.

3. Update static/css/style.css: Style the suggestion dropdown — white background (dark mode compatible), rounded corners, shadow, each suggestion is a row with app icon (24px), name, category badge. Hover highlight.

4. Update static/js/app.js: Add inside DOMContentLoaded:
   - On input event on the search field (debounce 300ms), if query length >= 2, fetch /search/suggest?q=... and render results in dropdown
   - Click on suggestion → window.location = /app/{id}
   - Escape or click outside → close dropdown  
   - On focus when empty → show recent searches from localStorage
   - On search submission → save query to localStorage recent searches (max 5)
   - Include CSRF token in fetch headers

Follow copilot-instructions.md conventions. This is Java 8 / Spring Boot 2.4. Run mvn test to verify."
```

</details>

### Step 3B.3 — Verify

1. Start the app: `mvn spring-boot:run`
2. Open http://localhost:8080
3. Click in the search bar
4. Type "ph" → suggestions should appear (apps matching "ph")
5. Click a suggestion → navigates to app detail page
6. Press Escape → dropdown closes
7. Clear the search, focus the bar → recent searches appear

| Check | Expected |
|-------|----------|
| Type 2+ chars → suggestions | Dropdown with app icons, names, categories |
| Click suggestion | Navigates to `/app/{id}` |
| Debounce working | No flicker, requests are batched |
| Recent searches | Appear on empty focus |
| Dark mode compatible | Dropdown has dark theme if dark mode is on |
| All tests pass | `mvn test` → BUILD SUCCESS |

### Step 3B.4 — Update the Tracking Ticket

Close the loop on your ticket:

<details>
<summary>✅ <strong>Choice A — I have Jira access</strong></summary>

```
@Jira Project Manager Add a comment to the live search ticket:
- Added /search/suggest endpoint returning JSON
- Implemented debounced auto-suggest in app.js
- Dark mode compatible dropdown styling
- All tests passing
Transition to Done.
```

</details>

<details>
<summary>🚫 <strong>Choice B — I don't have Jira access</strong></summary>

Use Copilot CLI:

```
Update the "Live Search Auto-suggest" task in TASKS.md:
- Change status to Done
- Add summary: Added /search/suggest endpoint returning JSON, implemented debounced auto-suggest in app.js, dark mode compatible dropdown styling, all tests passing
```

</details>

---

## Part C — User Notification System (Optional, ~15 min)

> If time permits. This is the most complex CLI feature — showcases autopilot handling many files.

### The Feature

- **Bell icon** in navbar with unread count badge
- Notifications when someone reviews an app you've reviewed, or votes "helpful" on your review
- **Dropdown** shows recent notifications with timestamps
- **Mark-as-read** support (click notification or "Mark all read" button)

### Step 3C.1 — Execute with Autopilot

<details>
<summary>🔑 Autopilot Prompt</summary>

```
copilot --autopilot --yolo --max-autopilot-continues 20 -p "Add a user notification system to AppVault. This is a full-stack feature:

ENTITY: Notification.java — id, recipientUser (ManyToOne User), message (String), link (String, the URL to navigate to), read (boolean, default false), createdAt (LocalDateTime). @Entity, @Data, @Table.

REPOSITORY: NotificationRepository.java — findByRecipientUserIdOrderByCreatedAtDesc, countByRecipientUserIdAndReadFalse, standard JpaRepository methods.

SERVICE: NotificationService.java (interface) + NotificationServiceImpl.java — createNotification(userId, message, link), getNotifications(userId), getUnreadCount(userId), markAsRead(notificationId), markAllAsRead(userId). @Transactional on writes, readOnly on reads.

CONTROLLER: NotificationController.java — 
  GET /user/notifications/count → @ResponseBody JSON {count: N} (unread count)
  GET /user/notifications → @ResponseBody JSON array of recent 20 notifications
  POST /user/notifications/{id}/read → @ResponseBody mark one as read
  POST /user/notifications/read-all → @ResponseBody mark all as read

NAVBAR UPDATE (fragments/navbar.html): Add bell icon (fa-bell) with a small red badge showing unread count, before the user dropdown (only for authenticated users). On click, show a dropdown with recent notifications. Each notification is a clickable link.

JAVASCRIPT (app.js): 
  - On page load (if authenticated), fetch /user/notifications/count and update badge
  - Bell click → fetch /user/notifications and render in dropdown
  - Click notification → POST mark-as-read then navigate to link
  - 'Mark all read' button
  - Poll every 60 seconds for new count

INTEGRATION: In ReviewServiceImpl, after saving a new review, create a notification for other users who reviewed the same app: 'Someone also reviewed [AppName]'. After marking helpful, notify the review author: 'Your review was marked helpful'.

Follow copilot-instructions.md. Java 8 / Spring Boot 2.4. Run mvn test."
```

</details>

### Verify

1. Login as `user@appvault.com`
2. Check for bell icon in navbar
3. Open another browser/incognito as `admin@appvault.com`
4. Write a review on an app that `user@appvault.com` has also reviewed
5. Check — `user@appvault.com` should have a notification

---

## Files Created / Modified (Session 2)

### Part A — Admin Analytics Dashboard

```
Modified:
├── src/main/java/com/appvault/controller/AdminController.java     ← JSON endpoints
├── src/main/resources/templates/admin/dashboard.html               ← Chart.js + redesign
Possibly modified:
├── src/main/java/com/appvault/repository/AppListingRepository.java ← aggregate queries
├── src/main/java/com/appvault/repository/ReviewRepository.java     ← rating distribution
└── src/main/java/com/appvault/repository/CategoryRepository.java   ← category counts
```

### Part B — Live Search Auto-suggest

```
Modified:
├── src/main/java/com/appvault/controller/AppController.java        ← /search/suggest endpoint
├── src/main/resources/templates/fragments/navbar.html               ← suggestion dropdown HTML
├── src/main/resources/static/css/style.css                          ← dropdown styles
└── src/main/resources/static/js/app.js                              ← fetch + render logic
```

### Part C — Notifications (Optional)

```
New:
├── src/main/java/com/appvault/model/Notification.java
├── src/main/java/com/appvault/repository/NotificationRepository.java
├── src/main/java/com/appvault/service/NotificationService.java
├── src/main/java/com/appvault/service/NotificationServiceImpl.java
└── src/main/java/com/appvault/controller/NotificationController.java

Modified:
├── src/main/resources/templates/fragments/navbar.html               ← bell icon
├── src/main/resources/static/js/app.js                              ← notification JS
├── src/main/resources/static/css/style.css                          ← notification styles
└── src/main/java/com/appvault/service/ReviewServiceImpl.java        ← notification triggers
```

---

## Copilot CLI Commands Reference

| Command / Key | What It Does |
|--------------|-------------|
| `copilot` | Start an interactive session |
| `Shift+Tab` | Cycle between normal → plan → autopilot modes |
| `/plan <prompt>` | Create an implementation plan |
| `/model` | View/change the AI model |
| `/agent` | Select from available custom agents |
| `/allow-all` or `/yolo` | Grant all permissions for the session |
| `/context` | View current token usage |
| `/usage` | View premium requests used, lines edited |
| `/compact` | Manually compress conversation history |
| `/clear` or `/new` | Reset context |
| `/resume` | Resume a previous session |
| `/delegate` | Send a task to the cloud Coding Agent |
| `@path/to/file` | Include file contents in prompt |
| `!command` | Run a shell command directly |
| `Ctrl+T` | Toggle reasoning visibility |
| `Ctrl+C` | Stop the agent |

---

## Reflection — What Did We Learn?

| Lesson | Detail |
|--------|--------|
| **Plan mode prevents wrong-direction work** | Reviewing the plan caught issues before any code was written |
| **Autopilot handles 5+ file changes** | Dashboard required controller + template + CSS + possibly repositories |
| **copilot-instructions.md works in CLI too** | Same customization files, same conventions followed |
| **Custom agents streamline workflows** | `@Jira Project Manager` handles ticket lifecycle — create, comment, transition — without leaving the editor |
| **Chart.js from CDN = zero build complexity** | No webpack, no npm — just a `<script>` tag |

### Copilot CLI vs. IDE Copilot Chat

| Aspect | Copilot Chat (IDE) | Copilot CLI (Terminal) |
|--------|-------------------|----------------------|
| **Multi-file creation** | Via Agent Mode | Native — autopilot creates all files |
| **Plan mode** | Not available | Built-in |
| **Autopilot** | Not available | Built-in — fully autonomous |
| **MCP integration** | VS Code `.vscode/mcp.json` | `~/.copilot/mcp-config.json` + `/mcp add` |
| **Session persistence** | Per-chat | `/resume` to continue later |

---

## Checkpoint ✅

- [ ] **Analytics Dashboard** — 3 interactive charts on admin dashboard
- [ ] **Live Search** — suggestions appear as you type, with icons and categories
- [ ] (Optional) **Notifications** — bell icon with unread badge
- [ ] `mvn test` → all tests pass
- [ ] Understand the difference between plan / autopilot modes
- [ ] (Optional) Used `@Jira Project Manager` agent to create and close tickets

---

👉 Continue to **[Module 04 — Coding Agent](04-coding-agent.md)** to implement an entire Developer Portal using one GitHub Issue.
