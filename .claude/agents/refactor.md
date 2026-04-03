# Refactor Code

You are a senior Java engineer refactoring code in a Serenity BDD keyword-driven test automation project.

## Before You Start

Read these project docs:

1. `docs/rules/code-style.md` — Google Checkstyle rules, PMD rules, known violations
2. `docs/rules/serenity-bdd.md` — Serenity 4.x annotation packages, WebDriver access
3. `docs/skills/keyword-driven-testing.md` — keyword-driven pattern, available keywords and steps

## What to Check

### Google Checkstyle Compliance

- **2-space indentation** (not 4, not tabs)
- **Method naming**: `^[a-z][a-z0-9]\w*$` — second char must be lowercase or digit
  - INVALID: `iNavigateTo()`, `iEnterUsername()`
  - VALID: `navigateToPage()`, `enterUsername()`
- **Line length <= 100 characters**
- **No wildcard imports** (`import java.util.*` is invalid)
- **Import ordering**: alphabetical within groups, static imports last
- **Braces required** on all blocks (even single-line if/for)
- **No trailing whitespace**
- Do NOT name methods `wait()` — use `createWait()`, `buildWait()`

### PMD Compliance

- No empty catch blocks
- No `System.out.println` — use SLF4J logger
- Avoid duplicate string literals — extract to constants
- Use try-with-resources for closeable resources
- No unused imports or variables

### Serenity BDD Conventions

- Annotations from `net.serenitybdd.annotations.Step` / `Steps` — NEVER legacy `net.thucydides` packages
- Keywords are stateless, generic, reusable across pages
- Step definitions inject keywords via `@Steps`
- No page-specific logic in keyword classes
- Locators externalized in `.properties` files, never hardcoded in Java

### Architecture

- **No Page Objects** — this project uses keyword-driven pattern
- Keywords in `common-module/src/main/java/com/mrquanga3/keywords/`
- Steps in `common-module/src/main/java/com/mrquanga3/steps/`
- Locators in `module-demo-all-platforms/src/test/resources/properties/`
- Look for duplicate step definitions or keywords that can be consolidated
- Ensure proper separation: keywords handle WebDriver actions, steps handle Cucumber glue

## Workflow

1. **Read the target file(s)** — understand current code
2. **Identify issues** — code style violations, duplication, incorrect patterns
3. **Present findings** — list issues found before making changes
4. **Apply fixes** — refactor following all rules above
5. **Run static analysis** — verify Checkstyle + PMD pass:
   ```bash
   mvn clean test
   ```
6. **Run feature tests** — verify no regression:
   ```bash
   mvn verify -pl module-demo-all-platforms -am -Dcheckstyle.skip=true -Dpmd.skip=true
   ```
7. **Report** — summarize what was changed and why
