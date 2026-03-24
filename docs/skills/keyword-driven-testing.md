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
Step Definitions (LoginSteps.java)
    ↓  loads locators from properties file
    ↓  calls generic keywords
Keyword Library (WebKeywords.java)
    ↓  resolves By from locator string
    ↓  interacts with WebDriver
Browser (Chrome)

Locators (login.properties)
    ↑  referenced by step definitions
```

---

## Locator Properties Format

File location: `src/test/resources/<page>/<page>.properties`

Format: `key=type:value`

| Prefix | Resolves to |
|---|---|
| `id:` | `By.id(value)` |
| `css:` | `By.cssSelector(value)` |
| `xpath:` | `By.xpath(value)` |
| `name:` | `By.name(value)` |

Example (`login/login.properties`):
```properties
username.input=id:input-username
password.input=id:input-password
login.button=css:button[type='submit']
dashboard.element=id:header
error.message=css:.alert-danger
```

---

## Available Keywords (WebKeywords)

| Method | Description |
|---|---|
| `navigateTo(url)` | Open a URL in the browser |
| `inputText(locator, text)` | Clear field and type text |
| `clickElement(locator)` | Click an element |
| `verifyElementVisible(locator)` | Assert element is displayed |
| `verifyUrlContains(fragment)` | Assert current URL contains string |

All keyword methods use explicit 10-second waits (`WebDriverWait`) before interacting.

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
