# Module 04 — Coding Agent: Developer Portal

> **Goal**: Implement a **complete Developer Portal** — the biggest feature in the workshop — using a single GitHub Issue + the Copilot Coding Agent. One prompt triggers autonomous creation of 10–15 new files across all layers.

> ⏱️ **Duration**: ~30 minutes (5 min issue creation + 5–10 min agent work + 15 min review)

---

## 🎯 Objectives

- Create a comprehensive GitHub Issue that describes an entire new subsystem
- Watch the Coding Agent implement a new role, submission workflow, and 4+ new pages
- Review a large PR with 10–15 new/modified files
- Experience the "one prompt → complete subsystem" workflow
- Compare effort with the manual Copilot Chat work in Module 02

---

## The Feature — Developer Portal with App Submission Workflow

This is the largest feature in the workshop. It adds an entirely new area to AppVault:

| Component | Details |
|-----------|---------|
| **New Role** | `ROLE_DEVELOPER` — extends the existing role system |
| **Developer Registration** | "Become a Developer" flow — company name, website URL |
| **App Submission Form** | Multi-field form: app name, description, category, icon URL, price |
| **Submission Queue** | New admin page showing pending submissions — approve/reject actions |
| **Developer Dashboard** | `/developer/dashboard` — view submitted apps, status, stats |
| **Status Workflow** | `DRAFT` → `PENDING_REVIEW` → `APPROVED` / `REJECTED` |
| **New Templates** | 4–5 new pages: developer dashboard, submission form, admin review queue, submission detail |
| **Security** | New `/developer/**` route rules in `SecurityConfig` |
| **Data Seeding** | Sample developer account + sample submissions in `DataInitializer` |

### Why This Is the Best Coding Agent Feature

- **10–15 new files** — entity, DTO, repository, service interface + impl, controller, 4–5 templates, security update, data initializer update
- **New role** — agent must understand and extend the existing security model
- **Visual and testable** — browse to `/developer/dashboard`, submit an app, see it in admin queue
- **Follows existing patterns** — agent must discover and replicate Review/User patterns at scale
- **Impressive PR** — large, coherent diff that tells a clear story

---

## Step 4.1 — Create the GitHub Issue

### Option A: Create via Copilot Chat + GitHub MCP

In Copilot Chat (Agent Mode):

<details>
<summary>🔑 Prompt — Create GitHub Issue via MCP</summary>

```
Create a GitHub Issue in this repository with the following content. Use the 
GitHub MCP tools to create it directly.

Title: "Developer Portal — App Submission Workflow"

Body:

## Description
Add a Developer Portal to AppVault Store where users can register as developers, 
submit apps for review, and track their submissions. Admins can review, approve, 
or reject submitted apps.

## Data Layer

### New Entity: AppSubmission
- id (Long, auto-generated)
- submitter (ManyToOne → User, the developer who submitted)
- name (String, required, max 100)
- subtitle (String, max 200)
- description (String, required, max 5000, TEXT column)
- developer (String — company/developer display name)
- iconUrl (String)
- price (BigDecimal, default 0)
- category (ManyToOne → Category)
- status (enum: DRAFT, PENDING_REVIEW, APPROVED, REJECTED)
- reviewNotes (String, admin's feedback when approving/rejecting)
- createdAt, updatedAt (LocalDateTime, @PrePersist/@PreUpdate)
- Follow the AppListing entity pattern for field definitions

### New Enum: SubmissionStatus
- DRAFT, PENDING_REVIEW, APPROVED, REJECTED

### New Repository: AppSubmissionRepository
- findBySubmitterIdOrderByCreatedAtDesc(Long userId)
- findByStatusOrderByCreatedAtAsc(SubmissionStatus status)
- countByStatus(SubmissionStatus status)

### New Role
- Add ROLE_DEVELOPER to the Role entity seed data in DataInitializer
- A user can have both ROLE_USER and ROLE_DEVELOPER

## Service Layer

### AppSubmissionService (interface) + AppSubmissionServiceImpl
Follow the existing service pattern (interface + impl, like UserService/UserServiceImpl):
- createSubmission(AppSubmissionDto dto, Long userId) → saves as DRAFT
- submitForReview(Long submissionId, Long userId) → changes DRAFT → PENDING_REVIEW
- approveSubmission(Long submissionId, String reviewNotes) → PENDING_REVIEW → APPROVED, 
  creates a real AppListing from the submission data
- rejectSubmission(Long submissionId, String reviewNotes) → PENDING_REVIEW → REJECTED
- getSubmissionsByUser(Long userId) → list for developer dashboard
- getPendingSubmissions() → list for admin review queue
- getSubmissionById(Long id) → single submission detail
- @Transactional on write methods, @Transactional(readOnly = true) on reads
- Throw ResourceNotFoundException for missing submissions

## Controller Layer

### DeveloperController (@RequestMapping("/developer"))
- GET /developer/dashboard → developer dashboard page showing their submissions (grouped by status)
- GET /developer/submit → app submission form (new)
- POST /developer/submit → process submission form, save as DRAFT
- POST /developer/submit/{id}/review → submit a DRAFT for review (PENDING_REVIEW)
- GET /developer/submission/{id} → view submission details and status

### Admin Additions
- GET /admin/submissions → review queue page showing PENDING_REVIEW submissions
- GET /admin/submissions/{id} → view submission detail with approve/reject form
- POST /admin/submissions/{id}/approve → approve with notes, create AppListing
- POST /admin/submissions/{id}/reject → reject with notes

## DTO
### AppSubmissionDto
- name, subtitle, description, developer, iconUrl, price, categoryId
- Standard validation: @NotBlank on name and description, @Size constraints

## Templates

### developer/dashboard.html
- Stats: total submissions, pending, approved, rejected counts
- Table/cards showing submissions grouped by status
- "Submit New App" button linking to /developer/submit
- Reuse navbar and footer fragments

### developer/submit-form.html  
- Form with fields: name, subtitle, description, developer name, icon URL, price, category dropdown
- Category dropdown populated from CategoryRepository
- "Save as Draft" and "Submit for Review" buttons

### developer/submission-detail.html
- Shows full submission info + current status badge (color-coded)
- If DRAFT: "Submit for Review" button
- If REJECTED: shows admin's review notes + "Resubmit" option
- If APPROVED: link to the created app listing

### admin/submission-queue.html
- Table showing pending submissions: name, developer, submitted date, actions
- Click to view detail

### admin/submission-detail.html
- Full submission preview (looks like an app detail page preview)
- "Approve" button with notes textarea
- "Reject" button with notes textarea

## Security Changes
- Add to SecurityConfig: /developer/** requires authentication (same as /user/**)
- Or: /developer/** requires ROLE_DEVELOPER (more precise)
- /admin/submissions/** already covered by /admin/** ADMIN rule

## Navigation Updates
- Add "Developer" link in navbar dropdown (for authenticated users with ROLE_DEVELOPER)
- Add "Become a Developer" link on user profile page (for users without ROLE_DEVELOPER)
- Add "Submission Queue" link in admin dashboard sidebar/nav
- Add submission queue count badge if there are pending submissions

## Data Initializer Updates
- Add ROLE_DEVELOPER to seeded roles
- Create a developer account: developer@appvault.com / Dev123! with ROLE_USER + ROLE_DEVELOPER
- Create 2-3 sample submissions in various statuses (DRAFT, PENDING_REVIEW, APPROVED)

## Acceptance Criteria
- [ ] Developer can register (get ROLE_DEVELOPER) — or use seeded developer account
- [ ] Developer dashboard shows submissions grouped by status
- [ ] Developer can submit a new app (save as draft, then submit for review)  
- [ ] Admin sees pending submissions in review queue
- [ ] Admin can approve (creates real AppListing) or reject (with notes)
- [ ] Rejected developer can see feedback and resubmit
- [ ] Status badges are color-coded (Draft=gray, Pending=yellow, Approved=green, Rejected=red)
- [ ] Navigation updated: developer link in navbar, submission queue in admin
- [ ] All existing tests pass (mvn test)
- [ ] New functionality follows project conventions (copilot-instructions.md)
```

</details>

### Option B: Create via `gh` CLI (Fallback)

```bash
gh issue create \
  --title "Developer Portal — App Submission Workflow" \
  --body-file developer-portal-issue.md
```

> 💡 Save the issue body from the prompt above to `developer-portal-issue.md` first.

---

## Step 4.2 — Assign the Coding Agent

### Option A: From GitHub.com

1. Open the issue on GitHub
2. Click **Assignees** → search "copilot" → assign

### Option B: Via `gh` CLI

```bash
gh issue edit <ISSUE_NUMBER> --add-assignee @copilot
```

### Option C: Via Copilot Chat (Agent Mode)

> *"Assign the Copilot Coding Agent to issue #X in this repository"*

> 💡 **Instructor Note**: The Coding Agent will create a new branch, implement everything, run tests, and open a PR. This takes a few minutes. Use this time to discuss the contrast with Module 02 (manual implementation).

---

## Step 4.3 — While Waiting: Discussion Points

While the Coding Agent works, discuss with the group:

### How the Agent Approaches This

1. **Reads** `.github/copilot-instructions.md` — discovers all conventions
2. **Analyzes** existing patterns — User entity, Review entity, SecurityConfig, DataInitializer
3. **Plans** the implementation order — entities first, then services, controllers, templates
4. **Implements** all files following the discovered patterns
5. **Runs** `mvn test` to verify nothing breaks
6. **Self-corrects** if compilation errors occur
7. **Opens** a PR with all changes

### Questions for Discussion

- The issue description is ~200 lines. How long would it take to implement manually?
- What patterns will the agent need to discover vs. what we told it explicitly?
- Which is more important: the issue quality or the `copilot-instructions.md`?

---

## Step 4.4 — Monitor Progress

Track the agent's work:

```bash
# Check for open PRs from the agent
gh pr list --author app/copilot-swe-agent

# Or check all recent PRs
gh pr list --state open

# Watch the issue for status comments
gh issue view <ISSUE_NUMBER> --comments
```

The agent posts status updates as comments on the issue.

---

## Step 4.5 — Review the PR

When the agent opens a PR:

```bash
# View the diff stats
gh pr diff <PR_NUMBER> --stat

# List all changed files
gh pr view <PR_NUMBER> --json files --jq '.files[].path'

# View the full diff
gh pr diff <PR_NUMBER>
```

### What to Look For

| Aspect | Expected | Check |
|--------|----------|-------|
| **AppSubmission entity** | `@Entity`, `@Data`, ManyToOne to User + Category, status enum | ✅/❌ |
| **SubmissionStatus enum** | DRAFT, PENDING_REVIEW, APPROVED, REJECTED | ✅/❌ |
| **Repository** | Extends JpaRepository, derived query methods | ✅/❌ |
| **Service interface** | Separate `AppSubmissionService` interface | ✅/❌ |
| **Service impl** | `@Transactional` on writes, `readOnly=true` on reads | ✅/❌ |
| **DeveloperController** | `/developer/**` routes, form handling | ✅/❌ |
| **Admin additions** | `/admin/submissions/**` routes | ✅/❌ |
| **Templates** | 4-5 new HTML files, reusing fragments | ✅/❌ |
| **SecurityConfig** | `/developer/**` route secured | ✅/❌ |
| **DataInitializer** | ROLE_DEVELOPER seeded, developer account created | ✅/❌ |
| **Navigation** | Developer link in navbar, submission queue in admin | ✅/❌ |
| **Existing tests** | All pass (`mvn test`) | ✅/❌ |

### Common Agent Decisions to Discuss

- Did the agent follow the service interface + impl pattern?
- Did it use `ResourceNotFoundException` for missing submissions?
- How did it handle the "Approve → Create AppListing" conversion?
- Did it create DTOs or use the entity directly in controllers?
- Did it add proper validation annotations?
- How does its template HTML compare to existing pages?

---

## Step 4.6 — Test the Feature

After merging (or checking out the branch locally):

```bash
# Merge the PR
gh pr merge <PR_NUMBER> --squash

# Pull changes
git pull

# Build and run
mvn clean package -DskipTests
mvn spring-boot:run
```

### Test Walkthrough

1. **Login as developer**: http://localhost:8080/auth/login
   - Use `developer@appvault.com` / `Dev123!` (seeded account)
   - Or use `user@appvault.com` / `User123!` and check the developer registration flow

2. **Developer Dashboard**: Navigate to http://localhost:8080/developer/dashboard
   - Should show submission stats and any existing submissions
   - Click "Submit New App"

3. **Submit an App**:
   - Fill in the form: name, description, category, etc.
   - Click "Save as Draft" → see it in dashboard as DRAFT
   - Click "Submit for Review" → status changes to PENDING_REVIEW

4. **Admin Review**: Login as `admin@appvault.com` / `Admin123!`
   - Find "Submission Queue" in admin navigation
   - See the pending submission
   - Click to view details
   - Click "Approve" with a note → submission becomes APPROVED
   - Verify: a new `AppListing` was created and appears on `/browse`

5. **Rejection Flow** (optional):
   - Submit another app
   - As admin, reject it with feedback
   - As developer, see the rejection reason and resubmit option

---

## Step 4.7 — Manual Fallback (if Agent is Unavailable)

If the Coding Agent isn't available or taking too long, use Copilot Chat in **Agent Mode** locally:

> *"Implement the Developer Portal feature as described in this issue: [paste the issue body]. Follow the project's copilot-instructions.md conventions. Create all entities, repositories, services (interface + impl), controllers, DTOs, and Thymeleaf templates. Update SecurityConfig and DataInitializer. Run mvn test to verify."*

Or use **Copilot CLI autopilot**:

```bash
copilot --autopilot --yolo --max-autopilot-continues 30 -p "Implement the Developer Portal feature for AppVault. [paste condensed requirements]. Follow copilot-instructions.md. Run mvn test."
```

---

## Effort Comparison

| Metric | Module 02 (IDE Chat) | Module 03 (CLI Autopilot) | Module 04 (Coding Agent) |
|--------|---------------------|--------------------------|--------------------------|
| **Features** | 2–3 visual features | 2–3 data-rich features | 1 complete subsystem |
| **Prompts written** | 2–3 (one per feature) | 2–3 (with plan review) | 1 (issue description) |
| **Files you created** | 0 (Copilot applied) | 0 (autopilot applied) | 0 (agent created PR) |
| **New files created** | 0 | 0–5 | 10–15 |
| **Time actively coding** | ~35 min | ~30 min (with plan) | ~5 min (writing issue) |
| **Control level** | High — prompt per file | Medium — shaped plan | Low — agent decides |
| **Verification** | Manual | Autopilot runs tests | Agent runs tests |
| **Output** | Local changes | Local changes | PR with diff + tests |

---

## Checkpoint ✅

- [ ] GitHub Issue created with detailed Developer Portal requirements
- [ ] Coding Agent assigned to the issue
- [ ] Agent opened a PR with 10–15 new/modified files
- [ ] PR reviewed — verified conventions, patterns, completeness
- [ ] PR merged (or locally implemented via fallback)
- [ ] Developer dashboard works at `/developer/dashboard`
- [ ] App submission + admin review workflow works end-to-end
- [ ] All tests pass (`mvn test`)
- [ ] Understand the scale difference: 1 issue = entire subsystem

---

👉 Continue to **[Module 05 — Testing Integration](05-testing-integration.md)** to generate and run tests for all the features you built.
