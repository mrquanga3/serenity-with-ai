# Serenity BDD Rules

## Version: 4.1.20

---

## Annotation Packages

Always import from `net.serenitybdd.annotations` (in `serenity-model` jar):

```java
import net.serenitybdd.annotations.Step;   // for keyword/step library methods
import net.serenitybdd.annotations.Steps;  // for injecting step libraries into step defs
```

Do NOT use `net.thucydides.model.annotations` or `net.thucydides.core.annotations` —
they either don't exist in 4.x or don't contain `Step`/`Steps`.

---

## WebDriver Access in Keywords

Use `Serenity.getWebdriverManager().getWebdriver()` to get the current managed driver:

```java
import net.serenitybdd.core.Serenity;

private WebDriver driver() {
  return Serenity.getWebdriverManager().getWebdriver();
}
```

- The driver is lazy-initialized on first call based on `serenity.conf`
- Lifecycle (open/close) is managed automatically by `CucumberWithSerenity`
- Do NOT create `new ChromeDriver()` manually

---

## Step Library Pattern

Keyword class: plain POJO with `@Step` annotations.
Step def class: injects keyword class via `@Steps`.

```java
// WebKeywords.java (in common-module src/main/java)
public class WebKeywords {
  @Step("Navigate to '{0}'")
  public void navigateTo(String url) { ... }
}

// LoginSteps.java (in web-module src/test/java)
public class LoginSteps {
  @Steps
  WebKeywords keywords;   // Serenity proxies this for reporting
}
```

When `@Steps` is used, Serenity wraps the field in a proxy that:
- Records each `@Step` call in the HTML report
- Takes screenshots at each step (controlled by `serenity.conf`)

---

## serenity.conf (HOCON format)

Minimal working config:
```hocon
webdriver {
  driver = chrome    # chrome | firefox | edge
}

serenity {
  project.name = "YourProjectName"
  take.screenshots = AFTER_EACH_STEP  # BEFORE_AND_AFTER_EACH_STEP | FOR_FAILURES
}
```

Place in: `src/test/resources/serenity.conf`

---

## Runner Class

```java
@RunWith(CucumberWithSerenity.class)   // NOT @RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.mrquanga3.steps",
    plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"}
)
public class CucumberTestRunner {}
```

The runner class name does NOT match default Surefire patterns.
Must explicitly include it in surefire config:
```xml
<configuration>
  <includes>
    <include>**/CucumberTestRunner.java</include>
  </includes>
</configuration>
```

---

## Serenity Maven Plugin

Reports are generated at `post-integration-test` phase (runs with `mvn verify`, not `mvn test`):

```bash
mvn clean verify           # build + test + generate report
mvn serenity:aggregate     # regenerate report from existing test data
```

Report location: `web-module/target/site/serenity/index.html`
