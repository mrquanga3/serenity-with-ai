# Project Structure

## Overview

Multi-module Maven project demonstrating Serenity BDD with keyword-driven testing and AI tooling.
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
│       ├── keywords/
│       │   └── WebKeywords.java    ← generic browser keyword actions
│       ├── steps/
│       │   └── CommonSteps.java    ← generic reusable Cucumber step defs
│       └── utils/
│           ├── ActorManager.java   ← multi-actor browser session manager
│           └── PropertiesLoader.java
│
└── web-module/                     ← web UI test scenarios
    ├── pom.xml
    └── src/test/
        ├── java/com/mrquanga3/
        │   └── runner/CucumberTestRunner.java
        └── resources/
            ├── features/login/
            │   ├── login.feature
            │   └── login-multi-actor.feature
            ├── properties/
            │   ├── environment.properties  ← URLs and account credentials
            │   └── login/login.properties  ← locators (type:value format)
            └── serenity.conf
```

## Module Responsibilities

| Module          | Scope   | Purpose                                                      |
|-----------------|---------|--------------------------------------------------------------|
| `common-module` | compile | Keywords, common steps, multi-actor support (ActorManager)    |
| `web-module`    | test    | Feature files, locators, environment config, runner          |

## Adding a New Page/Feature

1. Add locators to `web-module/src/test/resources/properties/<page>/<page>.properties`
2. Add feature file to `web-module/src/test/resources/features/<page>/`
3. Add URLs/credentials to `web-module/src/test/resources/properties/environment.properties`
4. Generic steps (navigate, click, enter text, verify) are in `CommonSteps` — no duplication needed
5. Add domain-specific step definitions only if the generic ones don't cover the scenario

## Serenity Report Location

After `mvn clean verify`:

```
web-module/target/site/serenity/index.html
```
