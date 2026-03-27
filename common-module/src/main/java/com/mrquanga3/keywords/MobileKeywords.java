package com.mrquanga3.keywords;

import static org.assertj.core.api.Assertions.assertThat;

import com.mrquanga3.utils.ActorManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.remote.SupportsRotation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import net.serenitybdd.annotations.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Keyword-driven library of reusable mobile actions for Appium.
 *
 * <p>Locator strings follow the format {@code type:value},
 * e.g.&nbsp;{@code id:com.example:id/button} or
 * {@code accessibilityId:Search}.
 *
 * <p>Supported locator prefixes:
 * {@code id:}, {@code xpath:}, {@code accessibilityId:},
 * {@code uiAutomator:}, {@code classChain:}, {@code predicate:},
 * {@code className:}, {@code css:}.
 *
 * <p>"Get" methods return {@link String} so callers can save the
 * result into {@link com.mrquanga3.common.Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class MobileKeywords {

  private static final int DEFAULT_TIMEOUT_SECONDS = 10;
  private static final String SCREENSHOT_DIR = "target/screenshots";

  // ── driver helpers ──────────────────────────────────────────────

  private AppiumDriver driver() {
    return ActorManager.currentMobileDriver();
  }

  private WebDriverWait createWait() {
    return new WebDriverWait(
        driver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
  }

  private By parseLocator(String locator) {
    if (locator.startsWith("id:")) {
      return AppiumBy.id(locator.substring(3));
    }
    if (locator.startsWith("xpath:")) {
      return AppiumBy.xpath(locator.substring(6));
    }
    if (locator.startsWith("accessibilityId:")) {
      return AppiumBy.accessibilityId(locator.substring(16));
    }
    if (locator.startsWith("uiAutomator:")) {
      return AppiumBy.androidUIAutomator(locator.substring(12));
    }
    if (locator.startsWith("classChain:")) {
      return AppiumBy.iOSClassChain(locator.substring(11));
    }
    if (locator.startsWith("predicate:")) {
      return AppiumBy.iOSNsPredicateString(
          locator.substring(10));
    }
    if (locator.startsWith("className:")) {
      return AppiumBy.className(locator.substring(10));
    }
    if (locator.startsWith("css:")) {
      return AppiumBy.cssSelector(locator.substring(4));
    }
    throw new IllegalArgumentException(
        "Unknown mobile locator format: " + locator);
  }

  // ── Tap / Click ─────────────────────────────────────────────────

  /** Taps the element identified by the locator. */
  @Step("Tap element '{0}'")
  public void tapElement(String locator) {
    createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator))).click();
  }

  /** Taps at absolute screen coordinates. */
  @Step("Tap at coordinates ({0}, {1})")
  public void tapAtCoordinates(int xcoord, int ycoord) {
    PointerInput finger = new PointerInput(
        PointerInput.Kind.TOUCH, "finger");
    Sequence tap = new Sequence(finger, 1)
        .addAction(finger.createPointerMove(
            Duration.ZERO,
            PointerInput.Origin.viewport(),
            xcoord, ycoord))
        .addAction(finger.createPointerDown(
            PointerInput.MouseButton.LEFT.asArg()))
        .addAction(finger.createPointerUp(
            PointerInput.MouseButton.LEFT.asArg()));
    driver().perform(List.of(tap));
  }

  /** Long-presses the element for ~1 second. */
  @Step("Long press element '{0}'")
  public void longPressElement(String locator) {
    WebElement element = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    org.openqa.selenium.Point loc = element.getLocation();
    Dimension size = element.getSize();
    int cx = loc.getX() + size.getWidth() / 2;
    int cy = loc.getY() + size.getHeight() / 2;

    PointerInput finger = new PointerInput(
        PointerInput.Kind.TOUCH, "finger");
    Sequence longPress = new Sequence(finger, 1)
        .addAction(finger.createPointerMove(
            Duration.ZERO,
            PointerInput.Origin.viewport(), cx, cy))
        .addAction(finger.createPointerDown(
            PointerInput.MouseButton.LEFT.asArg()))
        .addAction(new Pause(finger, Duration.ofMillis(1000)))
        .addAction(finger.createPointerUp(
            PointerInput.MouseButton.LEFT.asArg()));
    driver().perform(List.of(longPress));
  }

  // ── Input ───────────────────────────────────────────────────────

  /** Clears the field and types the given text. */
  @Step("Input '{1}' into '{0}'")
  public void inputText(String locator, String text) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    el.clear();
    el.sendKeys(text);
  }

  /** Clears the text from the field. */
  @Step("Clear text of '{0}'")
  public void clearText(String locator) {
    createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).clear();
  }

  /** Hides the on-screen keyboard. */
  @Step("Hide keyboard")
  public void hideKeyboard() {
    ((HidesKeyboard) driver()).hideKeyboard();
  }

  // ── Element getters ─────────────────────────────────────────────

  /** Returns the visible text of the element. */
  @Step("Get text of '{0}'")
  public String getText(String locator) {
    return createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).getText();
  }

  /** Returns the value of the given attribute. */
  @Step("Get attribute '{1}' of '{0}'")
  public String getElementAttribute(
      String locator, String attribute) {
    return createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).getAttribute(attribute);
  }

  /** Returns the number of matching elements as a string. */
  @Step("Get element count of '{0}'")
  public String getElementCount(String locator) {
    return String.valueOf(
        driver().findElements(parseLocator(locator)).size());
  }

  /** Returns the page/screen source XML. */
  @Step("Get page source")
  public String getPageSource() {
    return driver().getPageSource();
  }

  // ── Element assertions ──────────────────────────────────────────

  /** Asserts the element is visible. */
  @Step("Verify element '{0}' is visible")
  public void verifyElementVisible(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.isDisplayed())
        .as("Element should be visible: %s", locator)
        .isTrue();
  }

  /** Asserts the element is NOT visible. */
  @Step("Verify element '{0}' is not visible")
  public void verifyElementNotVisible(String locator) {
    Boolean gone = createWait().until(
        ExpectedConditions.invisibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(gone)
        .as("Element should not be visible: %s", locator)
        .isTrue();
  }

  /** Asserts the element is enabled. */
  @Step("Verify element '{0}' is enabled")
  public void verifyElementEnabled(String locator) {
    WebElement el = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
    assertThat(el.isEnabled())
        .as("Element should be enabled: %s", locator)
        .isTrue();
  }

  /** Asserts the element's text contains the expected substring. */
  @Step("Verify element '{0}' contains text '{1}'")
  public void verifyElementContains(
      String locator, String expected) {
    String actual = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).getText();
    assertThat(actual)
        .as("Element text should contain '%s'", expected)
        .contains(expected);
  }

  /** Asserts the element's text equals the expected value. */
  @Step("Verify element '{0}' text is '{1}'")
  public void verifyElementTextIs(
      String locator, String expected) {
    String actual = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).getText();
    assertThat(actual)
        .as("Element text should be '%s'", expected)
        .isEqualTo(expected);
  }

  /** Asserts an attribute of the element equals the expected value. */
  @Step("Verify element '{0}' attribute '{1}' is '{2}'")
  public void verifyElementAttributeIs(
      String locator, String attribute, String expected) {
    String actual = createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator))).getAttribute(attribute);
    assertThat(actual)
        .as("Attribute '%s' should be '%s'", attribute, expected)
        .isEqualTo(expected);
  }

  /** Asserts at least one matching element exists on screen. */
  @Step("Verify screen contains element '{0}'")
  public void verifyScreenContainsElement(String locator) {
    List<WebElement> elements =
        driver().findElements(parseLocator(locator));
    assertThat(elements)
        .as("Screen should contain element: %s", locator)
        .isNotEmpty();
  }

  /** Asserts no matching element exists on screen. */
  @Step("Verify screen does not contain element '{0}'")
  public void verifyScreenNotContainsElement(String locator) {
    List<WebElement> elements =
        driver().findElements(parseLocator(locator));
    assertThat(elements)
        .as("Screen should not contain element: %s", locator)
        .isEmpty();
  }

  // ── Gestures ────────────────────────────────────────────────────

  /** Swipes up from bottom-center to top-center. */
  @Step("Swipe up")
  public void swipeUp() {
    performSwipe(0.8, 0.2);
  }

  /** Swipes down from top-center to bottom-center. */
  @Step("Swipe down")
  public void swipeDown() {
    performSwipe(0.2, 0.8);
  }

  /** Swipes left from right-center to left-center. */
  @Step("Swipe left")
  public void swipeLeft() {
    performHorizontalSwipe(0.8, 0.2);
  }

  /** Swipes right from left-center to right-center. */
  @Step("Swipe right")
  public void swipeRight() {
    performHorizontalSwipe(0.2, 0.8);
  }

  /**
   * Scrolls until the given text is visible (Android only).
   * Uses UiScrollable on Android.
   */
  @Step("Scroll to text '{0}'")
  public void scrollToText(String text) {
    driver().findElement(AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().scrollable(true))"
            + ".scrollIntoView(new UiSelector().text(\""
            + text + "\"))"));
  }

  // ── App lifecycle ───────────────────────────────────────────────

  /** Activates (brings to foreground) the app. */
  @Step("Launch app '{0}'")
  public void launchApp(String appPackageOrBundleId) {
    ((InteractsWithApps) driver())
        .activateApp(appPackageOrBundleId);
  }

  /** Terminates the app. */
  @Step("Close app '{0}'")
  public void closeApp(String appPackageOrBundleId) {
    ((InteractsWithApps) driver())
        .terminateApp(appPackageOrBundleId);
  }

  /** Terminates and re-activates the app. */
  @Step("Reset app '{0}'")
  public void resetApp(String appPackageOrBundleId) {
    InteractsWithApps appsDriver =
        (InteractsWithApps) driver();
    appsDriver.terminateApp(appPackageOrBundleId);
    appsDriver.activateApp(appPackageOrBundleId);
  }

  /** Returns the app state as a string. */
  @Step("Get app state '{0}'")
  public String getAppState(String appPackageOrBundleId) {
    return ((InteractsWithApps) driver())
        .queryAppState(appPackageOrBundleId).toString();
  }

  // ── Device actions ──────────────────────────────────────────────

  /** Sets the device orientation (LANDSCAPE or PORTRAIT). */
  @Step("Set orientation to '{0}'")
  public void setOrientation(String orientation) {
    ((SupportsRotation) driver()).rotate(
        ScreenOrientation.valueOf(
            orientation.toUpperCase(java.util.Locale.ROOT)));
  }

  /** Returns the current device orientation. */
  @Step("Get orientation")
  public String getOrientation() {
    return ((SupportsRotation) driver())
        .getOrientation().toString();
  }

  /** Takes a screenshot and saves it to target/screenshots. */
  @Step("Capture mobile screenshot")
  public void captureScreenshot() {
    byte[] screenshot =
        ((TakesScreenshot) driver())
            .getScreenshotAs(OutputType.BYTES);
    Path dir = Paths.get(SCREENSHOT_DIR);
    try {
      Files.createDirectories(dir);
      Path file = dir.resolve(
          "mobile_" + System.currentTimeMillis() + ".png");
      Files.write(file, screenshot);
    } catch (IOException e) {
      throw new RuntimeException(
          "Failed to save screenshot", e);
    }
  }

  // ── Waits ───────────────────────────────────────────────────────

  /** Waits until the element is visible. */
  @Step("Wait until element '{0}' is visible")
  public void waitUntilElementVisible(String locator) {
    createWait().until(
        ExpectedConditions.visibilityOfElementLocated(
            parseLocator(locator)));
  }

  /** Waits until the element is no longer visible. */
  @Step("Wait until element '{0}' is not visible")
  public void waitUntilElementNotVisible(String locator) {
    createWait().until(
        ExpectedConditions.invisibilityOfElementLocated(
            parseLocator(locator)));
  }

  /** Waits until the element is clickable. */
  @Step("Wait until element '{0}' is enabled")
  public void waitUntilElementEnabled(String locator) {
    createWait().until(
        ExpectedConditions.elementToBeClickable(
            parseLocator(locator)));
  }

  /** Waits until the element's text contains the expected value. */
  @Step("Wait until element '{0}' contains text '{1}'")
  public void waitUntilElementContains(
      String locator, String text) {
    createWait().until(ExpectedConditions.textToBePresentInElementLocated(
        parseLocator(locator), text));
  }

  // ── Context switching (hybrid apps) ─────────────────────────────

  /** Switches to the named context (NATIVE_APP, WEBVIEW_*). */
  @Step("Switch to context '{0}'")
  public void switchToContext(String contextName) {
    ((SupportsContextSwitching) driver())
        .context(contextName);
  }

  /** Returns available contexts as comma-separated string. */
  @Step("Get contexts")
  public String getContexts() {
    Set<String> contexts =
        ((SupportsContextSwitching) driver())
            .getContextHandles();
    return String.join(",", contexts);
  }

  // ── Private gesture helpers ─────────────────────────────────────

  private void performSwipe(
      double startYpct, double endYpct) {
    Dimension size = driver().manage().window().getSize();
    int cx = size.getWidth() / 2;
    int startY = (int) (size.getHeight() * startYpct);
    int endY = (int) (size.getHeight() * endYpct);

    PointerInput finger = new PointerInput(
        PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 1)
        .addAction(finger.createPointerMove(
            Duration.ZERO,
            PointerInput.Origin.viewport(), cx, startY))
        .addAction(finger.createPointerDown(
            PointerInput.MouseButton.LEFT.asArg()))
        .addAction(finger.createPointerMove(
            Duration.ofMillis(600),
            PointerInput.Origin.viewport(), cx, endY))
        .addAction(finger.createPointerUp(
            PointerInput.MouseButton.LEFT.asArg()));
    driver().perform(List.of(swipe));
  }

  private void performHorizontalSwipe(
      double startXpct, double endXpct) {
    Dimension size = driver().manage().window().getSize();
    int cy = size.getHeight() / 2;
    int startX = (int) (size.getWidth() * startXpct);
    int endX = (int) (size.getWidth() * endXpct);

    PointerInput finger = new PointerInput(
        PointerInput.Kind.TOUCH, "finger");
    Sequence swipe = new Sequence(finger, 1)
        .addAction(finger.createPointerMove(
            Duration.ZERO,
            PointerInput.Origin.viewport(), startX, cy))
        .addAction(finger.createPointerDown(
            PointerInput.MouseButton.LEFT.asArg()))
        .addAction(finger.createPointerMove(
            Duration.ofMillis(600),
            PointerInput.Origin.viewport(), endX, cy))
        .addAction(finger.createPointerUp(
            PointerInput.MouseButton.LEFT.asArg()));
    driver().perform(List.of(swipe));
  }
}
