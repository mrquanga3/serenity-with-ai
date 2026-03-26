package com.mrquanga3.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages multiple named sessions (actors) within a single scenario.
 *
 * <p>Each actor gets its own {@link WebDriver} instance — either a
 * desktop browser or a mobile device (via Appium). Use
 * {@link #switchTo(String)} to change the active actor, and
 * {@link #currentDriver()} to get the active driver.
 *
 * <p>Call {@link #closeAll()} after each scenario to clean up all sessions.
 */
public final class ActorManager {

  private static final Logger LOG =
      LoggerFactory.getLogger(ActorManager.class);

  /** Distinguishes web-browser actors from mobile-device actors. */
  public enum ActorType { WEB, MOBILE }

  private static final Map<String, WebDriver> DRIVERS =
      new LinkedHashMap<>();
  private static final Map<String, ActorType> ACTOR_TYPES =
      new LinkedHashMap<>();
  private static String currentActor;

  private ActorManager() {
  }

  // ── Web browser actors ───────────────────────────────────────────

  /**
   * Opens a new browser for the given actor and makes it active.
   *
   * @param actorName unique name for this actor (e.g. "Alice")
   * @param browserType browser to launch: {@code chrome} or {@code firefox}
   */
  public static void openBrowser(
      String actorName, String browserType) {
    guardDuplicate(actorName);
    WebDriver driver = createBrowserDriver(browserType);
    DRIVERS.put(actorName, driver);
    ACTOR_TYPES.put(actorName, ActorType.WEB);
    currentActor = actorName;
    LOG.info("Opened {} browser for actor '{}'",
        browserType, actorName);
  }

  // ── Mobile device actors ─────────────────────────────────────────

  /**
   * Opens a mobile device session for the given actor via Appium.
   *
   * @param actorName    unique name (e.g. "MobileUser")
   * @param platformName {@code android} or {@code ios}
   * @param appiumUrl    Appium server URL (e.g. http://127.0.0.1:4723)
   * @param capabilities desired capabilities / options
   */
  public static void openMobileDevice(
      String actorName,
      String platformName,
      URL appiumUrl,
      Capabilities capabilities) {
    guardDuplicate(actorName);
    AppiumDriver driver =
        createMobileDriver(platformName, appiumUrl, capabilities);
    DRIVERS.put(actorName, driver);
    ACTOR_TYPES.put(actorName, ActorType.MOBILE);
    currentActor = actorName;
    LOG.info("Opened {} mobile device for actor '{}'",
        platformName, actorName);
  }

  // ── Switching & queries ──────────────────────────────────────────

  /**
   * Switches the active session to the named actor.
   *
   * @param actorName the actor to switch to (must already exist)
   */
  public static void switchTo(String actorName) {
    if (!DRIVERS.containsKey(actorName)) {
      throw new IllegalArgumentException(
          "No session open for actor: " + actorName);
    }
    currentActor = actorName;
  }

  /**
   * Returns the active actor's {@link WebDriver}, or {@code null}
   * if no actor is active.
   */
  public static WebDriver currentDriver() {
    if (currentActor == null) {
      return null;
    }
    return DRIVERS.get(currentActor);
  }

  /**
   * Returns the active actor's driver cast to {@link AppiumDriver}.
   *
   * @throws IllegalStateException if the current actor is not mobile
   */
  public static AppiumDriver currentMobileDriver() {
    WebDriver driver = currentDriver();
    if (driver instanceof AppiumDriver appiumDriver) {
      return appiumDriver;
    }
    throw new IllegalStateException(
        "Current actor is not a mobile device");
  }

  /** Returns {@code true} if at least one actor is active. */
  public static boolean hasActiveActor() {
    return currentActor != null
        && DRIVERS.containsKey(currentActor);
  }

  /**
   * Returns the {@link ActorType} of the current actor,
   * or {@code null} if none is active.
   */
  public static ActorType currentActorType() {
    if (currentActor == null) {
      return null;
    }
    return ACTOR_TYPES.get(currentActor);
  }

  // ── Cleanup ──────────────────────────────────────────────────────

  /** Quits every actor's session and resets all state. */
  public static void closeAll() {
    DRIVERS.values().forEach(ActorManager::quitQuietly);
    DRIVERS.clear();
    ACTOR_TYPES.clear();
    currentActor = null;
  }

  // ── Private helpers ──────────────────────────────────────────────

  private static void guardDuplicate(String actorName) {
    if (DRIVERS.containsKey(actorName)) {
      throw new IllegalArgumentException(
          "Actor already has a session: " + actorName);
    }
  }

  private static WebDriver createBrowserDriver(String browserType) {
    String type = browserType.trim().toLowerCase(Locale.ROOT);
    if ("chrome".equals(type)) {
      return new ChromeDriver();
    }
    if ("firefox".equals(type)) {
      return new FirefoxDriver();
    }
    throw new IllegalArgumentException(
        "Unsupported browser: " + browserType);
  }

  private static AppiumDriver createMobileDriver(
      String platformName,
      URL appiumUrl,
      Capabilities capabilities) {
    String platform = platformName.trim().toLowerCase(Locale.ROOT);
    if ("android".equals(platform)) {
      return new AndroidDriver(appiumUrl, capabilities);
    }
    if ("ios".equals(platform)) {
      return new IOSDriver(appiumUrl, capabilities);
    }
    throw new IllegalArgumentException(
        "Unsupported mobile platform: " + platformName);
  }

  private static void quitQuietly(WebDriver driver) {
    try {
      driver.quit();
    } catch (WebDriverException e) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Failed to quit driver: {}", e.getMessage());
      }
    }
  }
}
