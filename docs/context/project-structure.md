# Project Structure

## Overview

Multi-module Maven project demonstrating Serenity BDD with keyword-driven UI, mobile, and API testing and AI tooling.
Group ID: `com.mrquanga3` | Java 17 | Maven

## Module Layout

```
DemSerenityWithAI/                  ← parent POM (packaging=pom)
├── pom.xml
├── CLAUDE.md
├── docs/                           ← project knowledge base
│
├── common-module/                  ← shared reusable keyword + step library
│   ├── pom.xml
│   └── src/main/java/com/mrquanga3/
│       ├── common/
│       │   └── Common.java         ← global variables (save-to-variable pattern)
│       ├── keywords/
│       │   ├── WebKeywords.java    ← ~75 SeleniumLibrary-style web keywords
│       │   ├── MobileKeywords.java ← ~30 Appium mobile keywords
│       │   ├── ApiKeywords.java    ← REST API keywords (Serenity REST Assured)
│       │   └── DbKeywords.java     ← database keywords (JDBC queries, assertions)
│       ├── steps/
│       │   ├── CommonWebSteps.java    ← ~75 generic web Cucumber step definitions
│       │   ├── CommonMobileSteps.java ← ~30 generic mobile Cucumber step definitions
│       │   ├── CommonApiSteps.java    ← generic API Cucumber step definitions
│       │   └── CommonDbSteps.java     ← generic DB Cucumber step definitions
│       └── utils/
│           ├── ActorManager.java   ← multi-actor session manager (web + mobile)
│           └── PropertiesLoader.java
│
└── module-demo-all-platforms/                     ← web + mobile test scenarios
    ├── pom.xml
    └── src/test/
        ├── java/com/mrquanga3/
        │   └── runner/CucumberTestRunner.java  ← JUnit 5 Platform Suite runner
        └── resources/
            ├── features/
            │   ├── login/
            │   │   ├── login.feature
            │   │   └── login-multi-actor.feature
            │   ├── mobile/
            │   │   └── message-app.feature     ← Android Messages app scenarios
            │   ├── api/
            │   │   └── api-demo.feature        ← REST API testing scenarios (@api)
            │   ├── db/
            │   │   └── db-demo.feature         ← database testing scenarios
            │   └── cross-platform/
            │       └── web-then-mobile.feature ← web login → mobile search
            ├── properties/
            │   ├── SIT/environment.properties  ← SIT env URLs + credentials + apiBaseUrl
            │   ├── UAT/environment.properties  ← UAT env URLs + credentials + apiBaseUrl
            │   ├── login/login.properties      ← web locators (type:value)
            │   ├── api/api.properties          ← API endpoint paths
            │   └── mobile/
            │       ├── android-emulator.properties ← device profile
            │       └── message-app.properties      ← mobile locators
            ├── junit-platform.properties       ← parallel execution config
            └── serenity.conf
```

## Module Responsibilities

| Module          | Scope   | Purpose                                                      |
|-----------------|---------|--------------------------------------------------------------|
| `common-module` | compile | Keywords (Web, Mobile, API, DB), common steps, multi-actor support (ActorManager) |
| `module-demo-all-platforms`    | test    | Feature files (web, mobile, API, DB), locators, environment config, runner        |

## Adding a New Page/Feature

1. Add locators to `module-demo-all-platforms/src/test/resources/properties/<page>/<page>.properties`
2. Add feature file to `module-demo-all-platforms/src/test/resources/features/<page>/`
3. Add URLs/credentials to `module-demo-all-platforms/src/test/resources/properties/{env}/environment.properties`
4. Generic steps (navigate, click, enter text, verify) are in `CommonWebSteps` — no duplication needed
5. Add domain-specific step definitions only if the generic ones don't cover the scenario

## Serenity Report Location

After `mvn clean verify`:

```
module-demo-all-platforms/target/site/serenity/index.html
```
