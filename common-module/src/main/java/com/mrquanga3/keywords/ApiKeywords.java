package com.mrquanga3.keywords;

import static org.assertj.core.api.Assertions.assertThat;

import com.mrquanga3.common.Common;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;

/**
 * Keyword-driven library of reusable API actions modelled after
 * Robot Framework RequestsLibrary.
 *
 * <p>Manages per-thread state (headers, base URL, auth, body)
 * so that parallel scenario execution is thread-safe.
 *
 * <p>"Get" methods return {@link String} so callers can save the
 * result into {@link com.mrquanga3.common.Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class ApiKeywords {

  private static final ThreadLocal<String> BASE_URL =
      ThreadLocal.withInitial(() -> "");
  private static final ThreadLocal<Map<String, String>> HEADERS =
      ThreadLocal.withInitial(HashMap::new);
  private static final ThreadLocal<Map<String, String>>
      QUERY_PARAMS = ThreadLocal.withInitial(HashMap::new);
  private static final ThreadLocal<String> REQUEST_BODY =
      new ThreadLocal<>();
  private static final ThreadLocal<Response> LAST_RESPONSE =
      new ThreadLocal<>();

  // ── Session management ─────────────────────────────────────

  /** Sets the base URL prepended to all endpoint paths. */
  @Step("Set base URL to '{0}'")
  public void setBaseUrl(String url) {
    BASE_URL.set(url);
  }

  /** Clears all API session state (headers, body, response). */
  @Step("Clear API session")
  public void clearSession() {
    BASE_URL.remove();
    HEADERS.remove();
    QUERY_PARAMS.remove();
    REQUEST_BODY.remove();
    LAST_RESPONSE.remove();
  }

  /** Static cleanup for @After hooks (no Serenity reporting). */
  public static void resetState() {
    BASE_URL.remove();
    HEADERS.remove();
    QUERY_PARAMS.remove();
    REQUEST_BODY.remove();
    LAST_RESPONSE.remove();
  }

  // ── Headers ────────────────────────────────────────────────

  /** Adds or updates a request header. */
  @Step("Set header '{0}' = '{1}'")
  public void setHeader(String name, String value) {
    HEADERS.get().put(name, value);
  }

  /** Removes a request header. */
  @Step("Remove header '{0}'")
  public void removeHeader(String name) {
    HEADERS.get().remove(name);
  }

  // ── Authentication ─────────────────────────────────────────

  /** Sets HTTP Basic Authentication header. */
  @Step("Set basic auth for '{0}'")
  public void setBasicAuth(String username, String password) {
    String credentials = username + ":" + password;
    String encoded = Base64.getEncoder().encodeToString(
        credentials.getBytes(StandardCharsets.UTF_8));
    HEADERS.get().put("Authorization", "Basic " + encoded);
  }

  /** Sets Bearer token Authorization header. */
  @Step("Set bearer token")
  public void setBearerToken(String token) {
    HEADERS.get().put(
        "Authorization", "Bearer " + token);
  }

  // ── Request body ───────────────────────────────────────────

  /** Sets the request body for the next request. */
  @Step("Set request body")
  public void setRequestBody(String body) {
    REQUEST_BODY.set(body);
  }

  // ── Query parameters ──────────────────────────────────────

  /** Adds or updates a query parameter. */
  @Step("Set query param '{0}' = '{1}'")
  public void setQueryParam(String name, String value) {
    QUERY_PARAMS.get().put(name, value);
  }

  /** Removes a query parameter. */
  @Step("Remove query param '{0}'")
  public void removeQueryParam(String name) {
    QUERY_PARAMS.get().remove(name);
  }

  // ── HTTP methods ───────────────────────────────────────────

  /** Sends a GET request to the endpoint. */
  @Step("Send GET request to '{0}'")
  public void sendGet(String endpoint) {
    sendRequest("GET", endpoint);
  }

  /** Sends a POST request to the endpoint. */
  @Step("Send POST request to '{0}'")
  public void sendPost(String endpoint) {
    sendRequest("POST", endpoint);
  }

  /** Sends a PUT request to the endpoint. */
  @Step("Send PUT request to '{0}'")
  public void sendPut(String endpoint) {
    sendRequest("PUT", endpoint);
  }

  /** Sends a PATCH request to the endpoint. */
  @Step("Send PATCH request to '{0}'")
  public void sendPatch(String endpoint) {
    sendRequest("PATCH", endpoint);
  }

  /** Sends a DELETE request to the endpoint. */
  @Step("Send DELETE request to '{0}'")
  public void sendDelete(String endpoint) {
    sendRequest("DELETE", endpoint);
  }

  // ── Response getters ───────────────────────────────────────

  /** Returns the response body as a string. */
  @Step("Get response body")
  public String getResponseBody() {
    return lastResponse().body().asString();
  }

  /** Returns the response status code as a string. */
  @Step("Get response status code")
  public String getResponseStatusCode() {
    return String.valueOf(lastResponse().statusCode());
  }

  /** Returns the value of a response header. */
  @Step("Get response header '{0}'")
  public String getResponseHeader(String name) {
    String value = lastResponse().header(name);
    return value != null ? value : "";
  }

  /** Extracts a value from the JSON response by path. */
  @Step("Get JSON path '{0}'")
  public String getJsonPathValue(String jsonPath) {
    Object value = lastResponse().jsonPath().get(jsonPath);
    return value != null ? value.toString() : "";
  }

  // ── Response assertions ────────────────────────────────────

  /** Asserts the response status code equals expected. */
  @Step("Verify status code is {0}")
  public void verifyStatusCode(int expected) {
    assertThat(lastResponse().statusCode())
        .as("Response status code should be %d", expected)
        .isEqualTo(expected);
  }

  /** Asserts the response body contains the expected text. */
  @Step("Verify response contains '{0}'")
  public void verifyResponseContains(String expected) {
    String resolved = Common.resolveVariables(expected);
    assertThat(lastResponse().body().asString())
        .as("Response should contain '%s'", resolved)
        .contains(resolved);
  }

  /** Asserts the response body does NOT contain the text. */
  @Step("Verify response not contains '{0}'")
  public void verifyResponseNotContains(String expected) {
    String resolved = Common.resolveVariables(expected);
    assertThat(lastResponse().body().asString())
        .as("Response should not contain '%s'", resolved)
        .doesNotContain(resolved);
  }

  /** Asserts a JSON path value equals expected exactly. */
  @Step("Verify JSON path '{0}' equals '{1}'")
  public void verifyJsonPathEquals(
      String jsonPath, String expected) {
    String path = Common.resolveVariables(jsonPath);
    String exp = Common.resolveVariables(expected);
    Object actual = lastResponse().jsonPath().get(path);
    String actualStr = actual != null
        ? actual.toString() : "";
    assertThat(actualStr)
        .as("JSON path '%s' should equal '%s'", path, exp)
        .isEqualTo(exp);
  }

  /** Asserts a JSON path value contains expected text. */
  @Step("Verify JSON path '{0}' contains '{1}'")
  public void verifyJsonPathContains(
      String jsonPath, String expected) {
    String path = Common.resolveVariables(jsonPath);
    String exp = Common.resolveVariables(expected);
    Object actual = lastResponse().jsonPath().get(path);
    String actualStr = actual != null
        ? actual.toString() : "";
    assertThat(actualStr)
        .as("JSON path '%s' should contain '%s'", path, exp)
        .contains(exp);
  }

  // ── Private helpers ────────────────────────────────────────

  private void sendRequest(String method, String endpoint) {
    RequestSpecification spec = buildRequestSpec();
    final String url = Common.resolveVariables(
        resolveUrl(endpoint));

    Response response;
    if ("GET".equals(method)) {
      response = spec.get(url);
    } else if ("POST".equals(method)) {
      response = spec.post(url);
    } else if ("PUT".equals(method)) {
      response = spec.put(url);
    } else if ("PATCH".equals(method)) {
      response = spec.patch(url);
    } else if ("DELETE".equals(method)) {
      response = spec.delete(url);
    } else {
      throw new IllegalArgumentException(
          "Unsupported HTTP method: " + method);
    }

    LAST_RESPONSE.set(response);
    REQUEST_BODY.remove();
    QUERY_PARAMS.get().clear();
  }

  private RequestSpecification buildRequestSpec() {
    RequestSpecification spec = SerenityRest.given();

    Map<String, String> hdrs = HEADERS.get();
    if (!hdrs.isEmpty()) {
      Map<String, String> resolved = new HashMap<>();
      for (Map.Entry<String, String> e : hdrs.entrySet()) {
        resolved.put(
            e.getKey(), Common.resolveVariables(e.getValue()));
      }
      spec.headers(resolved);
    }

    Map<String, String> params = QUERY_PARAMS.get();
    if (!params.isEmpty()) {
      Map<String, String> resolved = new HashMap<>();
      for (Map.Entry<String, String> e : params.entrySet()) {
        resolved.put(
            e.getKey(), Common.resolveVariables(e.getValue()));
      }
      spec.queryParams(resolved);
    }

    String body = REQUEST_BODY.get();
    if (body != null && !body.isEmpty()) {
      if (!hdrs.containsKey("Content-Type")) {
        spec.contentType(ContentType.JSON);
      }
      spec.body(Common.resolveVariables(body));
    }

    return spec;
  }

  private String resolveUrl(String endpoint) {
    String base = BASE_URL.get();
    if (base == null || base.isEmpty()) {
      return endpoint;
    }
    String trimBase = base.endsWith("/")
        ? base.substring(0, base.length() - 1) : base;
    String trimEndpoint = endpoint.startsWith("/")
        ? endpoint : "/" + endpoint;
    return trimBase + trimEndpoint;
  }

  private Response lastResponse() {
    Response resp = LAST_RESPONSE.get();
    if (resp == null) {
      throw new IllegalStateException(
          "No API response available. Send a request first.");
    }
    return resp;
  }
}
