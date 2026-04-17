package com.mrquanga3.steps;

import com.mrquanga3.common.Common;
import com.mrquanga3.keywords.ApiKeywords;
import com.mrquanga3.utils.PropertiesLoader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

/**
 * Generic Cucumber step definitions for API (REST) testing.
 *
 * <p>Endpoint parameters can be resolved from
 * {@link PropertiesLoader} using the "endpoint" suffix pattern,
 * or passed as direct URLs.
 * "Get … then save to" steps store results in
 * {@link Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class CommonApiSteps {

  @Steps
  ApiKeywords apiKeywords;

  // ── Lifecycle hooks ────────────────────────────────────────

  /** Cleans up API session state after each scenario. */
  @After
  public void cleanUpApiSession() {
    ApiKeywords.resetState();
  }

  // ── Session management ─────────────────────────────────────

  /** Sets API base URL directly. */
  @Given("I set API base URL to {string}")
  public void setBaseUrl(String url) {
    apiKeywords.setBaseUrl(url);
  }

  /** Sets API base URL from a properties key. */
  @Given("I set API base URL from {string}")
  public void setBaseUrlFromProperty(String urlKey) {
    apiKeywords.setBaseUrl(PropertiesLoader.get(urlKey));
  }

  // ── Headers ────────────────────────────────────────────────

  /** Adds or updates an API request header. */
  @When("I set API header {string} to {string}")
  public void setApiHeader(String name, String value) {
    apiKeywords.setHeader(name, value);
  }

  /** Removes an API request header. */
  @When("I remove API header {string}")
  public void removeApiHeader(String name) {
    apiKeywords.removeHeader(name);
  }

  // ── Authentication ─────────────────────────────────────────

  /** Sets HTTP Basic auth with direct credentials. */
  @When("I set API basic auth with {string} and {string}")
  public void setApiBasicAuth(
      String username, String password) {
    apiKeywords.setBasicAuth(username, password);
  }

  /** Sets HTTP Basic auth from properties keys. */
  @When("I set API basic auth from {string} and {string}")
  public void setApiBasicAuthFromProperties(
      String usernameKey, String passwordKey) {
    apiKeywords.setBasicAuth(
        PropertiesLoader.get(usernameKey),
        PropertiesLoader.get(passwordKey));
  }

  /** Sets Bearer token directly. */
  @When("I set API bearer token {string}")
  public void setApiBearerToken(String token) {
    apiKeywords.setBearerToken(token);
  }

  /** Sets Bearer token from a saved variable. */
  @When("I set API bearer token from variable {string}")
  public void setApiBearerTokenFromVariable(
      String variableKey) {
    apiKeywords.setBearerToken(
        Common.getVariable(variableKey));
  }

  // ── Request body ───────────────────────────────────────────

  /** Sets request body from an inline string. */
  @When("I set API request body {string}")
  public void setApiRequestBody(String body) {
    apiKeywords.setRequestBody(body);
  }

  /** Sets request body from a DocString (multi-line). */
  @When("I set API request body to")
  public void setApiRequestBodyDocString(String body) {
    apiKeywords.setRequestBody(body);
  }

  /** Sets request body from a saved variable. */
  @When("I set API request body from variable {string}")
  public void setApiRequestBodyFromVariable(
      String variableKey) {
    apiKeywords.setRequestBody(
        Common.getVariable(variableKey));
  }

  // ── Query parameters ──────────────────────────────────────

  /** Adds or updates a query parameter. */
  @When("I set API query param {string} to {string}")
  public void setApiQueryParam(String name, String value) {
    apiKeywords.setQueryParam(name, value);
  }

  /** Removes a query parameter. */
  @When("I remove API query param {string}")
  public void removeApiQueryParam(String name) {
    apiKeywords.removeQueryParam(name);
  }

  // ── Send requests (direct URL) ─────────────────────────────

  /** Sends a GET request to a direct URL/path. */
  @When("I send a GET request to {string}")
  public void sendGetRequest(String url) {
    apiKeywords.sendGet(url);
  }

  /** Sends a POST request to a direct URL/path. */
  @When("I send a POST request to {string}")
  public void sendPostRequest(String url) {
    apiKeywords.sendPost(url);
  }

  /** Sends a PUT request to a direct URL/path. */
  @When("I send a PUT request to {string}")
  public void sendPutRequest(String url) {
    apiKeywords.sendPut(url);
  }

  /** Sends a PATCH request to a direct URL/path. */
  @When("I send a PATCH request to {string}")
  public void sendPatchRequest(String url) {
    apiKeywords.sendPatch(url);
  }

  /** Sends a DELETE request to a direct URL/path. */
  @When("I send a DELETE request to {string}")
  public void sendDeleteRequest(String url) {
    apiKeywords.sendDelete(url);
  }

  // ── Send requests (from properties) ────────────────────────

  /** Sends a GET request to an endpoint from properties. */
  @When("I send a GET request to the {string} endpoint")
  public void sendGetToEndpoint(String endpointKey) {
    apiKeywords.sendGet(
        PropertiesLoader.get(endpointKey));
  }

  /** Sends a POST request to an endpoint from properties. */
  @When("I send a POST request to the {string} endpoint")
  public void sendPostToEndpoint(String endpointKey) {
    apiKeywords.sendPost(
        PropertiesLoader.get(endpointKey));
  }

  /** Sends a PUT request to an endpoint from properties. */
  @When("I send a PUT request to the {string} endpoint")
  public void sendPutToEndpoint(String endpointKey) {
    apiKeywords.sendPut(
        PropertiesLoader.get(endpointKey));
  }

  /** Sends a PATCH request to an endpoint from properties. */
  @When("I send a PATCH request to the {string} endpoint")
  public void sendPatchToEndpoint(String endpointKey) {
    apiKeywords.sendPatch(
        PropertiesLoader.get(endpointKey));
  }

  /** Sends a DELETE request to an endpoint from properties. */
  @When("I send a DELETE request to the {string} endpoint")
  public void sendDeleteToEndpoint(String endpointKey) {
    apiKeywords.sendDelete(
        PropertiesLoader.get(endpointKey));
  }

  // ── Response getters (save to variable) ────────────────────

  /** Gets response body and saves to a variable. */
  @When("I get API response body then save to {string}")
  public void getResponseBodySaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, apiKeywords.getResponseBody());
  }

  /** Gets response status code and saves to a variable. */
  @When("I get API response status then save to {string}")
  public void getResponseStatusSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, apiKeywords.getResponseStatusCode());
  }

  /** Gets a response header value and saves to a variable. */
  @When("I get API response header {string} then save to {string}")
  public void getResponseHeaderSaveTo(
      String headerName, String variableKey) {
    Common.saveVariable(
        variableKey,
        apiKeywords.getResponseHeader(headerName));
  }

  /** Extracts a JSON path value and saves to a variable. */
  @When("I get API JSON path {string} then save to {string}")
  public void getJsonPathSaveTo(
      String jsonPath, String variableKey) {
    Common.saveVariable(
        variableKey, apiKeywords.getJsonPathValue(jsonPath));
  }

  // ── Response assertions ────────────────────────────────────

  /** Asserts the response status code. */
  @Then("the API response status code should be {int}")
  public void verifyStatusCode(int expectedStatus) {
    apiKeywords.verifyStatusCode(expectedStatus);
  }

  /** Asserts the response body contains expected text. */
  @Then("the API response body should contain {string}")
  public void verifyResponseContains(String expected) {
    apiKeywords.verifyResponseContains(expected);
  }

  /** Asserts the response body does NOT contain text. */
  @Then("the API response body should not contain {string}")
  public void verifyResponseNotContains(String expected) {
    apiKeywords.verifyResponseNotContains(expected);
  }

  /** Asserts a JSON path value equals expected exactly. */
  @Then("the API JSON path {string} should be {string}")
  public void verifyJsonPathEquals(
      String jsonPath, String expected) {
    apiKeywords.verifyJsonPathEquals(jsonPath, expected);
  }

  /** Asserts a JSON path value contains expected text. */
  @Then("the API JSON path {string} should contain {string}")
  public void verifyJsonPathContains(
      String jsonPath, String expected) {
    apiKeywords.verifyJsonPathContains(jsonPath, expected);
  }

  /** Asserts the response Content-Type is application/json. */
  @Then("the API response should be JSON")
  public void verifyResponseIsJson() {
    apiKeywords.verifyResponseIsJson();
  }

  /** Validates response body against a JSON Schema file on the classpath. */
  @Then("the API response should match JSON schema {string}")
  public void verifyResponseMatchesJsonSchema(String schemaPath) {
    apiKeywords.verifyResponseMatchesJsonSchema(schemaPath);
  }

  /** Validates response JSON object fields using Karate-style type markers. */
  @Then("the API response should match schema")
  public void verifyResponseSchema(DataTable dataTable) {
    apiKeywords.verifyResponseSchema(dataTable.asMaps());
  }

  /** Validates every item in a root JSON array using Karate-style type markers. */
  @Then("each item in the API response array should match schema")
  public void verifyArrayItemSchema(DataTable dataTable) {
    apiKeywords.verifyArrayItemSchema(dataTable.asMaps());
  }
}
