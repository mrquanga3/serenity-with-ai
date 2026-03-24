Feature: Login to OpenCart Admin Panel

  Scenario: Login successfully with valid credentials
    Given I navigate to the login page
    When I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field
    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"

  Scenario: Login fails with invalid credentials
    Given I navigate to the login page
    When I enter "admin" to "username.input" field
    And I enter "wrongpassword" to "password.input" field
    And I click "login.button"
    Then I should see "error.message"
