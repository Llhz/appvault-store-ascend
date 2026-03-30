# Module 06 — Comparison & Wrap-Up

> **Goal**: Compare the three implementation approaches side-by-side, connect to GitHub Advanced Security, and build a framework for choosing the right Copilot workflow.

> ⏱️ **Duration**: ~10–15 minutes

---

## 🎯 Objectives

- Directly compare Module 02 (IDE Chat) vs Module 03 (CLI) vs Module 04 (Coding Agent)
- Understand the progressive capability spectrum
- Connect GitHub Advanced Security features to AI-generated code
- Identify next steps for adoption

---

## Side-by-Side Comparison

### Module 02: IDE Copilot Chat

| Aspect | Detail |
|--------|--------|
| **Features** | App of the Day, Dark Mode, Screenshot Lightbox |
| **Nature** | Visual/UI-focused — CSS, templates, JavaScript |
| **Approach** | Precise prompt → Copilot generates → you apply |
| **Files per feature** | 2–3 |
| **Control level** | **High** — you craft each prompt, review each change |
| **Key skill demonstrated** | Prompt engineering (vague vs. precise) |
| **Best for** | UI polish, CSS, cross-layer changes where you want fine control |

### Module 03: Copilot CLI

| Aspect | Detail |
|--------|--------|
| **Features** | Analytics Dashboard (charts), Live Search Auto-suggest |
| **Nature** | Data-heavy — new endpoints, JSON APIs, Chart.js |
| **Approach** | JIRA ticket → plan mode → autopilot mode |
| **Files per feature** | 4–6 |
| **Control level** | **Medium** — you shaped the plan, agent decided details |
| **Key skill demonstrated** | Plan mode, autopilot, MCP integration |
| **Best for** | Multi-file features, API + frontend, when plan review matters |

### Module 04: Coding Agent

| Aspect | Detail |
|--------|--------|
| **Features** | Developer Portal (complete subsystem) |
| **Nature** | Full-stack — new role, entity, service, controller, 5 templates, security |
| **Approach** | One GitHub Issue → agent implements autonomously |
| **Files per feature** | 10–15 |
| **Control level** | **Low** — agent made architectural decisions |
| **Key skill demonstrated** | Issue quality, convention documentation |
| **Best for** | Large features, new subsystems, when conventions are well-documented |

---

## The Effort Matrix

| Metric | IDE Chat (M02) | CLI Autopilot (M03) | Coding Agent (M04) |
|--------|:-:|:-:|:-:|
| **Prompts written** | 2–3 precise | 2–3 (with plan review) | 1 (issue) |
| **Files modified/created** | ~6–8 total | ~8–12 total | ~10–15 |
| **Time actively working** | ~35 min | ~30 min | ~5 min |
| **Time waiting** | 0 | ~2 min (autopilot) | ~5–10 min |
| **Control over output** | High | Medium | Low |
| **Learning from process** | High | Medium | Low |
| **Self-verification** | Manual | Autopilot runs tests | Agent runs tests |
| **Output format** | Local changes | Local changes | PR with diff |

---

## The Capability Spectrum

```
                    ← More Control                    More Autonomy →
                    ← Smaller Scope                   Larger Scope  →
                    ← More Learning                   Less Learning  →

    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
    │  IDE Chat     │    │  Copilot CLI  │    │   Coding     │
    │  (Module 02)  │──→│  (Module 03)  │──→│   Agent      │
    │               │    │               │    │  (Module 04) │
    │ • Prompt eng. │    │ • Plan mode   │    │ • GitHub     │
    │ • Plan mode   │    │ • Autopilot   │    │   Issues     │
    │ • MCP (best)  │    │ • MCP         │    │ • PR-based   │
    │ • Fine-grain  │    │ • Multi-file  │    │ • MCP        │
    │   control     │    │   autonomous  │    │   (limited)  │
    └──────────────┘    └──────────────┘    └──────────────┘
```

### The Bridge: `/delegate`

Copilot CLI's `/delegate` command bridges local and cloud workflows:
- Start exploring locally → delegate implementation to Coding Agent
- Plan locally → agent executes in the cloud
- Review plan → change mind → implement locally instead

---

## The Full Capability Matrix

| Capability | IDE Chat | Copilot CLI | Coding Agent | GitHub Platform |
|------------|:---:|:---:|:---:|:---:|
| Code completion | ✅ | — | — | — |
| Multi-file editing | Agent Mode | ✅ (autopilot) | ✅ | — |
| Plan mode | ✅ | ✅ | — | — |
| Autopilot (autonomous) | ❌ | ✅ | ✅ | — |
| Terminal commands | Agent Mode | ✅ | ✅ | — |
| Self-verification | Agent Mode | ✅ | ✅ | — |
| Creates PRs | ✅ (GitHub MCP) | ✅ (GitHub MCP) | ✅ | — |
| Reads copilot-instructions.md | ✅ | ✅ | ✅ | — |
| Custom agents/skills | ✅ | ✅ | ✅ | — |
| MCP integration | ✅ (best) | ✅ | ✅ (limited) | — |
| Session resume | ✅ | ✅ | ✅ (in PR) | — |
| Delegate to cloud | ✅ | ✅ | ✅ | — |
| Code scanning (CodeQL) | — | — | — | ✅ |
| Secret scanning | — | — | — | ✅ |
| Dependency review | — | — | — | ✅ |
| Copilot PR review | — | — | — | ✅ |

---

## GitHub Advanced Security — Connecting the Dots

The GHAS features enabled in Module 01 apply to everything you built:

### CodeQL Analysis

- **Dark Mode**: Catches XSS if dark mode toggle injects unsanitized HTML
- **Analytics endpoints**: Catches SQL injection if using raw queries instead of JPQL
- **Developer Portal**: Catches missing access control on submission endpoints

### Secret Scanning

- Detects hardcoded API keys or credentials committed during development
- Push protection blocks commits containing detected secrets

### Dependency Review

- If Chart.js or any new dependency was added, checks for known CVEs
- Alerts on vulnerable transitive dependencies

### Copilot Code Review

- Automatically reviews the Coding Agent's PR (Module 04)
- Catches: missing `@Transactional`, unclosed resources, error handling gaps
- Suggests improvements based on project conventions

> 💡 **Key insight**: GHAS provides the safety net that makes agentic workflows trustworthy. Without automated security scanning, you'd need manual review of every agent-generated line.

---

## When to Use What

### Use IDE Copilot Chat when:
- Learning a new codebase — step-by-step exploration
- UI/CSS work where visual precision matters
- Debugging complex issues with fine-grained control
- Single-file or 2–3 file changes
- You want to understand every line produced

### Use Copilot CLI (Plan + Autopilot) when:
- Multi-file features (4+ files) with clear scope
- You want to review the plan before implementation
- Terminal-centric workflow — no IDE switching
- Repeatable tasks (programmatic `--autopilot`)
- External context via MCP (JIRA, Confluence — though IDE has richer MCP support)

### Use Coding Agent when:
- Large features that span many files (8+)
- Well-documented conventions (`copilot-instructions.md`)
- Async workflow — submit and go do other work
- GitHub Issue-driven workflow with PR review
- CI/CD + GHAS provide automated safety checks

---

## Copilot Customization Impact

Throughout the workshop, customization files shaped every Copilot output:

| File | Impact |
|------|--------|
| **`copilot-instructions.md`** | Auto-loaded — Copilot knew `@Transactional`, service interface+impl, DTOs, `ResourceNotFoundException` |
| **`.github/agents/`** (19 agents) | Specialized test runners, TDD workflow, code review agents |
| **`.github/skills/`** (18 skills) | Spring Boot best practices, JUnit patterns, refactoring techniques |

> 📝 The single highest-impact thing you can do: create a `copilot-instructions.md` for your own project. 10 minutes of documentation improves every Copilot interaction.

---

## Discussion Questions

1. **Visual impact**: Which feature gave you the biggest "wow" moment? App of the Day? Dark mode? Charts? Developer Portal?

2. **Trust threshold**: Would you merge the Coding Agent's Developer Portal PR without reviewing every line? What safeguards help?

3. **Convention documentation**: How well-documented are your project's conventions? Would a Coding Agent succeed in your codebase?

4. **Workflow fit**: Which Copilot mode (Chat / CLI / Agent) fits your daily work best? Why?

5. **JIRA integration**: How would JIRA MCP change your team's workflow if tickets became prompts?

---

## What We Covered Today

| Module | Feature(s) | Key Copilot Workflow |
|--------|-----------|---------------------|
| **00** | — | Prerequisites & environment setup |
| **01** | — | `copilot-instructions.md`, GHAS, CLI setup, MCP setup |
| **02** | App of the Day + Dark Mode + Lightbox | IDE Copilot Chat — prompt engineering |
| **03** | Analytics Charts + Live Search + Notifications | Copilot CLI plan + autopilot (+ MCP optional) |
| **04** | Developer Portal (10-15 files) | Coding Agent via GitHub Issues |
| **05** | Tests for all features | Multi-framework test generation |
| **06** | — | Comparison, GHAS, adoption framework |

---

## Resources

### Copilot Documentation

- [GitHub Copilot Documentation](https://docs.github.com/en/copilot)
- [Copilot Customization (instructions, prompts, agents)](https://docs.github.com/en/copilot/customizing-copilot)
- [Copilot Coding Agent](https://docs.github.com/en/copilot/using-github-copilot/using-copilot-coding-agent)
- [Copilot CLI](https://docs.github.com/en/copilot/how-tos/copilot-cli/use-copilot-cli)
- [Copilot CLI Autopilot Mode](https://docs.github.com/en/copilot/concepts/agents/copilot-cli/autopilot)

### GitHub Advanced Security

- [Code Scanning with CodeQL](https://docs.github.com/en/code-security/code-scanning)
- [Secret Scanning](https://docs.github.com/en/code-security/secret-scanning)
- [Dependency Review](https://docs.github.com/en/code-security/supply-chain-security/understanding-your-software-supply-chain/about-dependency-review)

### MCP (Model Context Protocol)

- [MCP Specification](https://modelcontextprotocol.io/)
- [Atlassian MCP Server](https://developer.atlassian.com/cloud/mcp/)

---

## Checkpoint ✅

- [ ] Reviewed side-by-side comparison of all three modules
- [ ] Understand the capability spectrum: Chat → CLI → Agent
- [ ] Connected GHAS features to AI-generated code
- [ ] Identified one action item for your own project
- [ ] Workshop complete! 🎉

---

> **Thank you for attending!** The AppVault Store repository and all workshop materials remain available for revisiting and practice.
