# Code Style Rules

## Enforcement

Both Checkstyle and PMD run at the `validate` phase (before compile) and **fail the build** on violations.
Defined once in the parent `pom.xml` and inherited by all modules.

Skip both for a single run:

```bash
mvn test -Dcheckstyle.skip=true -Dpmd.skip=true
```

---

## Google Checkstyle (`google_checks.xml`)

### Indentation

- **2 spaces** (not 4, not tabs) for Java source files.
- XML/pom.xml files are not checked — 4 spaces is fine there.

### Method Naming — `MethodName` rule

Pattern: `^[a-z][a-z0-9]\w*$`

The **second character must also be lowercase or digit**.
This means `iNavigateTo()` is INVALID — `i` followed by uppercase `N` fails.

| Invalid             | Valid               |
|---------------------|---------------------|
| `iNavigateToPage()` | `navigateToPage()`  |
| `iEnterUsername()`  | `enterUsername()`   | 
| `iShouldSeeX()`     | `verifySomething()` |

> Cucumber step definitions: the method name is irrelevant to Cucumber — only the
> annotation regex matters. Rename freely without breaking scenarios.

### Import Ordering — `CustomImportOrder` rule

Imports must be in strict alphabetical/lexicographic order within each group.
Static imports go **last** (after all regular imports).

```java
// CORRECT

import net.serenitybdd.annotations.Step;   // 'annotations' < 'core'
import net.serenitybdd.core.Serenity;

// WRONG
import net.serenitybdd.core.Serenity;
import net.serenitybdd.annotations.Step;
```

### Method Name Conflicts

Do NOT name methods `wait()` — it conflicts with `java.lang.Object.wait()` which is `final`.
Use `createWait()`, `buildWait()`, or similar.

### Other Active Rules

- No trailing whitespace
- Braces required on all blocks
- Line length ≤ 100 characters
- No wildcard imports (`import java.util.*` is invalid)

---

## PMD (`/rulesets/java/quickstart.xml`)

Covers: bestpractices, errorprone, multithreading, performance.

### Common Rules to Respect

- **No empty catch blocks** — always rethrow or wrap
- **No `System.out.println`** — use a logger
- **Avoid duplicate string literals** — extract to constants
- **Close resources in finally/try-with-resources** — use `try (InputStream is = ...)` pattern

### PMD Version Note

The `maven-pmd-plugin:3.21.2` bundles PMD 6.55.0 (not 7.x).
The ruleset path `/rulesets/java/quickstart.xml` is valid for PMD 6.x.
