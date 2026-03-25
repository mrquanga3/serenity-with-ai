package com.mrquanga3.utils;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages multiple named browser sessions (actors) within a single scenario.
 *
 * <p>Each actor gets its own {@link WebDriver} instance. Use
 * {@link #switchTo(String)} to change the active actor, and
 * {@link #currentDriver()} to get the active driver.
 *
 * <p>Call {@link #closeAll()} after each scenario to clean up all sessions.
 */
public final class ActorManager {

  private static final Logger LOG = LoggerFactory.getLogger(ActorManager.class);

  private static final Map<String, WebDriver> DRIVERS = new LinkedHashMap<>();
  private static String currentActor;

  private ActorManager() {
  }

  /**
   * Opens a new browser for the given actor and makes it the active session.
   *
   * @param actorName unique name for this actor (e.g. "Alice")
   * @param browserType browser to launch: {@code chrome} or {@code firefox}
   */
  public static void openBrowser(String actorName, String browserType) {
    if (DRIVERS.containsKey(actorName)) {
      throw new IllegalArgumentException(
          "Actor already has a browser: " + actorName);
    }
    WebDriver driver = createDriver(browserType);
    DRIVERS.put(actorName, driver);
    currentActor = actorName;
    LOG.info("Opened {} browser for actor '{}'", browserType, actorName);
  }

  /**
   * Switches the active session to the named actor.
   *
   * @param actorName the actor to switch to (must already have a browser)
   */
  public static void switchTo(String actorName) {
    if (!DRIVERS.containsKey(actorName)) {
      throw new IllegalArgumentException(
          "No browser open for actor: " + actorName);
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

  /** Returns {@code true} if at least one actor has an open browser. */
  public static boolean hasActiveActor() {
    return currentActor != null && DRIVERS.containsKey(currentActor);
  }

  /** Quits every actor's browser and resets all state. */
  public static void closeAll() {
    DRIVERS.values().forEach(ActorManager::quitQuietly);
    DRIVERS.clear();
    currentActor = null;
  }

  private static WebDriver createDriver(String browserType) {
    String type = browserType.trim().toLowerCase(Locale.ROOT);
    if ("chrome".equals(type)) {
      return new ChromeDriver();
    }
    if ("firefox".equals(type)) {
      return new FirefoxDriver();
    }
    throw new IllegalArgumentException("Unsupported browser: " + browserType);
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
