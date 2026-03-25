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

**Single-actor mode** (default): uses `Serenity.getWebdriverManager().getWebdriver()`,
lazy-initialized from `serenity.conf`, lifecycle managed by `CucumberWithSerenity`.

**Multi-actor mode**: when actors are opened via `ActorManager`, `WebKeywords.driver()`
returns the active actor's driver instead. Each actor creates its own `ChromeDriver`
or `FirefoxDriver`. Cleanup is handled by an `@After` hook.

```java
private WebDriver driver() {
  if (ActorManager.hasActiveActor()) {
    return ActorManager.currentDriver();
  }
  return Serenity.getWebdriverManager().getWebdriver();
}
```

---

## Step Library Pattern

Keyword class: plain POJO with `@Step` annotations.
Step def classes: inject keyword class via `@Steps`.

**CommonSteps** (in `common-module/src/main/java`) holds all generic, reusable step definitions.
Domain-specific steps can be added in `web-module/src/test/java` when needed.

```java
// WebKeywords.java (in common-module src/main/java)
public class WebKeywords {
  @Step("Navigate to '{0}'")
  public void navigateTo(String url) { ... }
}

// CommonSteps.java (in common-module src/main/java)
public class CommonSteps {
  @Steps
  WebKeywords keywords;
  @Given("I navigate to the {string} page")
  public void navigateToPage(String urlKey) { ... }
  @And("I click {string}")
  public void clickElement(String locatorKey) { ... }
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
public class CucumberTestRunner {
}
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
