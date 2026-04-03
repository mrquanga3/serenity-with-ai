package com.mrquanga3.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Thread-safe global shared state for the test session.
 *
 * <p>{@link #globalVariables} stores values captured by "Get … then save to"
 * steps so they can be referenced later in the same scenario.
 * Call {@link #clearAllVariables()} in an {@code @After} hook to reset
 * between scenarios.
 *
 * <p>All state is stored per-thread via {@link ThreadLocal} so that
 * parallel scenario execution does not cause cross-thread interference.
 */
public final class Common {

  /** Matches {@code ${varName}} placeholders in strings. */
  private static final Pattern VAR_PATTERN =
      Pattern.compile("\\$\\{([^}]+)\\}");

  private static final ThreadLocal<Map<String, String>>
      GLOBAL_VARIABLES =
          ThreadLocal.withInitial(HashMap::new);

  private Common() {
  }

  /** Returns the mutable global variables map for this thread. */
  public static Map<String, String> globalVariables() {
    return GLOBAL_VARIABLES.get();
  }

  /** Stores a value under the given key. */
  public static void saveVariable(String key, String value) {
    GLOBAL_VARIABLES.get().put(key, value);
  }

  /**
   * Retrieves a previously saved value.
   *
   * @throws IllegalArgumentException if the key does not exist
   */
  public static String getVariable(String key) {
    if (!GLOBAL_VARIABLES.get().containsKey(key)) {
      throw new IllegalArgumentException(
          "Global variable not found: " + key);
    }
    return GLOBAL_VARIABLES.get().get(key);
  }

  /**
   * Replaces {@code ${varName}} placeholders in the given text
   * with values from {@link #globalVariables()}.
   *
   * <p>Returns the original text unchanged if it contains no
   * placeholders. Throws {@link IllegalArgumentException} if a
   * referenced variable does not exist.
   */
  public static String resolveVariables(String text) {
    if (text == null || !text.contains("${")) {
      return text;
    }
    Matcher matcher = VAR_PATTERN.matcher(text);
    StringBuilder result = new StringBuilder();
    while (matcher.find()) {
      String varName = matcher.group(1);
      String value = getVariable(varName);
      matcher.appendReplacement(
          result, Matcher.quoteReplacement(value));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  /** Removes all saved variables (call between scenarios). */
  public static void clearAllVariables() {
    GLOBAL_VARIABLES.get().clear();
  }
}
