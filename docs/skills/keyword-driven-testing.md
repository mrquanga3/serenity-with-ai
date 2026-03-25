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
    ↓  ~75 generic steps modelled after RF SeleniumLibrary
    ↓  actor management, save-to-variable via Common.globalVariables
    ↓  resolves locator keys and URL keys via PropertiesLoader
Keyword Library (WebKeywords.java — in common-module)
    ↓  ~75 keyword methods with @Step annotations
    ↓  uses ActorManager driver (multi-actor) or Serenity driver (single-actor)
Browser(s) (Chrome / Firefox — one per actor, or single Serenity-managed)

Properties files (auto-loaded by PropertiesLoader)
    ├── environment.properties  ← URLs, credentials (key=value)
    └── login/login.properties  ← locators (key=type:value)
```

---

## Properties Files

`PropertiesLoader` scans `src/test/resources/properties/` and merges all
`.properties` files. **Environment-specific** files (`environment.properties`)
are loaded only from the active environment folder.

### Environment Resolution

The active environment is resolved in order:

1. System property: `mvn test -Denv=UAT`
2. `env` key in `serenity.conf` (default: `SIT`)

### Environment Properties (`{env}/environment.properties`)

Each environment folder holds URLs and credentials for that environment:

```
properties/
  SIT/environment.properties   ← loaded when env=SIT (default)
  UAT/environment.properties   ← loaded when env=UAT
```

Example (`SIT/environment.properties`):

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

Modelled after [Robot Framework SeleniumLibrary](https://robotframework.org/SeleniumLibrary/SeleniumLibrary.html).
All keyword methods use 10-second `WebDriverWait` before interacting.

### Navigation

| Method | Description |
|---|---|
| `navigateTo(url)` | Open a URL |
| `goBack()` | Browser back button |
| `reloadPage()` | Refresh the page |
| `getLocation()` | Returns current URL |

### Window Management

| Method | Description |
|---|---|
| `maximizeWindow()` | Maximize window |
| `minimizeWindow()` | Minimize window |
| `setWindowSize(w, h)` | Set window dimensions |
| `setWindowPosition(x, y)` | Set window position |
| `getWindowSize()` | Returns `widthxheight` |
| `getWindowPosition()` | Returns `x,y` |
| `switchWindow(nameOrHandle)` | Switch to window |
| `closeWindow()` | Close current window |
| `getWindowHandles()` | Returns comma-separated handles |
| `getWindowTitles()` | Returns comma-separated titles |

### Frame Management

| Method | Description |
|---|---|
| `selectFrame(locator)` | Switch to frame |
| `unselectFrame()` | Switch to default content |

### Click / Mouse Actions

| Method | Description |
|---|---|
| `clickElement(locator)` | Click element |
| `doubleClickElement(locator)` | Double-click element |
| `clickElementAtCoordinates(loc, x, y)` | Click at offset |
| `contextClickElement(locator)` | Right-click |
| `mouseOver(locator)` | Hover over element |
| `mouseOut(locator)` | Move mouse away |
| `dragAndDrop(source, target)` | Drag and drop |
| `dragAndDropByOffset(loc, x, y)` | Drag by pixel offset |

### Keyboard / Input

| Method | Description |
|---|---|
| `inputText(locator, text)` | Clear and type text |
| `clearElementText(locator)` | Clear field |
| `pressKey(locator, keyName)` | Press key (ENTER, TAB…) |

### Element Getters (return String)

| Method | Description |
|---|---|
| `getText(locator)` | Visible text |
| `getValue(locator)` | Value attribute |
| `getElementAttribute(loc, attr)` | Any attribute |
| `getElementCount(locator)` | Number of matches |

### Element Assertions

| Method | Description |
|---|---|
| `verifyElementVisible(locator)` | Is visible |
| `verifyElementNotVisible(locator)` | Is NOT visible |
| `verifyElementEnabled(locator)` | Is enabled |
| `verifyElementDisabled(locator)` | Is disabled |
| `verifyElementFocused(locator)` | Is focused |
| `verifyElementContains(loc, text)` | Contains text |
| `verifyElementNotContains(loc, text)` | Not contains text |
| `verifyElementTextIs(loc, text)` | Exact text match |
| `verifyElementAttributeIs(loc, attr, val)` | Attribute equals |

### Page / URL / Title Assertions

| Method | Description |
|---|---|
| `pageShouldContainText(text)` | Page has text |
| `pageShouldNotContainText(text)` | Page lacks text |
| `pageShouldContainElement(locator)` | Element exists |
| `pageShouldNotContainElement(locator)` | Element absent |
| `verifyUrlContains(fragment)` | URL contains |
| `verifyLocationIs(url)` | URL equals |
| `getTitle()` | Returns page title |
| `verifyTitleIs(title)` | Title equals |
| `getPageSource()` | Returns HTML source |

### Checkbox / Radio / Dropdown

| Method | Description |
|---|---|
| `selectCheckbox(locator)` | Check |
| `unselectCheckbox(locator)` | Uncheck |
| `verifyCheckboxSelected(locator)` | Assert checked |
| `verifyCheckboxNotSelected(locator)` | Assert unchecked |
| `selectRadioButton(locator)` | Select radio |
| `verifyRadioButtonSelected(locator)` | Assert selected |
| `verifyRadioButtonNotSelected(locator)` | Assert not selected |
| `selectFromListByLabel(loc, label)` | Select by text |
| `selectFromListByValue(loc, value)` | Select by value |
| `selectFromListByIndex(loc, index)` | Select by index |
| `unselectAllFromList(locator)` | Deselect all |
| `getSelectedListLabel(locator)` | Selected label |
| `getSelectedListValue(locator)` | Selected value |

### Table

| Method | Description |
|---|---|
| `getTableCell(loc, row, col)` | Cell text (1-based) |
| `verifyTableContains(loc, text)` | Table has text |
| `verifyTableCellContains(loc, r, c, text)` | Cell has text |

### Form / Alert / Cookie / JS / Screenshot

| Method | Description |
|---|---|
| `submitForm(locator)` | Submit form |
| `chooseFile(locator, path)` | File upload |
| `acceptAlert()` | Accept, return message |
| `dismissAlert()` | Dismiss, return message |
| `verifyAlertPresent()` | Alert exists |
| `verifyAlertNotPresent()` | No alert |
| `inputTextIntoAlert(text)` | Type into prompt |
| `addCookie(name, value)` | Add cookie |
| `deleteCookie(name)` | Delete cookie |
| `deleteAllCookies()` | Delete all |
| `getCookieValue(name)` | Cookie value |
| `executeJavascript(code)` | Run JS, return result |
| `capturePageScreenshot()` | Full-page screenshot |
| `captureElementScreenshot(loc)` | Element screenshot |

### Wait Keywords

| Method | Description |
|---|---|
| `waitUntilElementVisible(locator)` | Wait visible |
| `waitUntilElementNotVisible(locator)` | Wait invisible |
| `waitUntilElementEnabled(locator)` | Wait clickable |
| `waitUntilElementContains(loc, text)` | Wait text in element |
| `waitUntilPageContainsText(text)` | Wait text in page |
| `waitUntilPageNotContainsText(text)` | Wait text gone |
| `waitUntilPageContainsElement(locator)` | Wait element in DOM |
| `waitUntilPageNotContainsElement(loc)` | Wait element gone |
| `waitUntilLocationIs(url)` | Wait URL equals |
| `waitUntilLocationContains(fragment)` | Wait URL contains |

### Element Utilities

| Method | Description |
|---|---|
| `scrollElementIntoView(locator)` | Scroll into view |
| `setFocusToElement(locator)` | Set focus |

---

## Global Variables (Save-to-Variable Pattern)

`Common.java` (`com.mrquanga3.common`) provides a static
`Map<String, String>` for storing values captured by "Get" keywords.

### How It Works

1. A "Get … then save to" Cucumber step calls the keyword getter
2. The returned value is stored via `Common.saveVariable(key, value)`
3. Later steps can retrieve it via `Common.getVariable(key)`
4. Variables are cleared automatically after each scenario (`@After` hook)

### Example

```gherkin
When I get text of "welcome.label" then save to "welcomeText"
And I get title then save to "pageTitle"
And I execute javascript "return document.readyState" then save to "state"
```

---

## Common Step Definitions (CommonSteps)

Located in `common-module/src/main/java/com/mrquanga3/steps/CommonSteps.java`.
~75 generic Cucumber steps. Full reference by category:

### Actor Management

| Cucumber Expression | What It Does |
|---|---|
| `{string} opens a {string} browser` | Open named browser session |
| `switching to {string}` | Switch to actor's browser |

### Navigation

| Cucumber Expression | What It Does |
|---|---|
| `I navigate to {string}` | Open URL directly |
| `I navigate to the {string} page` | Resolve URL key from properties |
| `I go back` | Browser back |
| `I reload page` | Refresh |
| `I get location then save to {string}` | Save current URL |

### Window / Frame

| Cucumber Expression | What It Does |
|---|---|
| `I maximize browser window` | Maximize |
| `I minimize browser window` | Minimize |
| `I set window size to {int} x {int}` | Set size |
| `I set window position to {int} and {int}` | Set position |
| `I get window size then save to {string}` | Save size |
| `I get window position then save to {string}` | Save position |
| `I switch window {string}` | Switch by name/handle |
| `I close window` | Close tab |
| `I get window handles then save to {string}` | Save handles |
| `I get window titles then save to {string}` | Save titles |
| `I select frame {string}` | Enter frame |
| `I unselect frame` | Exit frame |

### Click / Mouse

| Cucumber Expression | What It Does |
|---|---|
| `I click {string}` | Click by locator key |
| `I double click {string}` | Double-click |
| `I click {string} at coordinates {int} and {int}` | Click at offset |
| `I right click {string}` | Context menu |
| `I mouse over {string}` | Hover |
| `I mouse out from {string}` | Move away |
| `I drag {string} and drop to {string}` | Drag & drop |
| `I drag {string} and drop by offset {int} and {int}` | Drag by pixels |

### Input

| Cucumber Expression | What It Does |
|---|---|
| `I enter {string} to {string} field` | Type text |
| `I clear text of {string}` | Clear field |
| `I press key {string} on {string}` | Press key |

### Getters (save to variable)

| Cucumber Expression | What It Does |
|---|---|
| `I get text of {string} then save to {string}` | Save text |
| `I get value of {string} then save to {string}` | Save value attr |
| `I get attribute {string} of {string} then save to {string}` | Save attribute |
| `I get element count of {string} then save to {string}` | Save count |
| `I get title then save to {string}` | Save page title |
| `I get page source then save to {string}` | Save HTML |
| `I get selected label of {string} then save to {string}` | Save dropdown label |
| `I get selected value of {string} then save to {string}` | Save dropdown value |
| `I get cell at row {int} column {int} of table {string} then save to {string}` | Save cell text |
| `I get cookie {string} then save to {string}` | Save cookie value |
| `I execute javascript {string} then save to {string}` | Save JS result |
| `I accept alert then save to {string}` | Save alert message |
| `I dismiss alert then save to {string}` | Save alert message |

### Assertions

| Cucumber Expression | What It Does |
|---|---|
| `I should see {string}` | Element visible |
| `element {string} should not be visible` | Element hidden |
| `element {string} should be enabled` | Enabled |
| `element {string} should be disabled` | Disabled |
| `element {string} should be focused` | Focused |
| `element {string} should contain text {string}` | Contains text |
| `element {string} should not contain text {string}` | Not contains |
| `element {string} text should be {string}` | Exact text |
| `element {string} attribute {string} should be {string}` | Attribute value |
| `page should contain text {string}` | Page has text |
| `page should not contain text {string}` | Page lacks text |
| `page should contain element {string}` | Element exists |
| `page should not contain element {string}` | Element absent |
| `the URL should contain {string}` | URL contains |
| `the URL should be {string}` | URL equals |
| `the title should be {string}` | Title equals |
| `checkbox {string} should be selected` | Checked |
| `checkbox {string} should not be selected` | Unchecked |
| `radio button {string} should be selected` | Selected |
| `radio button {string} should not be selected` | Not selected |
| `table {string} should contain {string}` | Table has text |
| `cell at row {int} column {int} of table {string} should contain {string}` | Cell has text |
| `alert should be present` | Alert exists |
| `alert should not be present` | No alert |

### Checkbox / Radio / Dropdown

| Cucumber Expression | What It Does |
|---|---|
| `I select checkbox {string}` | Check |
| `I unselect checkbox {string}` | Uncheck |
| `I select radio button {string}` | Select radio |
| `I select {string} from {string} dropdown` | By label |
| `I select value {string} from {string} dropdown` | By value |
| `I select index {int} from {string} dropdown` | By index |
| `I unselect all from {string} dropdown` | Deselect all |

### Form / Alert / Cookie / JS / Screenshot

| Cucumber Expression | What It Does |
|---|---|
| `I submit form {string}` | Submit |
| `I choose file {string} for {string}` | File upload |
| `I accept alert` | Accept alert |
| `I dismiss alert` | Dismiss alert |
| `I input {string} into alert and accept` | Type + accept |
| `I add cookie {string} with value {string}` | Add cookie |
| `I delete cookie {string}` | Delete cookie |
| `I delete all cookies` | Delete all |
| `I execute javascript {string}` | Run JS |
| `I capture page screenshot` | Full-page capture |
| `I capture screenshot of element {string}` | Element capture |

### Wait Keywords

| Cucumber Expression | What It Does |
|---|---|
| `I wait until element {string} is visible` | Wait visible |
| `I wait until element {string} is not visible` | Wait hidden |
| `I wait until element {string} is enabled` | Wait clickable |
| `I wait until element {string} contains text {string}` | Wait text |
| `I wait until page contains text {string}` | Wait page text |
| `I wait until page does not contain text {string}` | Wait text gone |
| `I wait until page contains element {string}` | Wait element |
| `I wait until page does not contain element {string}` | Wait element gone |
| `I wait until location is {string}` | Wait URL equals |
| `I wait until location contains {string}` | Wait URL contains |

### Element Utilities

| Cucumber Expression | What It Does |
|---|---|
| `I scroll to element {string}` | Scroll into view |
| `I set focus to element {string}` | Set focus |

An `@After` hook automatically closes all actor browsers and clears global variables after each scenario.

Domain-specific steps can be added in `web-module/src/test/java/com/mrquanga3/steps/`.

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
