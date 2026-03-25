# Keyword-Driven Testing Pattern

## Concept

Keywords are named, reusable browser actions (like Robot Framework keywords).
Step definitions call keywords rather than interacting with the driver directly.
Locators are externalized to properties files — test code never hardcodes selectors.

---

## Architecture in This Project

```
Feature file (.feature)
    ↓  Gherkin steps matched by annotations
Common Step Definitions (CommonSteps.java — in common-module)
    ↓  generic steps: navigate, click, enter text, verify visible, verify URL
    ↓  actor management: open browser, switch actor
    ↓  resolves locator keys and URL keys from properties via PropertiesLoader
Keyword Library (WebKeywords.java — in common-module)
    ↓  resolves By from locator string
    ↓  uses ActorManager driver (multi-actor) or Serenity driver (single-actor)
Browser(s) (Chrome / Firefox — one per actor, or single Serenity-managed)

Properties files (auto-loaded by PropertiesLoader)
    ├── environment.properties  ← URLs, credentials (key=value)
    └── login/login.properties  ← locators (key=type:value)
```

---

## Properties Files

All `.properties` files under `src/test/resources/properties/` are auto-loaded by `PropertiesLoader`.

### Environment Properties (`environment.properties`)

Stores URLs and account credentials as plain `key=value` pairs:

```properties
urlAdmin = http://103.245.237.118:8081/opencart/administrator/
adminUsername = admin
adminPassword = admin
```

### Locator Properties (`<page>/<page>.properties`)

Format: `key=type:value`

| Prefix   | Resolves to             |
|----------|-------------------------|
| `id:`    | `By.id(value)`          |
| `css:`   | `By.cssSelector(value)` |
| `xpath:` | `By.xpath(value)`       |
| `name:`  | `By.name(value)`        |

Example (`login/login.properties`):

```properties
username.input = id:input-username
password.input = id:input-password
login.button = css:button[type='submit']
dashboard.element = id:header
error.message = css:.alert-danger
```

---

## Available Keywords (WebKeywords)

| Method                          | Description                        |
|---------------------------------|------------------------------------|
| `navigateTo(url)`               | Open a URL in the browser          |
| `inputText(locator, text)`      | Clear field and type text          |
| `clickElement(locator)`         | Click an element                   |
| `verifyElementVisible(locator)` | Assert element is displayed        |
| `verifyUrlContains(fragment)`   | Assert current URL contains string |

All keyword methods use explicit 10-second waits (`WebDriverWait`) before interacting.

---

## Common Step Definitions (CommonSteps)

Located in `common-module/src/main/java/com/mrquanga3/steps/CommonSteps.java`.
These are generic Cucumber steps that any feature module can reuse without duplication.

| Cucumber Expression                           | What It Does                              |
|-----------------------------------------------|-------------------------------------------|
| Cucumber Expression                           | What It Does                                     |
|-----------------------------------------------|--------------------------------------------------|
| `{string} opens a {string} browser`           | Opens a named browser session for an actor        |
| `switching to {string}`                        | Switches subsequent steps to the named actor      |
| `I navigate to {string}`                      | Opens the given URL directly                      |
| `I navigate to the {string} page`             | Resolves URL key from properties, then navigates  |
| `I enter {string} to {string} field`          | Types text into field by locator key              |
| `I click {string}`                            | Clicks element by locator key                     |
| `I should see {string}`                       | Asserts element visible by locator key            |
| `the URL should contain {string}`             | Asserts current URL contains fragment             |

An `@After` hook automatically closes all actor browsers after each scenario.

Domain-specific steps can be added in `web-module/src/test/java/com/mrquanga3/steps/` when generic ones don't cover the scenario.

---

## Multi-Actor Support

Use `ActorManager` (in `common-module/src/main/java/com/mrquanga3/utils/`) to run
multiple browser sessions in a single scenario.

### How It Works

1. `"{name}" opens a "{browser}" browser` — creates a named WebDriver (chrome/firefox)
2. `switching to "{name}"` — all subsequent steps run in that actor's browser
3. `WebKeywords.driver()` returns the active actor's driver when actors are present,
   otherwise falls back to the default Serenity-managed driver
4. `@After` hook calls `ActorManager.closeAll()` to quit all browsers

### Example

```gherkin
Scenario: Two actors login simultaneously
  Given "Alice" opens a "chrome" browser
  And "Bob" opens a "firefox" browser

  When switching to "Alice"
  And I navigate to the "urlAdmin" page
  And I enter "admin" to "username.input" field
  And I click "login.button"

  When switching to "Bob"
  And I navigate to the "urlAdmin" page
  And I enter "admin" to "username.input" field
  And I click "login.button"
```

### Backward Compatibility

Scenarios without actor steps continue to work as before — `WebKeywords` uses
the Serenity-managed driver when no actor is active.

---

## Adding a New Keyword

1. Add a method to `WebKeywords.java` in `common-module/src/main/java/`
2. Annotate with `@Step("Description with '{0}' placeholder for args")`
3. Use `createWait().until(...)` for reliability
4. Use `driver()` helper to access the managed WebDriver

```java

@Step("Select option '{1}' from dropdown '{0}'")
public void selectOption(String locator, String optionText) {
  WebElement dropdown = createWait().until(
      ExpectedConditions.visibilityOfElementLocated(parseLocator(locator)));
  new Select(dropdown).selectByVisibleText(optionText);
}
```

---

## Why No Page Object?

This project deliberately uses keyword-driven style (not Page Object):

- Locators live in `.properties` files, not Java classes
- `WebKeywords` is stateless and generic across all pages
- Step definitions own the page-specific knowledge (which properties file, which keys)
- Easier for non-Java team members to update locators
