package com.mrquanga3.keywords;

import static org.assertj.core.api.Assertions.assertThat;

import com.mrquanga3.utils.ActorManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.core.Serenity;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Keyword-driven library of reusable browser actions modelled after
 * Robot Framework SeleniumLibrary.
 *
 * <p>Locator strings follow the format {@code type:value},
 * e.g.&nbsp;{@code id:input-username} or
 * {@code css:button[type='submit']}.
 *
 * <p>"Get" methods return {@link String} so callers can save the
 * result into {@link com.mrquanga3.common.Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class WebKeywords {

  private static final int DEFAULT_TIMEOUT_SECONDS = 10;
  private static final String SCREENSHOT_DIR = "target/screenshots";

  // ── driver helpers ──────────────────────────────────────────────

  private WebDriver driver() {
    if (ActorManager.hasActiveActor()) {
      return ActorManager.currentDriver();
    }
    return Serenity.getWebdriverManager().getWebdriver();
  }

  private WebDriverWait createWait() {
    return new WebDriverWait(
        driver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
  }

  private By parseLocator(String locator) {
    if (locator.startsWith("id:")) {
      return By.id(locator.substring(3));
    }
    if (locator.startsWith("css:")) {
      return By.cssSelector(locator.substring(4));
    }
    if (locator.startsWith("xpath:")) {
      return By.xpath(locator.substring(6));
    }
    if (locator.startsWith("name:")) {
      return By.name(locator.substring(5));
    }
    throw new IllegalArgumentException(
        "Unknown locator format: " + locator);
  }

  // ── Navigation ────────────────────────────────────────────────

  /** Opens the given URL. */
  @Step("Navigate to '{0}'")
  public void navigateTo(String url) {
    driver().get(url);
  }

  /** Simulates clicking the browser Back button. */
  @Step("Go back")
  public void goBack() {
    driver().navigate().back();
  }

  /** Reloads the current page. */
  @Step("Reload page")
  public void reloadPage() {
    driver().navigate().refresh();
  }

  /** Returns the current page URL. */
  @Step("Get location")
  public String getLocation() {
    return driver().getCurrentUrl();
  }

  // ── Window management ─────────────────────────────────────────

  /** Maximizes the current browser window. */
  @Step("Maximize browser window")
  public void maximizeWindow() {
    driver().manage().window().maximize();
  }

  /** Minimizes the current browser window. */
  @Step("Minimize browser window")
  public void minimizeWindow() {
    driver().manage().window().minimize();
  }

  /** Sets window size. */
  @Step("Set window size {0}x{1}")
  public void setWindowSize(int width, int height) {
    driver().manage().window().setSize(
        new Dimension(width, height));
  }

  /** Sets window position. */
  @Step("Set window position ({0}, {1})")
  public void setWindowPosition(int xcoord, int ycoord) {
    driver().manage().window().setPosition(
        new Point(xcoord, ycoord));
  }

  /** Returns window size as {@code widthxheight}. */
  @Step("Get window size")
  public String getWindowSize() {
    Dimension d = driver().manage().window().getSize();
    return d.getWidth() + "x" + d.getHeight();
  }

  /** Returns window position as {@code x,y}. */
  @Step("Get window position")
  public String getWindowPosition() {
    Point p = driver().manage().window().getPosition();
    return p.getX() + "," + p.getY();
  }

  /** Switches to window by name or handle. */
  @Step("Switch to window '{0}'")
  public void switchWindow(String nameOrHandle) {
    driver().switchTo().window(nameOrHandle);
  }

  /** Closes the currently focused window/tab. */
  @Step("Close window")
  public void closeWindow() {
    driver().close();
  }

  /** Returns all window handles as comma-separated string. */
  @Step("Get window handles")
  public String getWindowHandles() {
    return String.join(",", driver().getWindowHandles());
  }

  /** Returns titles of all windows as comma-separated string. */
  @Step("Get window titles")
  public String getWindowTitles() {
    String current = driver().getWindowHandle();
    StringBuilder titles = new StringBuilder();
    for (String handle : driver().getWindowHandles()) {
      driver().switchTo().window(handle);
      if (titles.length() > 0) {
        titles.append(',');
      }
      titles.append(driver().getTitle());
    }
    driver().switchTo().window(current);
    return titles.toString();
  }

  // ── Frame management ──────────────────────────────────────────

  /** Switches to the frame identified by locator. */
  @Step("Select frame '{0}'")
  public void selectFrame(String locator) {
    WebElement frame = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    driver().switchTo().frame(frame);
  }

  /** Switches back to the main/default content. */
  @Step("Unselect frame")
  public void unselectFrame() {
    driver().switchTo().defaultContent();
  }

  // ── Click actions ─────────────────────────────────────────────

  /** Clicks the element. */
  @Step("Click element '{0}'")
  public void clickElement(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    el.click();
  }

  /** Double-clicks the element. */
  @Step("Double click '{0}'")
  public void doubleClickElement(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    new Actions(driver()).doubleClick(el).perform();
  }

  /** Clicks element at offset from its top-left corner. */
  @Step("Click '{0}' at ({1}, {2})")
  public void clickElementAtCoordinates(
      String locator, int xoffset, int yoffset) {
    WebElement el = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    new Actions(driver())
        .moveToElement(el, xoffset, yoffset).click().perform();
  }

  /** Right-clicks the element (opens context menu). */
  @Step("Right click '{0}'")
  public void contextClickElement(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    new Actions(driver()).contextClick(el).perform();
  }

  // ── Mouse actions ─────────────────────────────────────────────

  /** Hovers the mouse over the element. */
  @Step("Mouse over '{0}'")
  public void mouseOver(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Actions(driver()).moveToElement(el).perform();
  }

  /** Moves the mouse away from the element. */
  @Step("Mouse out from '{0}'")
  public void mouseOut(String locator) {
    createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Actions(driver()).moveByOffset(-9999, -9999).perform();
  }

  /** Drags source element onto target element. */
  @Step("Drag '{0}' to '{1}'")
  public void dragAndDrop(String sourceLocator, String targetLocator) {
    WebElement src = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(sourceLocator)));
    WebElement tgt = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(targetLocator)));
    new Actions(driver()).dragAndDrop(src, tgt).perform();
  }

  /** Drags element by pixel offset. */
  @Step("Drag '{0}' by offset ({1}, {2})")
  public void dragAndDropByOffset(
      String locator, int xoffset, int yoffset) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Actions(driver())
        .dragAndDropBy(el, xoffset, yoffset).perform();
  }

  // ── Keyboard / input ──────────────────────────────────────────

  /** Clears and types text into the field. */
  @Step("Input '{1}' into element '{0}'")
  public void inputText(String locator, String text) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    el.clear();
    el.sendKeys(text);
  }

  /** Clears the text-input element. */
  @Step("Clear text of '{0}'")
  public void clearElementText(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    el.clear();
  }

  /** Presses a key on the element (e.g. ENTER, TAB, ESCAPE). */
  @Step("Press key '{1}' on '{0}'")
  public void pressKey(String locator, String keyName) {
    Keys key = Keys.valueOf(
        keyName.trim().toUpperCase(Locale.ROOT));
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    el.sendKeys(key);
  }

  // ── Element getters ───────────────────────────────────────────

  /** Returns the visible text of the element. */
  @Step("Get text of '{0}'")
  public String getText(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    return el.getText();
  }

  /** Returns the {@code value} attribute of the element. */
  @Step("Get value of '{0}'")
  public String getValue(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    return el.getAttribute("value");
  }

  /** Returns the given attribute of the element. */
  @Step("Get attribute '{1}' of '{0}'")
  public String getElementAttribute(
      String locator, String attribute) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    String val = el.getAttribute(attribute);
    return val != null ? val : "";
  }

  /** Returns the number of elements matching the locator. */
  @Step("Get element count of '{0}'")
  public String getElementCount(String locator) {
    List<WebElement> elements =
        driver().findElements(parseLocator(locator));
    return String.valueOf(elements.size());
  }

  // ── Element state assertions ──────────────────────────────────

  /** Asserts the element is visible. */
  @Step("Verify element '{0}' is visible")
  public void verifyElementVisible(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.isDisplayed())
        .as("Element '%s' should be visible", locator)
        .isTrue();
  }

  /** Asserts the element is NOT visible. */
  @Step("Verify element '{0}' is not visible")
  public void verifyElementNotVisible(String locator) {
    Boolean invisible = createWait().until(
        ExpectedConditions.invisibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(invisible)
        .as("Element '%s' should not be visible", locator)
        .isTrue();
  }

  /** Asserts the element is enabled. */
  @Step("Verify element '{0}' is enabled")
  public void verifyElementEnabled(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(el.isEnabled())
        .as("Element '%s' should be enabled", locator)
        .isTrue();
  }

  /** Asserts the element is disabled. */
  @Step("Verify element '{0}' is disabled")
  public void verifyElementDisabled(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(el.isEnabled())
        .as("Element '%s' should be disabled", locator)
        .isFalse();
  }

  /** Asserts the element is focused. */
  @Step("Verify element '{0}' is focused")
  public void verifyElementFocused(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(el).as("Element '%s' should be focused", locator)
        .isEqualTo(driver().switchTo().activeElement());
  }

  /** Asserts visible text of element contains expected. */
  @Step("Verify '{0}' contains text '{1}'")
  public void verifyElementContains(
      String locator, String expected) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.getText())
        .as("Element '%s' should contain '%s'",
            locator, expected)
        .contains(expected);
  }

  /** Asserts visible text of element does not contain text. */
  @Step("Verify '{0}' not contains text '{1}'")
  public void verifyElementNotContains(
      String locator, String expected) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.getText())
        .as("Element '%s' should not contain '%s'",
            locator, expected)
        .doesNotContain(expected);
  }

  /** Asserts visible text of element equals expected exactly. */
  @Step("Verify '{0}' text is '{1}'")
  public void verifyElementTextIs(
      String locator, String expected) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.getText())
        .as("Element '%s' text should be '%s'",
            locator, expected)
        .isEqualTo(expected);
  }

  /** Asserts an attribute of the element equals expected. */
  @Step("Verify '{0}' attr '{1}' is '{2}'")
  public void verifyElementAttributeIs(
      String locator, String attribute, String expected) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(el.getAttribute(attribute))
        .as("Attribute '%s' of '%s' should be '%s'",
            attribute, locator, expected)
        .isEqualTo(expected);
  }

  // ── Page content assertions ───────────────────────────────────

  /** Asserts the page source contains the given text. */
  @Step("Verify page contains text '{0}'")
  public void pageShouldContainText(String text) {
    assertThat(driver().getPageSource())
        .as("Page should contain text '%s'", text)
        .contains(text);
  }

  /** Asserts the page source does NOT contain the text. */
  @Step("Verify page not contains text '{0}'")
  public void pageShouldNotContainText(String text) {
    assertThat(driver().getPageSource())
        .as("Page should not contain text '%s'", text)
        .doesNotContain(text);
  }

  /** Asserts at least one element matches the locator. */
  @Step("Verify page contains element '{0}'")
  public void pageShouldContainElement(String locator) {
    List<WebElement> found =
        driver().findElements(parseLocator(locator));
    assertThat(found)
        .as("Page should contain element '%s'", locator)
        .isNotEmpty();
  }

  /** Asserts no element matches the locator. */
  @Step("Verify page not contains element '{0}'")
  public void pageShouldNotContainElement(String locator) {
    List<WebElement> found =
        driver().findElements(parseLocator(locator));
    assertThat(found)
        .as("Page should not contain element '%s'", locator)
        .isEmpty();
  }

  // ── URL / location assertions ─────────────────────────────────

  /** Asserts the current URL contains the fragment. */
  @Step("Verify URL contains '{0}'")
  public void verifyUrlContains(String fragment) {
    createWait().until(ExpectedConditions.urlContains(fragment));
    assertThat(driver().getCurrentUrl())
        .as("Current URL should contain '%s'", fragment)
        .contains(fragment);
  }

  /** Asserts the current URL equals expected exactly. */
  @Step("Verify URL is '{0}'")
  public void verifyLocationIs(String expectedUrl) {
    createWait().until(ExpectedConditions.urlToBe(expectedUrl));
    assertThat(driver().getCurrentUrl())
        .as("Current URL should be '%s'", expectedUrl)
        .isEqualTo(expectedUrl);
  }

  // ── Title ─────────────────────────────────────────────────────

  /** Returns the current page title. */
  @Step("Get title")
  public String getTitle() {
    return driver().getTitle();
  }

  /** Asserts the page title equals expected. */
  @Step("Title should be '{0}'")
  public void verifyTitleIs(String expected) {
    createWait().until(ExpectedConditions.titleIs(expected));
    assertThat(driver().getTitle())
        .as("Page title should be '%s'", expected)
        .isEqualTo(expected);
  }

  // ── Page source ───────────────────────────────────────────────

  /** Returns the full HTML source of the current page. */
  @Step("Get page source")
  public String getPageSource() {
    return driver().getPageSource();
  }

  // ── Checkbox ──────────────────────────────────────────────────

  /** Selects (checks) the checkbox if not already selected. */
  @Step("Select checkbox '{0}'")
  public void selectCheckbox(String locator) {
    WebElement cb = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    if (!cb.isSelected()) {
      cb.click();
    }
  }

  /** Un-selects (unchecks) the checkbox if selected. */
  @Step("Unselect checkbox '{0}'")
  public void unselectCheckbox(String locator) {
    WebElement cb = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    if (cb.isSelected()) {
      cb.click();
    }
  }

  /** Asserts the checkbox is selected. */
  @Step("Verify checkbox '{0}' is selected")
  public void verifyCheckboxSelected(String locator) {
    WebElement cb = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(cb.isSelected())
        .as("Checkbox '%s' should be selected", locator)
        .isTrue();
  }

  /** Asserts the checkbox is NOT selected. */
  @Step("Verify checkbox '{0}' not selected")
  public void verifyCheckboxNotSelected(String locator) {
    WebElement cb = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(cb.isSelected())
        .as("Checkbox '%s' should not be selected", locator)
        .isFalse();
  }

  // ── Radio button ──────────────────────────────────────────────

  /** Clicks the radio button. */
  @Step("Select radio button '{0}'")
  public void selectRadioButton(String locator) {
    WebElement rb = createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
    if (!rb.isSelected()) {
      rb.click();
    }
  }

  /** Asserts the radio button is selected. */
  @Step("Verify radio button '{0}' selected")
  public void verifyRadioButtonSelected(String locator) {
    WebElement rb = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(rb.isSelected())
        .as("Radio button '%s' should be selected", locator)
        .isTrue();
  }

  /** Asserts the radio button is NOT selected. */
  @Step("Verify radio button '{0}' not selected")
  public void verifyRadioButtonNotSelected(String locator) {
    WebElement rb = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    assertThat(rb.isSelected())
        .as("Radio button '%s' should not be selected", locator)
        .isFalse();
  }

  // ── Select list / dropdown ────────────────────────────────────

  /** Selects option by visible text. */
  @Step("Select '{1}' from dropdown '{0}'")
  public void selectFromListByLabel(
      String locator, String label) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Select(el).selectByVisibleText(label);
  }

  /** Selects option by value attribute. */
  @Step("Select value '{1}' from dropdown '{0}'")
  public void selectFromListByValue(
      String locator, String value) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Select(el).selectByValue(value);
  }

  /** Selects option by zero-based index. */
  @Step("Select index {1} from dropdown '{0}'")
  public void selectFromListByIndex(
      String locator, int index) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Select(el).selectByIndex(index);
  }

  /** Deselects all options (multi-select lists only). */
  @Step("Unselect all from dropdown '{0}'")
  public void unselectAllFromList(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    new Select(el).deselectAll();
  }

  /** Returns the label of the first selected option. */
  @Step("Get selected label of dropdown '{0}'")
  public String getSelectedListLabel(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    return new Select(el).getFirstSelectedOption().getText();
  }

  /** Returns the value attribute of the first selected option. */
  @Step("Get selected value of dropdown '{0}'")
  public String getSelectedListValue(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    return new Select(el).getFirstSelectedOption()
        .getAttribute("value");
  }

  // ── Table ─────────────────────────────────────────────────────

  /** Returns cell text (1-based row and column). */
  @Step("Get cell [{1},{2}] of table '{0}'")
  public String getTableCell(
      String locator, int row, int column) {
    WebElement table = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    List<WebElement> rows = table.findElements(By.tagName("tr"));
    assertThat(rows.size()).as("Table row count").isGreaterThanOrEqualTo(row);
    List<WebElement> cells =
        rows.get(row - 1).findElements(By.xpath("td|th"));
    assertThat(cells.size()).as("Table column count")
        .isGreaterThanOrEqualTo(column);
    return cells.get(column - 1).getText();
  }

  /** Asserts the table contains the expected text. */
  @Step("Verify table '{0}' contains '{1}'")
  public void verifyTableContains(
      String locator, String expected) {
    WebElement table = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(table.getText())
        .as("Table '%s' should contain '%s'", locator, expected)
        .contains(expected);
  }

  /** Asserts a specific table cell contains text. */
  @Step("Verify cell [{1},{2}] of '{0}' contains '{3}'")
  public void verifyTableCellContains(
      String locator, int row, int column, String expected) {
    String cellText = getTableCell(locator, row, column);
    assertThat(cellText)
        .as("Cell [%d,%d] should contain '%s'",
            row, column, expected)
        .contains(expected);
  }

  // ── Form ──────────────────────────────────────────────────────

  /** Submits the form. */
  @Step("Submit form '{0}'")
  public void submitForm(String locator) {
    WebElement form = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    form.submit();
  }

  /** Inputs a file path into a file-upload input. */
  @Step("Choose file '{1}' for '{0}'")
  public void chooseFile(String locator, String filePath) {
    WebElement input = driver().findElement(
        parseLocator(locator));
    input.sendKeys(filePath);
  }

  // ── Alert ─────────────────────────────────────────────────────

  /** Accepts the alert and returns its message. */
  @Step("Accept alert")
  public String acceptAlert() {
    Alert alert = createWait().until(
        ExpectedConditions.alertIsPresent());
    String text = alert.getText();
    alert.accept();
    return text;
  }

  /** Dismisses the alert and returns its message. */
  @Step("Dismiss alert")
  public String dismissAlert() {
    Alert alert = createWait().until(
        ExpectedConditions.alertIsPresent());
    String text = alert.getText();
    alert.dismiss();
    return text;
  }

  /** Asserts an alert is currently present. */
  @Step("Verify alert is present")
  public void verifyAlertPresent() {
    try {
      driver().switchTo().alert();
    } catch (NoAlertPresentException e) {
      throw new AssertionError(
          "Expected alert to be present", e);
    }
  }

  /** Asserts no alert is currently present. */
  @Step("Verify alert is not present")
  public void verifyAlertNotPresent() {
    boolean present;
    try {
      driver().switchTo().alert();
      present = true;
    } catch (NoAlertPresentException e) {
      present = false;
    }
    assertThat(present)
        .as("Expected no alert to be present").isFalse();
  }

  /** Types text into the alert prompt and accepts. */
  @Step("Input '{0}' into alert and accept")
  public void inputTextIntoAlert(String text) {
    Alert alert = createWait().until(
        ExpectedConditions.alertIsPresent());
    alert.sendKeys(text);
    alert.accept();
  }

  // ── Cookie ────────────────────────────────────────────────────

  /** Adds a cookie. */
  @Step("Add cookie '{0}'='{1}'")
  public void addCookie(String name, String value) {
    driver().manage().addCookie(new Cookie(name, value));
  }

  /** Deletes the named cookie. */
  @Step("Delete cookie '{0}'")
  public void deleteCookie(String name) {
    driver().manage().deleteCookieNamed(name);
  }

  /** Deletes all cookies. */
  @Step("Delete all cookies")
  public void deleteAllCookies() {
    driver().manage().deleteAllCookies();
  }

  /** Returns the value of the named cookie. */
  @Step("Get cookie '{0}'")
  public String getCookieValue(String name) {
    Cookie cookie = driver().manage().getCookieNamed(name);
    if (cookie == null) {
      throw new IllegalArgumentException(
          "Cookie not found: " + name);
    }
    return cookie.getValue();
  }

  // ── JavaScript ────────────────────────────────────────────────

  /** Executes JavaScript and returns the result as String. */
  @Step("Execute JavaScript")
  public String executeJavascript(String code) {
    JavascriptExecutor js = (JavascriptExecutor) driver();
    Object result = js.executeScript(code);
    return result != null ? result.toString() : "";
  }

  // ── Screenshot ────────────────────────────────────────────────

  /** Takes a full-page screenshot, returns the file path. */
  @Step("Capture page screenshot")
  public String capturePageScreenshot() {
    byte[] data = ((TakesScreenshot) driver())
        .getScreenshotAs(OutputType.BYTES);
    return saveScreenshot(data, "page");
  }

  /** Takes a screenshot of a specific element. */
  @Step("Capture screenshot of element '{0}'")
  public String captureElementScreenshot(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    byte[] data = el.getScreenshotAs(OutputType.BYTES);
    return saveScreenshot(data, "element");
  }

  private String saveScreenshot(byte[] data, String prefix) {
    Path dir = Paths.get(SCREENSHOT_DIR);
    try {
      Files.createDirectories(dir);
      String name = prefix + "_"
          + System.currentTimeMillis() + ".png";
      Path file = dir.resolve(name);
      Files.write(file, data);
      return file.toString();
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to save screenshot", e);
    }
  }

  // ── Wait keywords ─────────────────────────────────────────────

  /** Waits until the element is visible. */
  @Step("Wait until '{0}' is visible")
  public void waitUntilElementVisible(String locator) {
    createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
  }

  /** Waits until the element is NOT visible. */
  @Step("Wait until '{0}' is not visible")
  public void waitUntilElementNotVisible(String locator) {
    createWait().until(
        ExpectedConditions.invisibilityOfElementLocated(
            parseLocator(locator)));
  }

  /** Waits until the element is enabled. */
  @Step("Wait until '{0}' is enabled")
  public void waitUntilElementEnabled(String locator) {
    createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
  }

  /** Waits until the element contains the expected text. */
  @Step("Wait until '{0}' contains '{1}'")
  public void waitUntilElementContains(
      String locator, String text) {
    createWait().until(
        ExpectedConditions.textToBePresentInElementLocated(
            parseLocator(locator), text));
  }

  /** Waits until the page source contains the text. */
  @Step("Wait until page contains text '{0}'")
  public void waitUntilPageContainsText(String text) {
    createWait().until(d -> {
      String src = d.getPageSource();
      return src != null && src.contains(text);
    });
  }

  /** Waits until the page source does NOT contain the text. */
  @Step("Wait until page not contains text '{0}'")
  public void waitUntilPageNotContainsText(String text) {
    createWait().until(d -> {
      String src = d.getPageSource();
      return src == null || !src.contains(text);
    });
  }

  /** Waits until at least one element matches the locator. */
  @Step("Wait until page contains element '{0}'")
  public void waitUntilPageContainsElement(String locator) {
    createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
  }

  /** Waits until no element matches the locator. */
  @Step("Wait page not contains element '{0}'")
  public void waitUntilPageNotContainsElement(String locator) {
    createWait().until(d ->
        d.findElements(parseLocator(locator)).isEmpty());
  }

  /** Waits until the current URL equals expected. */
  @Step("Wait until location is '{0}'")
  public void waitUntilLocationIs(String expectedUrl) {
    createWait().until(
        ExpectedConditions.urlToBe(expectedUrl));
  }

  /** Waits until the current URL contains the fragment. */
  @Step("Wait until location contains '{0}'")
  public void waitUntilLocationContains(String fragment) {
    createWait().until(
        ExpectedConditions.urlContains(fragment));
  }

  // ── Element utilities ─────────────────────────────────────────

  /** Scrolls the element into the visible viewport. */
  @Step("Scroll to element '{0}'")
  public void scrollElementIntoView(String locator) {
    WebElement el = driver().findElement(
        parseLocator(locator));
    ((JavascriptExecutor) driver()).executeScript(
        "arguments[0].scrollIntoView({block:'center'})", el);
  }

  /** Sets focus to the element. */
  @Step("Focus on element '{0}'")
  public void setFocusToElement(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.presenceOfElementLocated(
            parseLocator(locator)));
    ((JavascriptExecutor) driver()).executeScript(
        "arguments[0].focus()", el);
  }
}
