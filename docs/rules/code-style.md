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

Config file: `config/checkstyle/checkstyle.xml`

### Indentation

| Setting                  | Value                  |
|--------------------------|------------------------|
| Basic indent             | **2 spaces** (no tabs) |
| Continuation line indent | 4 spaces               |
| Brace adjustment         | 2                      |
| Case indent              | 2 spaces               |
| Array initializer indent | 2 spaces               |
| Throws indent            | 4 spaces               |

See [XML and Properties Files](#xml-and-properties-files) for non-Java formatting rules.

```java
// Basic indent: 2 spaces
public void navigateTo(String url) {
  driver().get(url);
}

// Continuation indent: 4 spaces
private WebDriverWait createWait() {
  return new WebDriverWait(
      driver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
}

// Case indent: 2 spaces
switch (type) {
  case "GET":
    response = spec.get(url);
    break;
  default:
    throw new IllegalArgumentException("Unknown: " + type);
}
```

### Line Length

- **Maximum 100 characters** per line.
- Exempt: `package`, `import`, and lines containing URLs.

### Braces

- **Opening brace on same line** as declaration (K&R / Google style).
- **Braces required** on all `if`, `for`, `while`, `do` — even single-line bodies.

```java
// CORRECT
if (locator.startsWith("id:")) {
  return By.id(locator.substring(3));
}

// WRONG — missing braces
if (locator.startsWith("id:"))
  return By.id(locator.substring(3));
```

### Import Ordering

Three groups, separated by blank lines, each sorted alphabetically:

1. **Third-party packages** (`com.*`, `io.*`, `net.*`, `org.*`, etc.)
2. **Standard library** (`java.*`, `javax.*`)
3. **Static imports** (always last)

No wildcard imports (`import java.util.*` is invalid).

```java
import com.mrquanga3.utils.ActorManager;       // 1. third-party
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;                     // 2. java.*
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;  // 3. static
```

### Line Wrapping

When a line exceeds 100 characters, wrap with these rules:

- **Dot** goes on the **new line** (start of continuation):

```java
return new Select(el).getFirstSelectedOption()
    .getAttribute("value");
```

- **Comma** stays at the **end of the line**:

```java
public void setWindowSize(int width,
    int height) {
```

- **Binary operators** (`+`, `&&`, `||`, etc.) go on the **new line**:

```java
throw new IllegalArgumentException(
    "Unknown locator format: " + locator);
```

### Whitespace

- **Space after keywords:** `if (`, `for (`, `while (`, `switch (`, `try {`, `catch (`
- **Space around binary operators:** `=`, `+`, `-`, `==`, `!=`, `&&`, `||`, `?`, `:`
- **No space before method parens:** `method()` not `method ()`
- **No space inside parens:** `(x, y)` not `( x, y )`
- **No trailing whitespace** on any line

```java
// CORRECT
if (locator.startsWith("id:")) {
  return By.id(locator.substring(3));
}

// WRONG
if(locator.startsWith( "id:" )){
  return By.id( locator.substring( 3 ) );
}
```

### Blank Lines

- **Required** between methods, constructors, class/enum/interface definitions, and static initializers.
- **Optional** between fields (consecutive fields may have no blank line).
- Use `// ── Section Name ──...` comments to group related methods within a class.

```java
private static final int DEFAULT_TIMEOUT_SECONDS = 10;
private static final String SCREENSHOT_DIR = "target/screenshots";

// ── driver helpers ──────────────────────────────────────────────

private WebDriver driver() {
  // ...
}

private WebDriverWait createWait() {
  // ...
}

// ── Navigation ────────────────────────────────────────────────

/** Opens the given URL. */
@Step("Navigate to '{0}'")
public void navigateTo(String url) {
  driver().get(url);
}
```

### Annotations

- **Class/method annotations:** each on its own line, before the declaration.
- **Variable annotations:** multiple allowed on the same line.

```java
@SuppressWarnings("PMD.GodClass")
public class WebKeywords {

  /** Opens the given URL. */
  @Step("Navigate to '{0}'")
  public void navigateTo(String url) {
    driver().get(url);
  }
}
```

### Method Naming — `MethodName` rule

Pattern: `^[a-z][a-z0-9]\w*$`

The **second character must also be lowercase or digit**.
This means `iNavigateTo()` is INVALID — `i` followed by uppercase `N` fails.

| Invalid              | Valid               |
|----------------------|---------------------|
| `iNavigateToPage()`  | `navigateToPage()`  |
| `iEnterUsername()`   | `enterUsername()`   |
| `iShouldSeeX()`     | `verifySomething()` |

> Cucumber step definitions: the method name is irrelevant to Cucumber — only the
> annotation regex matters. Rename freely without breaking scenarios.

### Method Name Conflicts

Do NOT name methods `wait()` — it conflicts with `java.lang.Object.wait()` which is `final`.
Use `createWait()`, `buildWait()`, or similar.

### Other Naming Rules

- **Constants:** `UPPER_SNAKE_CASE` for `static final` fields.
- **Members/parameters/locals:** `lowerCamelCase`, second char must be lowercase or digit.
- **Types:** `UpperCamelCase`. No abbreviations longer than 1 char (e.g., `Url` not `URL` in names).
- **Packages:** all lowercase, no underscores.
- **Array type style:** brackets on the type: `int[]` not `int []`.

### Miscellaneous Rules

- **One statement per line** — no `a = 1; b = 2;`
- **One top-level class per file**
- **No finalizer methods**
- **Switch must have `default` case**
- **Fall-through in switch requires comment**
- **Long literals use uppercase `L`:** `100L` not `100l`
- **Modifier order:** `public static final` (standard Java order)
- **Empty blocks** must contain a comment or text (except empty constructors/lambdas/methods)

---

## XML and Properties Files

### XML Files (`*.xml`)

- **Indentation:** 2 spaces (no tabs), same as Java.
- **Encoding declaration:** `<?xml version="1.0" encoding="UTF-8"?>` on the first line.
- **Attributes:** align continuation attributes with the first attribute when wrapping.
- **Self-closing tags:** use `<tag/>` (no space before `/>`) for empty elements.
- **Blank lines:** one blank line between logical sections.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="...">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mrquanga3</groupId>
    <artifactId>DemSerenityWithAI</artifactId>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
    </properties>
</project>
```

Applies to: `pom.xml`, `checkstyle.xml`, Cucumber config, and all other XML files.

### Properties Files (`*.properties`)

- **Key-value separator:** space around equals sign: `key = value`
- **Comments:** use `#` with a space: `# Comment text`
- **Grouping:** blank line between logical sections, with a `#` comment header.
- **Locator format:** `element.name = type:value` (see [keyword-driven-testing.md](../skills/keyword-driven-testing.md))

```properties
# Login page locators
# Format: type:value  (supported types: id, css, xpath, name)
username.input = id:input-username
password.input = id:input-password
login.button = css:button[type='submit']
# Dashboard element visible after successful login
dashboard.element = id:header
# Error message shown on failed login
error.message = css:.alert-danger
```

### Markdown Files (`*.md`)

- **Headings:** ATX style (`#`, `##`, `###`). One blank line before and after each heading.
- **Heading hierarchy:** start with `# Title` (H1), use `##` for sections, `###` for subsections. Do not skip levels.
- **Blank lines:** one blank line between paragraphs, before/after code blocks, before/after lists, and before/after
  horizontal rules (`---`).
- **Lists:** use `-` for unordered lists, `1.` for ordered lists. Indent nested lists by 4 spaces.
- **Bold:** `**text**` for emphasis on key terms (e.g., `**Indentation:**`).
- **Inline code:** backticks for file names, class names, commands, keys, and values (e.g., `` `pom.xml` ``,
  `` `WebKeywords` ``).
- **Code blocks:** fenced with triple backticks and a language tag (````java`, ````bash`, ````xml`,
  ````properties`, ````sql`).
- **Tables:** pipe-delimited with aligned `|` columns. Pad cells with spaces so `|` characters line up vertically.
- **Links:** prefer relative paths for in-repo docs (e.g., `[code-style.md](../rules/code-style.md)`).
- **Horizontal rules:** `---` on its own line, with blank lines above and below, to separate major sections.
- **Line length:** no hard limit, but keep lines readable. Wrap at sentence boundaries where practical.
- **Trailing whitespace:** none.

```markdown
# Page Title

Short description of the page.

## Section

- **Key point:** explanation with `inline code`
- Another bullet

### Subsection

| Column A | Column B |
|----------|----------|
| value    | value    |

---

## Another Section

1. First step
2. Second step
    - Nested detail
    - Another detail
```

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
