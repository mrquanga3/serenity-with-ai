package com.mrquanga3.steps;

import com.mrquanga3.common.Common;
import com.mrquanga3.keywords.DbKeywords;
import com.mrquanga3.utils.PropertiesLoader;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;

/**
 * Generic Cucumber step definitions for database testing via JDBC.
 *
 * <p>Connection parameters can be resolved from
 * {@link PropertiesLoader} using property keys, or passed directly.
 * "Get … then save to" steps store results in
 * {@link Common#globalVariables()}.
 */
@SuppressWarnings("PMD.GodClass")
public class CommonDbSteps {

  @Steps
  DbKeywords dbKeywords;

  // ── Lifecycle hooks ────────────────────────────────────────

  /** Cleans up DB session state after each scenario. */
  @After
  public void cleanUpDbSession() {
    DbKeywords.resetState();
  }

  // ── Connection management ─────────────────────────────────

  /** Connects to a database with direct JDBC URL and creds. */
  @Given("I connect to database {string} with user {string} and password {string}")
  public void connectToDatabase(
      String jdbcUrl, String username, String password) {
    dbKeywords.connect(jdbcUrl, username, password);
  }

  /** Connects to a database using properties keys. */
  @Given("I connect to database from {string} with user from {string} and password from {string}")
  public void connectFromProperties(
      String urlKey, String userKey, String passKey) {
    dbKeywords.connect(
        PropertiesLoader.get(urlKey),
        PropertiesLoader.get(userKey),
        PropertiesLoader.get(passKey));
  }

  /** Connects to a database with URL only (no auth). */
  @Given("I connect to database {string}")
  public void connectToDatabaseNoAuth(String jdbcUrl) {
    dbKeywords.connect(jdbcUrl);
  }

  /** Connects using a URL property key (no auth). */
  @Given("I connect to database from {string}")
  public void connectFromPropertyNoAuth(String urlKey) {
    dbKeywords.connect(PropertiesLoader.get(urlKey));
  }

  /** Disconnects from the current database. */
  @Given("I disconnect from database")
  public void disconnectFromDatabase() {
    dbKeywords.disconnect();
  }

  // ── Execute ───────────────────────────────────────────────

  /** Executes a SELECT query (inline). */
  @When("I execute DB query {string}")
  public void executeQuery(String sql) {
    dbKeywords.executeQuery(sql);
  }

  /** Executes a SELECT query (DocString / multi-line). */
  @When("I execute DB query")
  public void executeQueryDocString(String sql) {
    dbKeywords.executeQuery(sql);
  }

  /** Executes an INSERT, UPDATE, or DELETE (inline). */
  @When("I execute DB update {string}")
  public void executeUpdate(String sql) {
    dbKeywords.executeUpdate(sql);
  }

  /** Executes an INSERT, UPDATE, or DELETE (DocString). */
  @When("I execute DB update")
  public void executeUpdateDocString(String sql) {
    dbKeywords.executeUpdate(sql);
  }

  // ── Result getters (save to variable) ─────────────────────

  /** Gets the row count and saves to a variable. */
  @When("I get DB row count then save to {string}")
  public void getRowCountSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, dbKeywords.getRowCount());
  }

  /** Gets the update count and saves to a variable. */
  @When("I get DB update count then save to {string}")
  public void getUpdateCountSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, dbKeywords.getUpdateCount());
  }

  /** Gets a cell value by row/column index and saves. */
  @When("I get DB cell at row {int} column {int} then save to {string}")
  public void getCellSaveTo(
      int row, int column, String variableKey) {
    Common.saveVariable(
        variableKey, dbKeywords.getCellValue(row, column));
  }

  /** Gets a cell value by row index and column name. */
  @When("I get DB cell at row {int} column {string} then save to {string}")
  public void getCellByNameSaveTo(
      int row, String columnName, String variableKey) {
    Common.saveVariable(
        variableKey,
        dbKeywords.getCellValueByName(row, columnName));
  }

  /** Gets all values in a column by index. */
  @When("I get DB column {int} values then save to {string}")
  public void getColumnValuesSaveTo(
      int column, String variableKey) {
    Common.saveVariable(
        variableKey, dbKeywords.getColumnValues(column));
  }

  /** Gets all values in a column by name. */
  @When("I get DB column {string} values then save to {string}")
  public void getColumnValuesByNameSaveTo(
      String columnName, String variableKey) {
    Common.saveVariable(
        variableKey,
        dbKeywords.getColumnValuesByName(columnName));
  }

  /** Gets the column names from the last query result. */
  @When("I get DB column names then save to {string}")
  public void getColumnNamesSaveTo(String variableKey) {
    Common.saveVariable(
        variableKey, dbKeywords.getColumnNames());
  }

  // ── Result assertions ─────────────────────────────────────

  /** Asserts the query row count equals expected. */
  @Then("the DB row count should be {int}")
  public void verifyRowCount(int expected) {
    dbKeywords.verifyRowCount(expected);
  }

  /** Asserts the update count equals expected. */
  @Then("the DB update count should be {int}")
  public void verifyUpdateCount(int expected) {
    dbKeywords.verifyUpdateCount(expected);
  }

  /** Asserts the query returned at least one row. */
  @Then("the DB query should return rows")
  public void verifyQueryReturnsRows() {
    dbKeywords.verifyQueryReturnsRows();
  }

  /** Asserts the query returned no rows. */
  @Then("the DB query should return no rows")
  public void verifyQueryReturnsNoRows() {
    dbKeywords.verifyQueryReturnsNoRows();
  }

  /** Asserts a cell value equals expected (by index). */
  @Then("the DB cell at row {int} column {int} should be {string}")
  public void verifyCellEquals(
      int row, int column, String expected) {
    dbKeywords.verifyCellEquals(row, column, expected);
  }

  /** Asserts a cell value contains text (by index). */
  @Then("the DB cell at row {int} column {int} should contain {string}")
  public void verifyCellContains(
      int row, int column, String expected) {
    dbKeywords.verifyCellContains(row, column, expected);
  }

  /** Asserts a cell value equals expected (by column name). */
  @Then("the DB cell at row {int} column {string} should be {string}")
  public void verifyCellByNameEquals(
      int row, String columnName, String expected) {
    dbKeywords.verifyCellByNameEquals(
        row, columnName, expected);
  }

  /** Asserts a cell value contains text (by column name). */
  @Then("the DB cell at row {int} column {string} should contain {string}")
  public void verifyCellByNameContains(
      int row, String columnName, String expected) {
    dbKeywords.verifyCellByNameContains(
        row, columnName, expected);
  }
}
