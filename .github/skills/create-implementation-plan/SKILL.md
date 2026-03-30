---
name: create-implementation-plan
description: 'Create a new implementation plan file for new features, refactoring existing code or upgrading packages, design, architecture or infrastructure.'
---

# Create Implementation Plan

## Primary Directive

Your goal is to create a new implementation plan file for `${input:PlanPurpose}`. Your output must be machine-readable, deterministic, and structured for autonomous execution by other AI systems or humans.

## Execution Context

This prompt is designed for AI-to-AI communication and automated processing. All instructions must be interpreted literally and executed systematically without human interpretation or clarification.

## Core Requirements

- Generate implementation plans that are fully executable by AI agents or humans
- Use deterministic language with zero ambiguity
- Structure all content for automated parsing and execution
- Ensure complete self-containment with no external dependencies for understanding

## Plan Structure Requirements

Plans must consist of discrete, atomic phases containing executable tasks. Each phase must be independently processable by AI agents or humans without cross-phase dependencies unless explicitly declared.

## Phase Architecture

- Each phase must have measurable completion criteria
- Tasks within phases must be executable in parallel unless dependencies are specified
- All task descriptions must include specific file paths, function names, and exact implementation details
- No task should require human interpretation or decision-making

## AI-Optimized Implementation Standards

- Use explicit, unambiguous language with zero interpretation required
- Structure all content as machine-parseable formats (tables, lists, structured data)
- Include specific file paths, line numbers, and exact code references where applicable
- Define all variables, constants, and configuration values explicitly
- Provide complete context within each task description
- Use standardized prefixes for all identifiers (REQ-, TASK-, etc.)
- Include validation criteria that can be automatically verified

## Output File Specifications

- Save implementation plan files in `/plan/` directory
- Use naming convention: `[purpose]-[component]-[version].md`
- Purpose prefixes: `upgrade|refactor|feature|data|infrastructure|process|architecture|design`
- Example: `upgrade-system-command-4.md`, `feature-auth-module-1.md`
- File must be valid Markdown with proper front matter structure

## Status

The status of the implementation plan must be clearly defined in the front matter and must reflect the current state of the plan. The status can be one of the following: `Completed`, `In progress`, `Planned`, `Deprecated`, or `On Hold`.

## Mandatory Template Structure

```md
---
goal: [Concise Title Describing the Package Implementation Plan's Goal]
version: [Optional: e.g., 1.0, Date]
date_created: [YYYY-MM-DD]
last_updated: [Optional: YYYY-MM-DD]
owner: [Optional: Team/Individual responsible for this spec]
status: 'Completed'|'In progress'|'Planned'|'Deprecated'|'On Hold'
tags: [Optional: List of relevant tags or categories]
---

# Introduction

[A short concise introduction to the plan and the goal it is intended to achieve.]

## 1. Requirements & Constraints

- **REQ-001**: Requirement 1
- **CON-001**: Constraint 1
- **GUD-001**: Guideline 1

## 2. Implementation Steps

### Implementation Phase 1

- GOAL-001: [Describe the goal of this phase]

| Task | Description | Completed | Date |
|------|-------------|-----------|------|
| TASK-001 | Description of task 1 | | |
| TASK-002 | Description of task 2 | | |

## 3. Alternatives

- **ALT-001**: Alternative approach 1

## 4. Dependencies

- **DEP-001**: Dependency 1

## 5. Files

- **FILE-001**: Description of file 1

## 6. Testing

- **TEST-001**: Description of test 1

## 7. Risks & Assumptions

- **RISK-001**: Risk 1
- **ASSUMPTION-001**: Assumption 1

## 8. Related Specifications / Further Reading

[Link to related spec 1]
```
