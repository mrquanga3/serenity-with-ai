@db
Feature: Database Testing Demo
  Demonstrate generic DB testing keywords with a local MySQL instance.
  Queries the information_schema.tables virtual table.

  Background:
    Given I connect to database from "dbJdbcUrl" with user from "dbUsername" and password from "dbPassword"

  Scenario: SELECT all rows from information_schema.tables
    When I execute DB query "SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE FROM information_schema.tables LIMIT 10"
    Then the DB query should return rows
    And I get DB row count then save to "totalRows"
    And I get DB column names then save to "columns"

  Scenario: SELECT specific columns and verify cell value
    When I execute DB query "SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE FROM information_schema.tables WHERE TABLE_SCHEMA = 'information_schema' LIMIT 5"
    And I get DB cell at row 1 column "TABLE_SCHEMA" then save to "firstSchema"
    And I get DB cell at row 1 column "TABLE_NAME" then save to "firstTable"
    And I get DB cell at row 1 column "TABLE_TYPE" then save to "firstType"
    Then the DB cell at row 1 column "TABLE_SCHEMA" should be "information_schema"

  Scenario: SELECT with WHERE clause and verify result
    When I execute DB query "SELECT TABLE_SCHEMA, TABLE_NAME FROM information_schema.tables WHERE TABLE_NAME = 'TABLES'"
    Then the DB query should return rows
    And the DB cell at row 1 column "TABLE_NAME" should be "TABLES"
    And the DB cell at row 1 column "TABLE_SCHEMA" should be "information_schema"

  Scenario: SELECT with COUNT aggregate
    When I execute DB query "SELECT COUNT(*) AS total FROM information_schema.tables"
    Then the DB row count should be 1
    And I get DB cell at row 1 column "total" then save to "tableCount"

  Scenario: SELECT with no matching rows
    When I execute DB query "SELECT * FROM information_schema.tables WHERE TABLE_NAME = 'nonexistent_table_xyz'"
    Then the DB query should return no rows
    And the DB row count should be 0

  Scenario: Variable resolution across queries
    When I execute DB query "SELECT DISTINCT TABLE_SCHEMA FROM information_schema.tables LIMIT 1"
    And I get DB cell at row 1 column 1 then save to "schemaName"
    When I execute DB query "SELECT TABLE_NAME FROM information_schema.tables WHERE TABLE_SCHEMA = '${schemaName}' LIMIT 5"
    Then the DB query should return rows
    And I get DB column "TABLE_NAME" values then save to "allTables"

  Scenario: SELECT using DocString for complex query
    When I execute DB query
      """
      SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE, ENGINE
        FROM information_schema.tables
       WHERE TABLE_SCHEMA = 'mysql'
         AND TABLE_TYPE = 'BASE TABLE'
       LIMIT 3
      """
    Then the DB query should return rows
    And the DB cell at row 1 column "TABLE_TYPE" should be "BASE TABLE"
