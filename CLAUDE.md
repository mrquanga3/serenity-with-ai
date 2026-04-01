# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

Multi-module Maven project demonstrating Serenity BDD with keyword-driven UI testing and AI tooling.

- **Group ID:** `com.mrquanga3`
- **Language:** Java 17
- **Build tool:** Maven (multi-module)
- **Test framework:** Serenity BDD 4.1.20 + Cucumber 7 + JUnit 5
- **Pattern:** Keyword-driven (no Page Object) — locators in `.properties` files

## Modules

| Module | Role |
|---|---|
| `common-module` | Generic reusable `WebKeywords` library (compile scope) |
| `module-demo-all-platforms` | Login feature files, step definitions, runner, locators |

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

# Run only specific tagged tests
mvn verify -pl module-demo-all-platforms -am -Dcucumber.filter.tags="@web"

# Regenerate Serenity HTML report from existing test data
mvn serenity:aggregate -pl module-demo-all-platforms
```

Serenity report: `module-demo-all-platforms/target/site/serenity/index.html`

## Static Analysis

Checkstyle (Google style) and PMD run at the `validate` phase in **every module**.
Both are defined in the parent `pom.xml` and inherited automatically.

Before writing any Java code, read:
- [docs/rules/code-style.md](docs/rules/code-style.md) — Checkstyle rules, known violations, PMD rules
- [docs/rules/serenity-bdd.md](docs/rules/serenity-bdd.md) — correct annotation packages, WebDriver access

## Knowledge Base

### Context
- [docs/context/project-structure.md](docs/context/project-structure.md) — module layout, how to add new pages
- [docs/context/tech-stack.md](docs/context/tech-stack.md) — dependency versions, scope decisions, Serenity 4.x package changes

### Rules
- [docs/rules/code-style.md](docs/rules/code-style.md) — Google Checkstyle rules, PMD rules, common violations
- [docs/rules/serenity-bdd.md](docs/rules/serenity-bdd.md) — Serenity 4.x specifics, WebDriver access, runner config

### Skills
- [docs/skills/keyword-driven-testing.md](docs/skills/keyword-driven-testing.md) — pattern overview, locator format, adding new keywords
- [docs/skills/multi-module-maven.md](docs/skills/multi-module-maven.md) — parent/child pom setup, plugin inheritance, adding new modules
