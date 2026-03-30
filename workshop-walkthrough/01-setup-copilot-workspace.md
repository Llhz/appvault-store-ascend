# Module 01 — Setup Copilot Workspace

> **Goal**: Fork the repository, verify GitHub Copilot is working, and enable GitHub Advanced Security on your fork. This is the foundation for all subsequent modules.

> ⏱️ **Duration**: ~10 minutes

---

## 🎯 Objectives

- Fork the workshop repository to your own GitHub account
- Verify Copilot Chat is active and can read the workspace
- Explore the pre-configured Copilot customization files
- Enable GitHub Advanced Security (Dependabot, Code Scanning, Secret Scanning)

---

## Step 1.1 — Fork the Repository

1. Go to the workshop repository on GitHub: `<instructor-provided-url>`
2. Click **Fork** → create a fork under your own GitHub account
3. Clone your fork locally:

```bash
git clone https://github.com/<your-username>/appvault-store.git
cd appvault-store
```

4. Verify the build:

```bash
mvn clean package -DskipTests
```

> 💡 **Instructor Note**: Provide the repository URL on screen. If attendees already cloned during prerequisites, they can add their fork as a remote instead: `git remote set-url origin https://github.com/<your-username>/appvault-store.git`

---

## Step 1.2 — Verify GitHub Copilot

1. Open the project in VS Code: `code .`
2. Look for the **Copilot icon** (✓) in the bottom-right status bar
3. Open **Copilot Chat** panel: `Ctrl+Shift+I` (Windows/Linux) or `Cmd+Shift+I` (macOS)

### Smoke Test — Ask Copilot About the Project

Type in Copilot Chat:

```
What controllers exist in this project and what routes do they handle?
```

**Expected**: Copilot should list 6 controllers (Home, Auth, App, User, Review, Admin) with their route prefixes. This confirms workspace indexing is working.

> If Copilot doesn't respond or shows a sign-in warning, click the **Accounts** icon in the VS Code sidebar and sign in with your GitHub account.

---

## Step 1.3 — Explore Copilot Customization

The repository comes pre-loaded with customization files that automatically guide every Copilot interaction:

```
.github/
├── copilot-instructions.md     ← Auto-loaded into every Copilot context
├── agents/                     ← 19 custom agent definitions
│   ├── tdd-red.agent.md
│   ├── tdd-green.agent.md
│   ├── tdd-refactor.agent.md
│   ├── cypress-test-runner.agent.md
│   ├── playwright-test-runner.agent.md
│   ├── robot-test-runner.agent.md
│   ├── k6-test-runner.agent.md
│   ├── implementation-plan.agent.md
│   └── ...
└── skills/                     ← 18 domain-knowledge modules
    ├── java-springboot/SKILL.md
    ├── java-junit/SKILL.md
    ├── test-runner/SKILL.md
    ├── conventional-commit/SKILL.md
    └── ...
```

### Quick Look at copilot-instructions.md

Open `.github/copilot-instructions.md` and skim it. Notice how it documents:

- **Build commands**: `mvn clean package -DskipTests`, `mvn test`
- **Architecture**: Controller → Service (interface + impl) → Repository → Entity
- **Conventions**: `@Transactional` on writes, `@Transactional(readOnly = true)` on reads
- **Security model**: roles, protected routes
- **Patterns**: DTOs for form binding, `ResourceNotFoundException` for missing entities

> 💡 **Key insight**: These instructions are **automatically loaded** into Copilot's context for every interaction in this workspace. This is why Copilot will "know" the project's conventions without being told each time. This is the single highest-impact customization you can make.

---

## Step 1.4 — Enable GitHub Advanced Security

On your forked repository, enable security features:

1. Go to your fork on GitHub: `https://github.com/<your-username>/appvault-store`
2. Click **Settings** → **Code security and analysis** (left sidebar)
3. Enable these features:

| Feature | What It Does | Enable |
|---------|-------------|--------|
| **Dependabot alerts** | Flags known vulnerabilities in dependencies | ✅ Enable |
| **Dependabot security updates** | Auto-creates PRs to fix vulnerable deps | ✅ Enable |
| **Code scanning (CodeQL)** | Static analysis for security vulnerabilities | ✅ Enable (default setup) |
| **Secret scanning** | Detects accidentally committed secrets/tokens | ✅ Enable |

> 💡 **Instructor Note**: GitHub Advanced Security is **free for public repositories**. If attendees forked as private repos on personal accounts, some features may require GitHub Enterprise.

---

## Step 1.5 — Verify Copilot CLI

Ensure Copilot CLI is installed and ready:

```bash
# Verify installation
copilot --version

# Start an interactive session to test auth
copilot
```

On first launch:
1. Trust the files in the workspace folder (choose "Yes, and remember this folder")
2. If prompted, use `/login` and follow the browser-based OAuth flow
3. Try a quick prompt: `Explain what this project does based on the README`
4. Type `/exit` to end the session

Also verify `gh` is authenticated:

```bash
gh auth status
```

---

## Step 1.6 — Setup Atlassian MCP (for Module 03)

If your workshop uses JIRA integration, configure the Atlassian MCP server now.

#### Option A — VS Code (settings.json)

Add to `.vscode/mcp.json` or User Settings:

```json
{
  "servers": {
    "Atlassian": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "mcp-remote", "https://mcp.atlassian.com/v1/mcp"]
    }
  }
}
```

#### Option B — Copilot CLI

```bash
copilot
# Inside the session:
/mcp add Atlassian
# Select STDIO transport, command: npx -y mcp-remote https://mcp.atlassian.com/v1/mcp
```

**Verify MCP Connection** (in Copilot Chat, Agent Mode):

> *"List available Jira projects using the Atlassian MCP"*

> 💡 **Instructor Note**: MCP integration (e.g., JIRA) is optional in Module 03 — the CLI plan/autopilot features work independently. MCP adds a nice "workflow hub" story if available.

---

## Checkpoint ✅

- [ ] Repository forked to your GitHub account
- [ ] `mvn clean package -DskipTests` → BUILD SUCCESS on the fork
- [ ] Copilot Chat responds to workspace questions
- [ ] You've skimmed `.github/copilot-instructions.md`
- [ ] GitHub Advanced Security features enabled on the fork
- [ ] `gh auth status` shows you're logged in
- [ ] `copilot --version` works and Copilot CLI session starts
- [ ] (Optional) Atlassian MCP connected and verified

---

👉 Continue to **[Module 02 — IDE Features](02-ide-features.md)** to start building visually impactful features with Copilot Chat.
