@web
Feature: Multi-actor login to OpenCart Admin Panel

  Scenario: Two actors login with different browsers
    Given "Alice" opens a "chrome" browser
    And "Bob" opens a "firefox" browser
    And I maximize browser window
    When switching to "Alice"
    And I navigate to the "urlAdmin" page
    And I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field
    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"

    When switching to "Bob"
    And I navigate to the "urlAdmin" page
    And I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field
    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"

  Scenario: Two actors login with two chrome browsers
    Given "Alice" opens a "chrome" browser
    And "Bob" opens a "chrome" browser

    When switching to "Alice"
    And I navigate to the "urlAdmin" page
    And I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field
    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"

    When switching to "Bob"
    And I navigate to the "urlAdmin" page
    And I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field
    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"
