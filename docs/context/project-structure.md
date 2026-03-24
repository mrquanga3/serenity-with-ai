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
├── common-module/                  ← shared reusable keyword library
│   ├── pom.xml
│   └── src/main/java/com/mrquanga3/keywords/
│       └── WebKeywords.java        ← generic browser keyword actions
│
└── web-module/                     ← web UI test scenarios
    ├── pom.xml
    └── src/test/
        ├── java/com/mrquanga3/
        │   ├── runner/CucumberTestRunner.java
        │   └── steps/LoginSteps.java
        └── resources/
            ├── features/login/login.feature
            ├── login/login.properties   ← locators (type:value format)
            └── serenity.conf
```

## Module Responsibilities

| Module          | Scope   | Purpose                                      |
|-----------------|---------|----------------------------------------------|
| `common-module` | compile | Generic keywords reusable by any test module |
| `web-module`    | test    | Feature files, step defs, locators, runner   | 

## Adding a New Page/Feature

1. Add locators to `web-module/src/test/resources/<page>/<page>.properties`
2. Add feature file to `web-module/src/test/resources/features/<page>/`
3. Add step definitions to `web-module/src/test/java/com/mrquanga3/steps/`
4. Reuse `WebKeywords` from `common-module` — no changes needed there

## Serenity Report Location

After `mvn clean verify`:

```
web-module/target/site/serenity/index.html
```
