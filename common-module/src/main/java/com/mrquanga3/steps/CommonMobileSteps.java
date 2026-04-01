package com.mrquanga3.steps;

import com.mrquanga3.common.Common;
import com.mrquanga3.keywords.MobileKeywords;
import com.mrquanga3.utils.ActorManager;
import com.mrquanga3.utils.PropertiesLoader;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import net.serenitybdd.annotations.Steps;

/**
 * Generic Cucumber step definitions for mobile (Appium) actions.
 *
 * <p>Locator parameters are resolved from {@link PropertiesLoader}.
 * Device profiles are defined in environment properties using a
 * key prefix (e.g. {@code android-emulator.platformName}).
 */
@SuppressWarnings("PMD.GodClass")
public class CommonMobileSteps {

  @Steps
  MobileKeywords mobileKeywords;

  // ── Actor management ────────────────────────────────────────────

  /**
   * Opens a mobile device session using a device profile key.
   *
   * <p>The profile key resolves properties like:
   * <pre>
   * android-emulator.platformName = android
   * android-emulator.udid = emulator-5554
   * android-emulator.appPackage = com.google.android.apps.messaging
   * android-emulator.appActivity = .ui.ConversationListActivity
   * android-emulator.appiumUrl = http://127.0.0.1:4723
   * android-emulator.automationName = UiAutomator2
   * </pre>
   */
  @Given("{string} opens a mobile device {string}")
  public void actorOpensMobileDevice(
      String actorName, String deviceProfile) {
    String platform = PropertiesLoader.get(
        deviceProfile + ".platformName")
        .trim().toLowerCase(Locale.ROOT);
    String appiumUrlStr = PropertiesLoader.get(
        deviceProfile + ".appiumUrl");
    URL appiumUrl;
    try {
      appiumUrl = new URL(appiumUrlStr);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(
          "Invalid Appium URL: " + appiumUrlStr, e);
    }

    if ("android".equals(platform)) {
      UiAutomator2Options opts = buildAndroidOptions(
          deviceProfile);
      ActorManager.openMobileDevice(
          actorName, platform, appiumUrl, opts);
    } else if ("ios".equals(platform)) {
      XCUITestOptions opts = buildIosOptions(deviceProfile);
      ActorManager.openMobileDevice(
          actorName, platform, appiumUrl, opts);
    } else {
      throw new IllegalArgumentException(
          "Unsupported platform: " + platform);
    }
  }

  // ── Tap / Click ─────────────────────────────────────────────────

  /** Taps the element resolved from properties. */
  @When("I tap {string}")
  public void tapElement(String locatorKey) {
    mobileKeywords.tapElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Taps at absolute screen coordinates. */
  @When("I tap at coordinates {int} and {int}")
  public void tapAtCoordinates(int xcoord, int ycoord) {
    mobileKeywords.tapAtCoordinates(xcoord, ycoord);
  }

  /** Long-presses the element. */
  @When("I long press {string}")
  public void longPressElement(String locatorKey) {
    mobileKeywords.longPressElement(
        PropertiesLoader.get(locatorKey));
  }

  // ── Input ───────────────────────────────────────────────────────

  /** Types text into a mobile field. */
  @When("I enter {string} to mobile field {string}")
  public void enterTextToMobileField(
      String text, String locatorKey) {
    mobileKeywords.inputText(
        PropertiesLoader.get(locatorKey), text);
  }

  /** Types a previously saved variable into a mobile field. */
  @When("I enter saved variable {string} to mobile field {string}")
  public void enterSavedVariableToMobileField(
      String variableKey, String locatorKey) {
    String value = Common.getVariable(variableKey);
    mobileKeywords.inputText(
        PropertiesLoader.get(locatorKey), value);
  }

  /** Clears a mobile field. */
  @When("I clear mobile field {string}")
  public void clearMobileField(String locatorKey) {
    mobileKeywords.clearText(
        PropertiesLoader.get(locatorKey));
  }

  /** Hides the on-screen keyboard. */
  @When("I hide keyboard")
  public void hideKeyboard() {
    mobileKeywords.hideKeyboard();
  }

  // ── Getters (save to variable) ──────────────────────────────────

  /** Gets visible text and saves to a variable. */
  @When("I get text of mobile element {string} then save to {string}")
  public void getTextAndSave(
      String locatorKey, String variableName) {
    String text = mobileKeywords.getText(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableName, text);
  }

  /** Gets an attribute value and saves to a variable. */
  @When("I get attribute {string} of mobile element {string} then save to {string}")
  public void getAttributeAndSave(
      String attribute, String locatorKey,
      String variableName) {
    String value = mobileKeywords.getElementAttribute(
        PropertiesLoader.get(locatorKey), attribute);
    Common.saveVariable(variableName, value);
  }

  /** Gets the element count and saves to a variable. */
  @When("I get mobile element count of {string} then save to {string}")
  public void getElementCountAndSave(
      String locatorKey, String variableName) {
    String count = mobileKeywords.getElementCount(
        PropertiesLoader.get(locatorKey));
    Common.saveVariable(variableName, count);
  }

  /** Gets mobile page source and saves to a variable. */
  @When("I get mobile page source then save to {string}")
  public void getPageSourceAndSave(String variableName) {
    Common.saveVariable(variableName,
        mobileKeywords.getPageSource());
  }

  // ── Assertions ──────────────────────────────────────────────────

  /** Asserts the mobile element is visible. */
  @Then("mobile element {string} should be visible")
  public void mobileElementShouldBeVisible(
      String locatorKey) {
    mobileKeywords.verifyElementVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the mobile element is NOT visible. */
  @Then("mobile element {string} should not be visible")
  public void mobileElementShouldNotBeVisible(
      String locatorKey) {
    mobileKeywords.verifyElementNotVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the mobile element is enabled. */
  @Then("mobile element {string} should be enabled")
  public void mobileElementShouldBeEnabled(
      String locatorKey) {
    mobileKeywords.verifyElementEnabled(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the mobile element contains the expected text. */
  @Then("mobile element {string} should contain text {string}")
  public void mobileElementShouldContainText(
      String locatorKey, String expected) {
    mobileKeywords.verifyElementContains(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts the mobile element's text equals exactly. */
  @Then("mobile element {string} text should be {string}")
  public void mobileElementTextShouldBe(
      String locatorKey, String expected) {
    mobileKeywords.verifyElementTextIs(
        PropertiesLoader.get(locatorKey), expected);
  }

  /** Asserts an attribute value of the mobile element. */
  @Then("mobile element {string} attribute {string} should be {string}")
  public void mobileElementAttributeShouldBe(
      String locatorKey, String attribute, String expected) {
    mobileKeywords.verifyElementAttributeIs(
        PropertiesLoader.get(locatorKey), attribute, expected);
  }

  /** Asserts the screen contains the element. */
  @Then("screen should contain mobile element {string}")
  public void screenShouldContainElement(
      String locatorKey) {
    mobileKeywords.verifyScreenContainsElement(
        PropertiesLoader.get(locatorKey));
  }

  /** Asserts the screen does NOT contain the element. */
  @Then("screen should not contain mobile element {string}")
  public void screenShouldNotContainElement(
      String locatorKey) {
    mobileKeywords.verifyScreenNotContainsElement(
        PropertiesLoader.get(locatorKey));
  }

  // ── Gestures ────────────────────────────────────────────────────

  /** Swipes up on the screen. */
  @When("I swipe up")
  public void swipeUp() {
    mobileKeywords.swipeUp();
  }

  /** Swipes down on the screen. */
  @When("I swipe down")
  public void swipeDown() {
    mobileKeywords.swipeDown();
  }

  /** Swipes left on the screen. */
  @When("I swipe left")
  public void swipeLeft() {
    mobileKeywords.swipeLeft();
  }

  /** Swipes right on the screen. */
  @When("I swipe right")
  public void swipeRight() {
    mobileKeywords.swipeRight();
  }

  /** Scrolls until the given text is visible (Android). */
  @When("I scroll to text {string}")
  public void scrollToText(String text) {
    mobileKeywords.scrollToText(text);
  }

  // ── App lifecycle ───────────────────────────────────────────────

  /** Activates the app. */
  @When("I launch app {string}")
  public void launchApp(String appId) {
    mobileKeywords.launchApp(appId);
  }

  /** Terminates the app. */
  @When("I close app {string}")
  public void closeApp(String appId) {
    mobileKeywords.closeApp(appId);
  }

  /** Resets (terminate + activate) the app. */
  @When("I reset app {string}")
  public void resetApp(String appId) {
    mobileKeywords.resetApp(appId);
  }

  /** Gets app state and saves to a variable. */
  @When("I get app state of {string} then save to {string}")
  public void getAppStateAndSave(
      String appId, String variableName) {
    Common.saveVariable(variableName,
        mobileKeywords.getAppState(appId));
  }

  // ── Device actions ──────────────────────────────────────────────

  /** Sets the device orientation. */
  @When("I set orientation to {string}")
  public void setOrientation(String orientation) {
    mobileKeywords.setOrientation(orientation);
  }

  /** Gets orientation and saves to a variable. */
  @When("I get orientation then save to {string}")
  public void getOrientationAndSave(String variableName) {
    Common.saveVariable(variableName,
        mobileKeywords.getOrientation());
  }

  /** Captures a mobile screenshot. */
  @When("I capture mobile screenshot")
  public void captureMobileScreenshot() {
    mobileKeywords.captureScreenshot();
  }

  // ── Waits ───────────────────────────────────────────────────────

  /** Waits until the mobile element is visible. */
  @Then("I wait until mobile element {string} is visible")
  public void waitUntilMobileElementVisible(
      String locatorKey) {
    mobileKeywords.waitUntilElementVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until the mobile element is not visible. */
  @Then("I wait until mobile element {string} is not visible")
  public void waitUntilMobileElementNotVisible(
      String locatorKey) {
    mobileKeywords.waitUntilElementNotVisible(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until the mobile element is enabled/clickable. */
  @Then("I wait until mobile element {string} is enabled")
  public void waitUntilMobileElementEnabled(
      String locatorKey) {
    mobileKeywords.waitUntilElementEnabled(
        PropertiesLoader.get(locatorKey));
  }

  /** Waits until the mobile element contains the given text. */
  @Then("I wait until mobile element {string} contains text {string}")
  public void waitUntilMobileElementContainsText(
      String locatorKey, String text) {
    mobileKeywords.waitUntilElementContains(
        PropertiesLoader.get(locatorKey), text);
  }

  // ── Waits with custom timeout ───────────────────────────────────

  /** Waits until the mobile element is visible within N seconds. */
  @Then("I wait until mobile element {string} is visible within {int} seconds")
  public void waitUntilMobileElementVisibleWithin(
      String locatorKey, int seconds) {
    mobileKeywords.waitUntilElementVisible(
        PropertiesLoader.get(locatorKey), seconds);
  }

  /** Waits until the mobile element is not visible within N seconds. */
  @Then("I wait until mobile element {string} is not visible within {int} seconds")
  public void waitUntilMobileElementNotVisibleWithin(
      String locatorKey, int seconds) {
    mobileKeywords.waitUntilElementNotVisible(
        PropertiesLoader.get(locatorKey), seconds);
  }

  /** Waits until the mobile element is enabled within N seconds. */
  @Then("I wait until mobile element {string} is enabled within {int} seconds")
  public void waitUntilMobileElementEnabledWithin(
      String locatorKey, int seconds) {
    mobileKeywords.waitUntilElementEnabled(
        PropertiesLoader.get(locatorKey), seconds);
  }

  /** Waits until the mobile element contains text within N seconds. */
  @Then("I wait until mobile element {string} contains text {string} within {int} seconds")
  public void waitUntilMobileElementContainsTextWithin(
      String locatorKey, String text, int seconds) {
    mobileKeywords.waitUntilElementContains(
        PropertiesLoader.get(locatorKey), text, seconds);
  }

  // ── Context switching ───────────────────────────────────────────

  /** Switches to the named context (NATIVE_APP, WEBVIEW_*). */
  @When("I switch to context {string}")
  public void switchToContext(String contextName) {
    mobileKeywords.switchToContext(contextName);
  }

  /** Gets available contexts and saves to a variable. */
  @When("I get contexts then save to {string}")
  public void getContextsAndSave(String variableName) {
    Common.saveVariable(variableName,
        mobileKeywords.getContexts());
  }

  // ── Private helpers ─────────────────────────────────────────────

  private UiAutomator2Options buildAndroidOptions(
      String profile) {
    UiAutomator2Options opts = new UiAutomator2Options();
    opts.setUdid(
        PropertiesLoader.get(profile + ".udid"));
    setIfPresent(opts, profile, "platformVersion");
    setIfPresent(opts, profile, "automationName");

    String appPackage = getOptional(
        profile + ".appPackage");
    if (appPackage != null) {
      opts.setCapability("appPackage", appPackage);
    }
    String appActivity = getOptional(
        profile + ".appActivity");
    if (appActivity != null) {
      opts.setCapability("appActivity", appActivity);
    }
    String app = getOptional(profile + ".app");
    if (app != null) {
      opts.setApp(app);
    }
    opts.setCapability("autoGrantPermissions", true);
    opts.setCapability(
        "uiautomator2ServerLaunchTimeout", 60000);
    return opts;
  }

  private XCUITestOptions buildIosOptions(String profile) {
    XCUITestOptions opts = new XCUITestOptions();
    opts.setUdid(
        PropertiesLoader.get(profile + ".udid"));
    setIfPresent(opts, profile, "platformVersion");
    setIfPresent(opts, profile, "automationName");

    String bundleId = getOptional(
        profile + ".bundleId");
    if (bundleId != null) {
      opts.setCapability("bundleId", bundleId);
    }
    String app = getOptional(profile + ".app");
    if (app != null) {
      opts.setApp(app);
    }
    return opts;
  }

  private void setIfPresent(
      org.openqa.selenium.MutableCapabilities opts,
      String profile, String key) {
    String value = getOptional(profile + "." + key);
    if (value != null) {
      opts.setCapability(key, value);
    }
  }

  private String getOptional(String key) {
    try {
      return PropertiesLoader.get(key);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
