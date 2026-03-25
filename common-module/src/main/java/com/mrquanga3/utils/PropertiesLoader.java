package com.mrquanga3.utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.util.Properties;

/**
 * Scans every {@code .properties} file found under the {@code properties/}
 * directory on the classpath and merges them into a single lookup map.
 *
 * <p>Folder structure (add sub-folders freely — all are picked up automatically):
 * <pre>
 * src/test/resources/
 *   properties/
 *     login/login.properties
 *     dashboard/dashboard.properties
 *     ...
 * </pre>
 *
 * <p>Usage: {@code PropertiesLoader.get("username.input")}
 */
public final class PropertiesLoader {

  private static final String PROPERTIES_ROOT = "properties";
  private static final Properties ALL = loadAll();

  private PropertiesLoader() {
  }

  /**
   * Returns the locator value for the given key.
   *
   * @param key property key, e.g. {@code username.input}
   * @return the locator string (never {@code null})
   * @throws IllegalArgumentException if the key is absent from all properties files
   */
  public static String get(String key) {
    String value = ALL.getProperty(key);
    if (value == null) {
      throw new IllegalArgumentException(
          "Locator key not found in any properties file under '"
              + PROPERTIES_ROOT + "/': " + key);
    }
    return value;
  }

  private static Properties loadAll() {
    Properties merged = new Properties();
    try (ScanResult scan = new ClassGraph().acceptPaths(PROPERTIES_ROOT).scan();
         ResourceList resources = scan.getResourcesWithExtension("properties")) {
      resources.forEachInputStream(
          (resource, inputStream) -> {
            try {
              merged.load(inputStream);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    }
    return merged;
  }
}
