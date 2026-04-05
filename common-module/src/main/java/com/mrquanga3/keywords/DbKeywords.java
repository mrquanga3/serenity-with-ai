package com.mrquanga3.keywords;

import com.mrquanga3.common.Common;
import net.serenitybdd.annotations.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Keyword-driven library of reusable database actions using JDBC.
 *
 * <p>Manages per-thread state (connection, result set metadata)
 * so that parallel scenario execution is thread-safe.
 *
 * <p>"Get" methods return {@link String} so callers can save the
 * result into {@link com.mrquanga3.common.Common#globalVariables()}.
 *
 * <p>Database drivers (MySQL, PostgreSQL, etc.) must be on the
 * classpath of the consuming module.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.CloseResource"})
public class DbKeywords {

  private static final Logger LOG =
      LoggerFactory.getLogger(DbKeywords.class);

  private static final ThreadLocal<Connection> CONNECTION =
      new ThreadLocal<>();
  private static final ThreadLocal<List<List<String>>> RESULT_ROWS =
      ThreadLocal.withInitial(ArrayList::new);
  private static final ThreadLocal<List<String>> COLUMN_NAMES =
      ThreadLocal.withInitial(ArrayList::new);
  private static final ThreadLocal<Integer> UPDATE_COUNT =
      ThreadLocal.withInitial(() -> 0);

  // ── Connection management ───────────────────────────────────

  /** Opens a JDBC connection with the given URL, user, password. */
  @Step("Connect to database '{0}' as '{1}'")
  public void connect(String jdbcUrl, String username,
                      String password) {
    closeExistingConnection();
    try {
      String url = Common.resolveVariables(jdbcUrl);
      String user = Common.resolveVariables(username);
      String pass = Common.resolveVariables(password);
      Connection conn =
          DriverManager.getConnection(url, user, pass);
      CONNECTION.set(conn);
      LOG.info("Connected to database: {}", url);
    } catch (SQLException ex) {
      throw new IllegalStateException(
          "Failed to connect to database: " + jdbcUrl, ex);
    }
  }

  /** Opens a JDBC connection with URL only (no auth). */
  @Step("Connect to database '{0}'")
  public void connect(String jdbcUrl) {
    closeExistingConnection();
    try {
      String url = Common.resolveVariables(jdbcUrl);
      Connection conn = DriverManager.getConnection(url);
      CONNECTION.set(conn);
      LOG.info("Connected to database: {}", url);
    } catch (SQLException ex) {
      throw new IllegalStateException(
          "Failed to connect to database: " + jdbcUrl, ex);
    }
  }

  /** Closes the current JDBC connection. */
  @Step("Disconnect from database")
  public void disconnect() {
    closeExistingConnection();
    LOG.info("Disconnected from database");
  }

  // ── Execute ─────────────────────────────────────────────────

  /** Executes a SELECT query and stores the result set. */
  @Step("Execute query: {0}")
  public void executeQuery(String sql) {
    String resolved = Common.resolveVariables(sql);
    Connection conn = currentConnection();
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(resolved)) {
      storeResultSet(rs);
    } catch (SQLException ex) {
      throw new IllegalStateException(
          "Query execution failed: " + resolved, ex);
    }
  }

  /** Executes an INSERT, UPDATE, or DELETE statement. */
  @Step("Execute update: {0}")
  public void executeUpdate(String sql) {
    String resolved = Common.resolveVariables(sql);
    Connection conn = currentConnection();
    try (Statement stmt = conn.createStatement()) {
      int count = stmt.executeUpdate(resolved);
      UPDATE_COUNT.set(count);
      LOG.info("Update affected {} row(s)", count);
    } catch (SQLException ex) {
      throw new IllegalStateException(
          "Update execution failed: " + resolved, ex);
    }
  }

  // ── Result getters ──────────────────────────────────────────

  /** Returns the number of rows from the last query result. */
  @Step("Get row count")
  public String getRowCount() {
    return String.valueOf(RESULT_ROWS.get().size());
  }

  /** Returns the number of rows affected by the last update. */
  @Step("Get update count")
  public String getUpdateCount() {
    return String.valueOf(UPDATE_COUNT.get());
  }

  /** Returns a cell value by row and column index (1-based). */
  @Step("Get cell at row {0} column {1}")
  public String getCellValue(int row, int column) {
    List<List<String>> rows = RESULT_ROWS.get();
    validateRowIndex(row, rows.size());
    List<String> rowData = rows.get(row - 1);
    validateColumnIndex(column, rowData.size());
    String value = rowData.get(column - 1);
    return value != null ? value : "";
  }

  /** Returns a cell value by row index and column name. */
  @Step("Get cell at row {0} column '{1}'")
  public String getCellValueByName(int row, String columnName) {
    int colIndex = findColumnIndex(columnName);
    return getCellValue(row, colIndex);
  }

  /** Returns all values in a column (by index, 1-based). */
  @Step("Get column {0} values")
  public String getColumnValues(int column) {
    List<List<String>> rows = RESULT_ROWS.get();
    if (!rows.isEmpty()) {
      validateColumnIndex(column, rows.get(0).size());
    }
    List<String> values = new ArrayList<>();
    for (List<String> row : rows) {
      String val = row.get(column - 1);
      values.add(val != null ? val : "");
    }
    return String.join(",", values);
  }

  /** Returns all values in a column (by name). */
  @Step("Get column '{0}' values")
  public String getColumnValuesByName(String columnName) {
    int colIndex = findColumnIndex(columnName);
    return getColumnValues(colIndex);
  }

  /** Returns the column names from the last query result. */
  @Step("Get column names")
  public String getColumnNames() {
    return String.join(",", COLUMN_NAMES.get());
  }

  // ── Result assertions ───────────────────────────────────────

  /** Asserts the row count equals expected. */
  @Step("Verify row count is {0}")
  public void verifyRowCount(int expected) {
    int actual = RESULT_ROWS.get().size();
    assertThat(actual)
        .as("Query row count should be %d", expected)
        .isEqualTo(expected);
  }

  /** Asserts the update count equals expected. */
  @Step("Verify update count is {0}")
  public void verifyUpdateCount(int expected) {
    assertThat(UPDATE_COUNT.get())
        .as("Update count should be %d", expected)
        .isEqualTo(expected);
  }

  /** Asserts the query returned at least one row. */
  @Step("Verify query returns rows")
  public void verifyQueryReturnsRows() {
    assertThat(RESULT_ROWS.get())
        .as("Query should return at least one row")
        .isNotEmpty();
  }

  /** Asserts the query returned no rows. */
  @Step("Verify query returns no rows")
  public void verifyQueryReturnsNoRows() {
    assertThat(RESULT_ROWS.get())
        .as("Query should return no rows")
        .isEmpty();
  }

  /** Asserts a cell value equals expected (1-based). */
  @Step("Verify cell at row {0} column {1} equals '{2}'")
  public void verifyCellEquals(
      int row, int column, String expected) {
    String resolved = Common.resolveVariables(expected);
    String actual = getCellValue(row, column);
    assertThat(actual)
        .as("Cell [%d,%d] should equal '%s'",
            row, column, resolved)
        .isEqualTo(resolved);
  }

  /** Asserts a cell value contains expected text (1-based). */
  @Step("Verify cell at row {0} column {1} contains '{2}'")
  public void verifyCellContains(
      int row, int column, String expected) {
    String resolved = Common.resolveVariables(expected);
    String actual = getCellValue(row, column);
    assertThat(actual)
        .as("Cell [%d,%d] should contain '%s'",
            row, column, resolved)
        .contains(resolved);
  }

  /** Asserts a cell by column name equals expected. */
  @Step("Verify cell at row {0} column '{1}' equals '{2}'")
  public void verifyCellByNameEquals(
      int row, String columnName, String expected) {
    int colIndex = findColumnIndex(columnName);
    verifyCellEquals(row, colIndex, expected);
  }

  /** Asserts a cell by column name contains expected text. */
  @Step("Verify cell at row {0} column '{1}' contains '{2}'")
  public void verifyCellByNameContains(
      int row, String columnName, String expected) {
    int colIndex = findColumnIndex(columnName);
    verifyCellContains(row, colIndex, expected);
  }

  // ── Session management ──────────────────────────────────────

  /** Clears all DB session state (reported in Serenity). */
  @Step("Clear DB session")
  public void clearSession() {
    closeExistingConnection();
    RESULT_ROWS.get().clear();
    COLUMN_NAMES.get().clear();
    UPDATE_COUNT.remove();
  }

  /** Static cleanup for @After hooks (no Serenity reporting). */
  public static void resetState() {
    Connection conn = CONNECTION.get();
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException ex) {
        LOG.warn("Error closing DB connection in cleanup", ex);
      }
    }
    CONNECTION.remove();
    RESULT_ROWS.remove();
    COLUMN_NAMES.remove();
    UPDATE_COUNT.remove();
  }

  // ── Private helpers ─────────────────────────────────────────

  private Connection currentConnection() {
    Connection conn = CONNECTION.get();
    if (conn == null) {
      throw new IllegalStateException(
          "No DB connection. Call connect() first.");
    }
    return conn;
  }

  private void closeExistingConnection() {
    Connection conn = CONNECTION.get();
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException ex) {
        LOG.warn("Error closing existing DB connection", ex);
      }
      CONNECTION.remove();
    }
  }

  private void storeResultSet(ResultSet rs)
      throws SQLException {
    List<List<String>> rows = new ArrayList<>();
    List<String> colNames = new ArrayList<>();
    ResultSetMetaData meta = rs.getMetaData();
    int colCount = meta.getColumnCount();
    for (int i = 1; i <= colCount; i++) {
      colNames.add(meta.getColumnLabel(i));
    }
    while (rs.next()) {
      List<String> row = new ArrayList<>();
      for (int i = 1; i <= colCount; i++) {
        Object val = rs.getObject(i);
        row.add(val != null ? val.toString() : null);
      }
      rows.add(row);
    }
    RESULT_ROWS.set(rows);
    COLUMN_NAMES.set(colNames);
  }

  private int findColumnIndex(String columnName) {
    List<String> cols = COLUMN_NAMES.get();
    for (int i = 0; i < cols.size(); i++) {
      if (cols.get(i).equalsIgnoreCase(columnName)) {
        return i + 1;
      }
    }
    throw new IllegalArgumentException(
        "Column not found: " + columnName
            + ". Available: " + cols);
  }

  private void validateRowIndex(int row, int totalRows) {
    if (row < 1 || row > totalRows) {
      throw new IndexOutOfBoundsException(
          "Row " + row + " out of range [1.." + totalRows + "]");
    }
  }

  private void validateColumnIndex(int column, int totalCols) {
    if (column < 1 || column > totalCols) {
      throw new IndexOutOfBoundsException(
          "Column " + column + " out of range [1.."
              + totalCols + "]");
    }
  }
}
