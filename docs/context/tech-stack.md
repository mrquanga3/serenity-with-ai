# Tech Stack

## Core Versions

| Library | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Maven | 3.x | Build tool |
| Serenity BDD | 4.1.20 | Test reporting + WebDriver lifecycle |
| Cucumber | 7.15.0 | BDD feature files |
| JUnit | 4.13.2 | Test runner (used with CucumberWithSerenity) |
| AssertJ | 3.25.3 | Fluent assertions inside keyword methods |
| SLF4J Simple | 2.0.11 | Logging backend |
| Checkstyle (Puppycrawl) | 10.14.2 | Google style enforcement |
| PMD | 6.55.0 (via plugin 3.21.2) | Static code analysis |

## Dependency Ownership per Module

### common-module (compile scope)
- `serenity-core` — provides `@Step`, `Serenity.getWebdriverManager()`, Selenium
- `assertj-core` — assertions in keyword methods

### web-module (test scope)
- `common-module` — the keyword library
- `serenity-cucumber` — Cucumber + Serenity integration
- `serenity-junit` — JUnit + Serenity integration
- `cucumber-java` + `cucumber-junit` — Cucumber runtime
- `junit` — JUnit 4 runner
- `slf4j-simple` — logging

## Why compile scope for common-module?

`WebKeywords.java` lives in `src/main/java` of `common-module`.
Test-scoped classes are NOT transitive — any module that depends on
`common-module` with test scope would NOT see the keywords class.
Using compile scope (default) makes the keywords available across modules.

## Key Serenity 4.x Package Changes

In Serenity 4.1.20, annotations moved from legacy packages:

| Annotation | Old (broken) package | Correct package |
|---|---|---|
| `@Step` | `net.thucydides.model.annotations` | `net.serenitybdd.annotations` |
| `@Steps` | `net.thucydides.model.annotations` | `net.serenitybdd.annotations` |

Both are in `serenity-model-4.1.20.jar` under `net/serenitybdd/annotations/`.
