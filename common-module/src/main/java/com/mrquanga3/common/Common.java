package com.mrquanga3.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Global shared state for the test session.
 *
 * <p>{@link #globalVariables} stores values captured by "Get … then save to"
 * steps so they can be referenced later in the same scenario.
 * Call {@link #clearAllVariables()} in an {@code @After} hook to reset
 * between scenarios.
 */
public final class Common {

  private static final Map<String, String> GLOBAL_VARIABLES =
      new HashMap<>();

  private Common() {
  }

  /** Returns the mutable global variables map. */
  public static Map<String, String> globalVariables() {
    return GLOBAL_VARIABLES;
  }

  /** Stores a value under the given key. */
  public static void saveVariable(String key, String value) {
    GLOBAL_VARIABLES.put(key, value);
  }

  /**
   * Retrieves a previously saved value.
   *
   * @throws IllegalArgumentException if the key does not exist
   */
  public static String getVariable(String key) {
    if (!GLOBAL_VARIABLES.containsKey(key)) {
      throw new IllegalArgumentException(
          "Global variable not found: " + key);
    }
    return GLOBAL_VARIABLES.get(key);
  }

  /** Removes all saved variables (call between scenarios). */
  public static void clearAllVariables() {
    GLOBAL_VARIABLES.clear();
  }
}
