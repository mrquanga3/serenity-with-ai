# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Multi-module Maven project demonstrating Serenity BDD with keyword-driven UI, mobile, and API testing and AI tooling.

- **Group ID:** `com.mrquanga3`
- **Language:** Java 17
- **Build tool:** Maven (multi-module)
- **Test framework:** Serenity BDD 4.1.20 + Cucumber 7 + JUnit 5
- **Pattern:** Keyword-driven (no Page Object) — locators in `.properties` files

## Modules

| Module                      | Role                                                                                               |
|-----------------------------|----------------------------------------------------------------------------------------------------|
| `common-module`             | Generic reusable keyword libraries: `WebKeywords`, `MobileKeywords`, `ApiKeywords` (compile scope) |
| `module-demo-all-platforms` | Feature files (web, mobile, API), step definitions, runner, locators                               |

See [docs/context/project-structure.md](docs/context/project-structure.md) for the full directory tree.

## Common Commands

```bash
# Static analysis only (checkstyle + PMD, no feature tests)
mvn clean test

# Full build + static analysis + feature tests + Serenity report
mvn clean verify

# Run feature tests in module-demo-all-platforms only
mvn verify -pl module-demo-all-platforms -am

# Skip static analysis for a quick test run
mvn verify -pl module-demo-all-platforms -am -Dcheckstyle.skip=true -Dpmd.skip=true

# Run only specific tagged tests (runner defaults to @api)
mvn verify -pl module-demo-all-platforms -am -Dcucumber.filter.tags="@web"

# Regenerate Serenity HTML report from existing test data
mvn serenity:aggregate -pl module-demo-all-platforms
```

Serenity report: `module-demo-all-platforms/target/site/serenity/index.html`

## Static Analysis

Checkstyle (Google style) and PMD run at the `validate` phase in **every module**.
Both are defined in the parent `pom.xml` and inherited automatically.

Before writing or editing **any file** (`.java`, `.xml`, `.properties`, `.md`), read:

- [docs/rules/code-style.md](docs/rules/code-style.md) — formatting rules for all file types, Checkstyle rules, PMD rules
- [docs/rules/serenity-bdd.md](docs/rules/serenity-bdd.md) — correct annotation packages, WebDriver access (Java only)

## Branch Workflow (MANDATORY)

At the **start of every session**, before making any changes:

1. **Check current branch** (`git branch --show-current`)
2. **If on `main`:** Suggest a new branch name based on the task and ask the user to confirm before proceeding.
3. **If on a non-main branch:** Ask the user whether to:
    - Continue working on the current branch, OR
    - Create a new branch from `main` and bring all uncommitted changes to the new branch

### Branch naming convention

Use the format: `<type>/<short-description>`

| Type        | When                                   |
|-------------|----------------------------------------|
| `feature/`  | New functionality, new docs, new tests |
| `fix/`      | Bug fixes                              |
| `refactor/` | Code restructuring, cleanup            |
| `chore/`    | Config, CI, tooling changes            |

Examples: `feature/add-login-tests`, `fix/mobile-locator-timeout`, `refactor/split-web-keywords`

### Creating a new branch from main with existing changes

```bash
# Stash current changes
git stash
# Switch to main and pull latest
git checkout main && git pull
# Create new branch
git checkout -b <type>/<short-description>
# Restore changes
git stash pop
```

### After work is done

Always create a PR to `main` — never push directly to `main`.

---

## Knowledge Base

### Context

- [docs/context/project-structure.md](docs/context/project-structure.md) — module layout, how to add new pages
- [docs/context/tech-stack.md](docs/context/tech-stack.md) — dependency versions, scope decisions, Serenity 4.x package
  changes

### RAG (Retrieval-Augmented Generation)

- [docs/rag/page-index.md](docs/rag/page-index.md) — page/screen index: elements, endpoints, test coverage,
  navigation flows. Used by AI agents for context retrieval when generating or fixing tests.

### Rules

- [docs/rules/code-style.md](docs/rules/code-style.md) — formatting rules for all file types, Checkstyle rules, PMD rules
- [docs/rules/serenity-bdd.md](docs/rules/serenity-bdd.md) — Serenity 4.x specifics, WebDriver access, runner config

### Skills

- [docs/skills/keyword-driven-testing.md](docs/skills/keyword-driven-testing.md) — pattern overview, locator format,
  adding new keywords
- [docs/skills/multi-module-maven.md](docs/skills/multi-module-maven.md) — parent/child pom setup, plugin inheritance,
  adding new modules
