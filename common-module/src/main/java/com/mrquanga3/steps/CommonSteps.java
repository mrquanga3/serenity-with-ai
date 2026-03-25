package com.mrquanga3.steps;

import com.mrquanga3.common.Common;
import com.mrquanga3.keywords.WebKeywords;
import com.mrquanga3.utils.ActorManager;
import com.mrquanga3.utils.PropertiesLoader;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

/**
 * Generic Cucumber step definitions modelled after Robot Framework
 * SeleniumLibrary keywords.
 *
 * <p>Locator parameters are resolved from {@link PropertiesLoader}.
 * "Get … then save to" steps store results in
 * {@link Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class CommonSteps {

  @Steps
  WebKeywords keywords;

  // ── Lifecycle hooks ───────────────────────────────────────────

  /** Cleans up after each scenario. */
  @After
  public void cleanUpAfterScenario() {
    ActorManager.closeAll();
    Common.clearAllVariables();
  }

  // ── Actor management ──────────────────────────────────────────

  /** Opens a new browser session for the named actor. */
  @Given("{string} opens a {string} browser")
  public void actorOpensBrowser(
      String actorName, String browserType) {
    ActorManager.openBrowser(actorName, browserType);
  }

  /** Switches subsequent steps to the named actor's browser. */
  @When("switching to {string}")
  public void switchingToActor(String actorName) {
    ActorManager.switchTo(actorName);
  }

  // ── Navigation ────────────────────────────────────────────────

  /** Opens the given URL directly. */
  @Given("I navigate to {string}")
  public void navigateToUrl(String url) {
    keywords.navigateTo(url);
  }

  /** Resolves a URL key from properties and navigates. */
  @Given("I navigate to the {string} page")
  public void navigateToPage(String urlKey) {
    keywords.navigateTo(PropertiesLoader.get(urlKey));
  }

  /** Goes back to the previous page. */
  @When("I go back")
  public void goBack() {
    keywords.goBack();
  }

  /** Reloads the current page. */
  @When("I reload page")
  public void reloadPage() {
    keywords.reloadPage();
  }

  /** Saves the current URL to a global variable. */
  @When("I get location then save to {string}")
  public void getLocationSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.getLocation());
  }

  // ── Window management ─────────────────────────────────────────

  /** Maximizes the current browser window. */
  @When("I maximize browser window")
  public void maximizeWindow() {
    keywords.maximizeWindow();
  }

  /** Minimizes the current browser window. */
  @When("I minimize browser window")
  public void minimizeWindow() {
    keywords.minimizeWindow();
  }

  /** Sets the window size in pixels. */
  @When("I set window size to {int} x {int}")
  public void setWindowSize(int width, int height) {
    keywords.setWindowSize(width, height);
  }

  /** Sets the window position. */
  @When("I set window position to {int} and {int}")
  public void setWindowPosition(int xcoord, int ycoord) {
    keywords.setWindowPosition(xcoord, ycoord);
  }

  /** Saves window size to a global variable. */
  @When("I get window size then save to {string}")
  public void getWindowSizeSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.getWindowSize());
  }

  /** Saves window position to a global variable. */
  @When("I get window position then save to {string}")
  public void getWindowPositionSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, keywords.getWindowPosition());
  }

  /** Switches to a window by name or handle. */
  @When("I switch window {string}")
  public void switchWindow(String nameOrHandle) {
    keywords.switchWindow(nameOrHandle);
  }

  /** Closes the currently focused window/tab. */
  @When("I close window")
  public void closeWindow() {
    keywords.closeWindow();
  }

  /** Saves all window handles to a global variable. */
  @When("I get window handles then save to {string}")
  public void getWindowHandlesSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, keywords.getWindowHandles());
  }

  /** Saves all window titles to a global variable. */
  @When("I get window titles then save to {string}")
  public void getWindowTitlesSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, keywords.getWindowTitles());
  }

  // ── Frame management ──────────────────────────────────────────

  /** Switches to the frame identified by locator key. */
  @When("I select frame {string}")
  public void selectFrame(String locatorKey) {
    keywords.selectFrame(PropertiesLoader.get(locatorKey));
  }

  /** Switches back to the main/default content. */
  @When("I unselect frame")
  public void unselectFrame() {
    keywords.unselectFrame();
  }

  // ── Click actions ─────────────────────────────────────────────

  /** Clicks the element identified by locator key. */
  @And("I click {string}")
  public void clickElement(String locatorKey) {
    keywords.clickElement(PropertiesLoader.get(locatorKey));
  }

  /** Double-clicks the element. */
  @When("I double click {string}")
  public void doubleClickElement(String locatorKey) {
    keywords.doubleClickElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Clicks element at pixel offset from its top-left. */
  @When("I click {string} at coordinates {int} and {int}")
  public void clickAtCoordinates(
      String locatorKey, int xoffset, int yoffset) {
    keywords.clickElementAtCoordinates(
        PropertiesLoader.get(locatorKey), xoffset, yoffset);
  }

  /** Right-clicks the element (context menu). */
  @When("I right click {string}")
  public void contextClick(String locatorKey) {
    keywords.contextClickElement(
        PropertiesLoader.get(locatorKey));
  }

  // ── Mouse actions ─────────────────────────────────────────────

  /** Hovers the mouse over the element. */
  @When("I mouse over {string}")
  public void mouseOver(String locatorKey) {
    keywords.mouseOver(PropertiesLoader.get(locatorKey));
  }

  /** Moves the mouse away from the element. */
  @When("I mouse out from {string}")
  public void mouseOut(String locatorKey) {
    keywords.mouseOut(PropertiesLoader.get(locatorKey));
  }

  /** Drags source element onto target element. */
  @When("I drag {string} and drop to {string}")
  public void dragAndDrop(
      String sourceKey, String targetKey) {
    keywords.dragAndDrop(
        PropertiesLoader.get(sourceKey),
        PropertiesLoader.get(targetKey));
  }

  /** Drags element by pixel offset. */
  @When("I drag {string} and drop by offset {int} and {int}")
  public void dragByOffset(
      String locatorKey, int xoffset, int yoffset) {
    keywords.dragAndDropByOffset(
        PropertiesLoader.get(locatorKey), xoffset, yoffset);
  }

  // ── Keyboard / input ──────────────────────────────────────────

  /** Types text into the field identified by locator key. */
  @When("I enter {string} to {string} field")
  public void enterTextToField(String text, String locatorKey) {
    keywords.inputText(PropertiesLoader.get(locatorKey), text);
  }

  /** Clears the text-input element. */
  @When("I clear text of {string}")
  public void clearText(String locatorKey) {
    keywords.clearElementText(PropertiesLoader.get(locatorKey));
  }

  /** Presses a key on the element (e.g. ENTER, TAB). */
  @When("I press key {string} on {string}")
  public void pressKey(String keyName, String locatorKey) {
    keywords.pressKey(
        PropertiesLoader.get(locatorKey), keyName);
  }

  // ── Element getters (save to variable) ────────────────────────

  /** Gets visible text and saves to variable. */
  @When("I get text of {string} then save to {string}")
  public void getTextSaveTo(
      String locatorKey, String variableKey) {
    String val = keywords.getText(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableKey, val);
  }

  /** Gets value attribute and saves to variable. */
  @When("I get value of {string} then save to {string}")
  public void getValueSaveTo(
      String locatorKey, String variableKey) {
    String val = keywords.getValue(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableKey, val);
  }

  /** Gets an attribute and saves to variable. */
  @When("I get attribute {string} of {string} then save to {string}")
  public void getAttributeSaveTo(
      String attribute, String locatorKey, String variableKey) {
    String val = keywords.getElementAttribute(
        PropertiesLoader.get(locatorKey), attribute);
    Common.saveVariable(variableKey, val);
  }

  /** Gets element count and saves to variable. */
  @When("I get element count of {string} then save to {string}")
  public void getElementCountSaveTo(
      String locatorKey, String variableKey) {
    String val = keywords.getElementCount(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableKey, val);
  }

  // ── Element state assertions ──────────────────────────────────

  /** Asserts element is visible. */
  @Then("I should see {string}")
  public void verifyElementVisible(String locatorKey) {
    keywords.verifyElementVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts element is NOT visible. */
  @Then("element {string} should not be visible")
  public void verifyElementNotVisible(String locatorKey) {
    keywords.verifyElementNotVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts element is enabled. */
  @Then("element {string} should be enabled")
  public void verifyElementEnabled(String locatorKey) {
    keywords.verifyElementEnabled(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts element is disabled. */
  @Then("element {string} should be disabled")
  public void verifyElementDisabled(String locatorKey) {
    keywords.verifyElementDisabled(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts element is focused. */
  @Then("element {string} should be focused")
  public void verifyElementFocused(String locatorKey) {
    keywords.verifyElementFocused(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts element contains the expected text. */
  @Then("element {string} should contain text {string}")
  public void verifyElementContains(
      String locatorKey, String expected) {
    keywords.verifyElementContains(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts element does NOT contain the text. */
  @Then("element {string} should not contain text {string}")
  public void verifyElementNotContains(
      String locatorKey, String expected) {
    keywords.verifyElementNotContains(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts element text equals expected exactly. */
  @Then("element {string} text should be {string}")
  public void verifyElementTextIs(
      String locatorKey, String expected) {
    keywords.verifyElementTextIs(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts an element attribute equals expected. */
  @Then("element {string} attribute {string} should be {string}")
  public void verifyElementAttributeIs(
      String locatorKey, String attribute, String expected) {
    keywords.verifyElementAttributeIs(
        PropertiesLoader.get(locatorKey), attribute, expected);
  }

  // ── Page content assertions ───────────────────────────────────

  /** Asserts the page contains the given text. */
  @Then("page should contain text {string}")
  public void pageShouldContainText(String text) {
    keywords.pageShouldContainText(text);
  }

  /** Asserts the page does NOT contain the text. */
  @Then("page should not contain text {string}")
  public void pageShouldNotContainText(String text) {
    keywords.pageShouldNotContainText(text);
  }

  /** Asserts the page contains the element. */
  @Then("page should contain element {string}")
  public void pageShouldContainElement(String locatorKey) {
    keywords.pageShouldContainElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the page does NOT contain the element. */
  @Then("page should not contain element {string}")
  public void pageShouldNotContainElement(String locatorKey) {
    keywords.pageShouldNotContainElement(
        PropertiesLoader.get(locatorKey));
  }

  // ── URL / location assertions ─────────────────────────────────

  /** Asserts the current URL contains the fragment. */
  @Then("the URL should contain {string}")
  public void verifyUrlContains(String fragment) {
    keywords.verifyUrlContains(fragment);
  }

  /** Asserts the current URL equals expected exactly. */
  @Then("the URL should be {string}")
  public void verifyUrlIs(String expectedUrl) {
    keywords.verifyLocationIs(expectedUrl);
  }

  // ── Title ─────────────────────────────────────────────────────

  /** Saves the page title to a global variable. */
  @When("I get title then save to {string}")
  public void getTitleSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.getTitle());
  }

  /** Asserts the page title equals expected. */
  @Then("the title should be {string}")
  public void verifyTitleIs(String expected) {
    keywords.verifyTitleIs(expected);
  }

  // ── Page source ───────────────────────────────────────────────

  /** Saves the full page source to a global variable. */
  @When("I get page source then save to {string}")
  public void getPageSourceSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.getPageSource());
  }

  // ── Checkbox ──────────────────────────────────────────────────

  /** Selects (checks) the checkbox. */
  @When("I select checkbox {string}")
  public void selectCheckbox(String locatorKey) {
    keywords.selectCheckbox(PropertiesLoader.get(locatorKey));
  }

  /** Un-selects (unchecks) the checkbox. */
  @When("I unselect checkbox {string}")
  public void unselectCheckbox(String locatorKey) {
    keywords.unselectCheckbox(PropertiesLoader.get(locatorKey));
  }

  /** Asserts the checkbox is selected. */
  @Then("checkbox {string} should be selected")
  public void verifyCheckboxSelected(String locatorKey) {
    keywords.verifyCheckboxSelected(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the checkbox is NOT selected. */
  @Then("checkbox {string} should not be selected")
  public void verifyCheckboxNotSelected(String locatorKey) {
    keywords.verifyCheckboxNotSelected(
        PropertiesLoader.get(locatorKey));
  }

  // ── Radio button ──────────────────────────────────────────────

  /** Selects (clicks) the radio button. */
  @When("I select radio button {string}")
  public void selectRadioButton(String locatorKey) {
    keywords.selectRadioButton(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the radio button is selected. */
  @Then("radio button {string} should be selected")
  public void verifyRadioSelected(String locatorKey) {
    keywords.verifyRadioButtonSelected(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the radio button is NOT selected. */
  @Then("radio button {string} should not be selected")
  public void verifyRadioNotSelected(String locatorKey) {
    keywords.verifyRadioButtonNotSelected(
        PropertiesLoader.get(locatorKey));
  }

  // ── Select list / dropdown ────────────────────────────────────

  /** Selects option by visible text. */
  @When("I select {string} from {string} dropdown")
  public void selectByLabel(String label, String locatorKey) {
    keywords.selectFromListByLabel(
        PropertiesLoader.get(locatorKey), label);
  }

  /** Selects option by value attribute. */
  @When("I select value {string} from {string} dropdown")
  public void selectByValue(String value, String locatorKey) {
    keywords.selectFromListByValue(
        PropertiesLoader.get(locatorKey), value);
  }

  /** Selects option by zero-based index. */
  @When("I select index {int} from {string} dropdown")
  public void selectByIndex(int index, String locatorKey) {
    keywords.selectFromListByIndex(
        PropertiesLoader.get(locatorKey), index);
  }

  /** Deselects all options from a multi-select list. */
  @When("I unselect all from {string} dropdown")
  public void unselectAll(String locatorKey) {
    keywords.unselectAllFromList(
        PropertiesLoader.get(locatorKey));
  }

  /** Gets selected option label and saves to variable. */
  @When("I get selected label of {string} then save to {string}")
  public void getSelectedLabelSaveTo(
      String locatorKey, String variableKey) {
    String val = keywords.getSelectedListLabel(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableKey, val);
  }

  /** Gets selected option value and saves to variable. */
  @When("I get selected value of {string} then save to {string}")
  public void getSelectedValueSaveTo(
      String locatorKey, String variableKey) {
    String val = keywords.getSelectedListValue(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableKey, val);
  }

  // ── Table ─────────────────────────────────────────────────────

  /** Gets table cell text and saves to variable. */
  @When("I get cell at row {int} column {int} of table {string} then save to {string}")
  public void getTableCellSaveTo(
      int row, int column, String locatorKey,
      String variableKey) {
    String val = keywords.getTableCell(
        PropertiesLoader.get(locatorKey), row, column);
    Common.saveVariable(variableKey, val);
  }

  /** Asserts table contains the expected text. */
  @Then("table {string} should contain {string}")
  public void verifyTableContains(
      String locatorKey, String expected) {
    keywords.verifyTableContains(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts a specific table cell contains text. */
  @Then("cell at row {int} column {int} of table {string} should contain {string}")
  public void verifyTableCellContains(
      int row, int column, String locatorKey, String expected) {
    keywords.verifyTableCellContains(
        PropertiesLoader.get(locatorKey), row, column, expected);
  }

  // ── Form ──────────────────────────────────────────────────────

  /** Submits the form. */
  @When("I submit form {string}")
  public void submitForm(String locatorKey) {
    keywords.submitForm(PropertiesLoader.get(locatorKey));
  }

  /** Inputs a file path into a file upload field. */
  @When("I choose file {string} for {string}")
  public void chooseFile(String filePath, String locatorKey) {
    keywords.chooseFile(
        PropertiesLoader.get(locatorKey), filePath);
  }

  // ── Alert ─────────────────────────────────────────────────────

  /** Accepts the alert. */
  @When("I accept alert")
  public void acceptAlert() {
    keywords.acceptAlert();
  }

  /** Dismisses the alert. */
  @When("I dismiss alert")
  public void dismissAlert() {
    keywords.dismissAlert();
  }

  /** Accepts the alert and saves its message to variable. */
  @When("I accept alert then save to {string}")
  public void acceptAlertSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.acceptAlert());
  }

  /** Dismisses the alert and saves its message to variable. */
  @When("I dismiss alert then save to {string}")
  public void dismissAlertSaveTo(String variableKey) {
    Common.saveVariable(variableKey, keywords.dismissAlert());
  }

  /** Asserts an alert is present. */
  @Then("alert should be present")
  public void verifyAlertPresent() {
    keywords.verifyAlertPresent();
  }

  /** Asserts no alert is present. */
  @Then("alert should not be present")
  public void verifyAlertNotPresent() {
    keywords.verifyAlertNotPresent();
  }

  /** Types text into the alert prompt and accepts. */
  @When("I input {string} into alert and accept")
  public void inputIntoAlert(String text) {
    keywords.inputTextIntoAlert(text);
  }

  // ── Cookie ────────────────────────────────────────────────────

  /** Adds a cookie. */
  @When("I add cookie {string} with value {string}")
  public void addCookie(String name, String value) {
    keywords.addCookie(name, value);
  }

  /** Deletes the named cookie. */
  @When("I delete cookie {string}")
  public void deleteCookie(String name) {
    keywords.deleteCookie(name);
  }

  /** Deletes all cookies. */
  @When("I delete all cookies")
  public void deleteAllCookies() {
    keywords.deleteAllCookies();
  }

  /** Gets cookie value and saves to variable. */
  @When("I get cookie {string} then save to {string}")
  public void getCookieSaveTo(
      String name, String variableKey) {
    Common.saveVariable(
        variableKey, keywords.getCookieValue(name));
  }

  // ── JavaScript ────────────────────────────────────────────────

  /** Executes JavaScript. */
  @When("I execute javascript {string}")
  public void executeJs(String code) {
    keywords.executeJavascript(code);
  }

  /** Executes JavaScript and saves the result to variable. */
  @When("I execute javascript {string} then save to {string}")
  public void executeJsSaveTo(
      String code, String variableKey) {
    Common.saveVariable(
        variableKey, keywords.executeJavascript(code));
  }

  // ── Screenshot ────────────────────────────────────────────────

  /** Captures a full-page screenshot. */
  @When("I capture page screenshot")
  public void capturePageScreenshot() {
    keywords.capturePageScreenshot();
  }

  /** Captures a screenshot of a specific element. */
  @When("I capture screenshot of element {string}")
  public void captureElementScreenshot(String locatorKey) {
    keywords.captureElementScreenshot(
        PropertiesLoader.get(locatorKey));
  }

  // ── Wait keywords ─────────────────────────────────────────────

  /** Waits until element is visible. */
  @When("I wait until element {string} is visible")
  public void waitVisible(String locatorKey) {
    keywords.waitUntilElementVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until element is NOT visible. */
  @When("I wait until element {string} is not visible")
  public void waitNotVisible(String locatorKey) {
    keywords.waitUntilElementNotVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until element is enabled/clickable. */
  @When("I wait until element {string} is enabled")
  public void waitEnabled(String locatorKey) {
    keywords.waitUntilElementEnabled(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until element contains the text. */
  @When("I wait until element {string} contains text {string}")
  public void waitContains(String locatorKey, String text) {
    keywords.waitUntilElementContains(
        PropertiesLoader.get(locatorKey), text);
  }

  /** Waits until page contains the text. */
  @When("I wait until page contains text {string}")
  public void waitPageContains(String text) {
    keywords.waitUntilPageContainsText(text);
  }

  /** Waits until page does NOT contain the text. */
  @When("I wait until page does not contain text {string}")
  public void waitPageNotContains(String text) {
    keywords.waitUntilPageNotContainsText(text);
  }

  /** Waits until page contains the element. */
  @When("I wait until page contains element {string}")
  public void waitPageContainsElement(String locatorKey) {
    keywords.waitUntilPageContainsElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until page does NOT contain the element. */
  @When("I wait until page does not contain element {string}")
  public void waitPageNotContainsElement(String locatorKey) {
    keywords.waitUntilPageNotContainsElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until the current URL equals expected. */
  @When("I wait until location is {string}")
  public void waitLocationIs(String expectedUrl) {
    keywords.waitUntilLocationIs(expectedUrl);
  }

  /** Waits until the current URL contains the fragment. */
  @When("I wait until location contains {string}")
  public void waitLocationContains(String fragment) {
    keywords.waitUntilLocationContains(fragment);
  }

  // ── Element utilities ─────────────────────────────────────────

  /** Scrolls the element into the visible viewport. */
  @When("I scroll to element {string}")
  public void scrollToElement(String locatorKey) {
    keywords.scrollElementIntoView(
        PropertiesLoader.get(locatorKey));
  }

  /** Sets focus to the element. */
  @When("I set focus to element {string}")
  public void focusElement(String locatorKey) {
    keywords.setFocusToElement(
        PropertiesLoader.get(locatorKey));
  }
}
