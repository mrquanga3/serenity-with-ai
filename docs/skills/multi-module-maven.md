# Multi-Module Maven Setup

## Structure

```
parent pom (packaging=pom)
├── common-module (packaging=jar, compile scope)
└── web-module    (packaging=jar, test scope classes)
```

---

## Parent POM Responsibilities
 
| Section                      | What Goes Here                                        |
|------------------------------|-------------------------------------------------------|
| `<packaging>pom</packaging>` | Required for parent/aggregator                        |
| `<modules>`                  | List all child modules                                |
| `<properties>`               | Shared version numbers                                |
| `<dependencyManagement>`     | Declare versions — children reference without version |
| `<build><plugins>`           | Checkstyle + PMD — inherited by all child modules     |

---

## Child Module Inheritance

Plugins declared in parent `<build><plugins>` are **automatically inherited**.
Each child module runs them during their own lifecycle phases.

Plugins that should only run in one module (surefire, serenity-maven-plugin)
belong in that module's own `<build><plugins>`, NOT in the parent.

---

## Sharing Test Utilities Across Modules

**Problem:** Test-scoped classes (`src/test/java`) are NOT transitive.
If `common-module` has `WebKeywords` as a test class, `web-module` cannot use it.

**Solution:** Put shared utilities in `src/main/java` of `common-module` with compile scope.

```
common-module/src/main/java/  ← compile scope, transitive to dependents
web-module/src/test/java/     ← test scope, private to web-module
```

`web-module/pom.xml` depends on `common-module` without scope (= compile default):

```xml

<dependency>
    <groupId>com.mrquanga3</groupId>
    <artifactId>common-module</artifactId>
    <version>${project.version}</version>
</dependency>
```

---

## Build Reactor Order

Maven resolves build order automatically from dependency graph:

1. Parent (pom) — always first
2. `common-module` — no inter-module dependencies
3. `web-module` — depends on `common-module`, so built after it

---

## Useful Commands

```bash
# Build everything
mvn clean verify

# Build only one module (and its dependencies)
mvn clean verify -pl web-module -am

# Skip static analysis for a quick test run
mvn test -pl web-module -am -Dcheckstyle.skip=true -Dpmd.skip=true

# Run a specific feature tag (when tags are added)
mvn test -pl web-module -am -Dcucumber.filter.tags="@login"
```

---

## Adding a New Test Module

1. Create `<new-module>/pom.xml` with parent reference
2. Add `<module>new-module</module>` to parent `pom.xml`
3. Depend on `common-module` to reuse `WebKeywords`
4. Add surefire + serenity-maven-plugin to the new module's pom
5. Add feature files, step defs, properties, serenity.conf

Checkstyle and PMD will automatically apply — no extra config needed.
