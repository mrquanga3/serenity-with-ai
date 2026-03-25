package com.mrquanga3.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scans {@code .properties} files under the {@code properties/} classpath
 * directory and merges them into a single lookup map.
 *
 * <p><b>Environment support:</b> files named {@code environment.properties}
 * are only loaded from the active environment folder
 * (e.g.&nbsp;{@code properties/SIT/environment.properties}).
 * All other property files (locators, etc.) are loaded unconditionally.
 *
 * <p>The active environment is resolved in order:
 * <ol>
 *   <li>System property {@code -Denv=UAT}</li>
 *   <li>{@code env} key in {@code serenity.conf}</li>
 *   <li>Default: {@code SIT}</li>
 * </ol>
 *
 * <p>Folder structure:
 * <pre>
 * src/test/resources/
 *   properties/
 *     SIT/environment.properties
 *     UAT/environment.properties
 *     login/login.properties
 * </pre>
 *
 * <p>Usage: {@code PropertiesLoader.get("username.input")}
 */
public final class PropertiesLoader {

  private static final Logger LOG =
      LoggerFactory.getLogger(PropertiesLoader.class);

  private static final String PROPERTIES_ROOT = "properties";
  private static final String ENV_FILE_NAME = "environment.properties";
  private static final String DEFAULT_ENV = "SIT";
  private static final Properties ALL = loadAll();

  private PropertiesLoader() {
  }

  /**
   * Returns the property value for the given key.
   *
   * @param key property key, e.g. {@code username.input} or {@code urlAdmin}
   * @return the value (never {@code null})
   * @throws IllegalArgumentException if the key is absent
   */
  public static String get(String key) {
    String value = ALL.getProperty(key);
    if (value == null) {
      throw new IllegalArgumentException(
          "Key not found in any properties file under '"
              + PROPERTIES_ROOT + "/': " + key);
    }
    return value;
  }

  /** Returns the active environment name (e.g. {@code SIT}). */
  public static String activeEnvironment() {
    return resolveEnvironment();
  }

  // ── internals ─────────────────────────────────────────────────────

  private static Properties loadAll() {
    String env = resolveEnvironment();
    String envPath = PROPERTIES_ROOT + "/" + env + "/" + ENV_FILE_NAME;

    if (LOG.isInfoEnabled()) {
      LOG.info("Active environment: {} — loading {}", env, envPath);
    }

    Properties merged = new Properties();
    try (ScanResult scan = new ClassGraph()
        .acceptPaths(PROPERTIES_ROOT).scan();
         ResourceList all =
             scan.getResourcesWithExtension("properties");
         ResourceList filtered = all.filter(
             r -> !isEnvFile(r.getPath())
                 || r.getPath().equals(envPath))) {
      filtered.forEachInputStream((resource, inputStream) -> {
        try {
          merged.load(inputStream);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
    return merged;
  }

  /**
   * Returns {@code true} if the resource is an {@code environment.properties}
   * file inside an environment subdirectory.
   */
  private static boolean isEnvFile(String path) {
    return path.endsWith("/" + ENV_FILE_NAME)
        && path.startsWith(PROPERTIES_ROOT + "/");
  }

  private static String resolveEnvironment() {
    String env = System.getProperty("env");
    if (env != null && !env.isEmpty()) {
      return env;
    }
    try {
      Config config = ConfigFactory.parseResources("serenity.conf");
      if (config.hasPath("env")) {
        return config.getString("env");
      }
    } catch (RuntimeException e) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Could not read env from serenity.conf: {}",
            e.getMessage());
      }
    }
    return DEFAULT_ENV;
  }
}
