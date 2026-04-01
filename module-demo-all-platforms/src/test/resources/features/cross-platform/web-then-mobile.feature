@cross-platform
Feature: Cross-platform Web then Mobile

  Scenario: Login web then search Messages app on Android with the web account
    # Step 1 — Login on the web
    Given "WebAdmin" opens a "chrome" browser
    And I maximize browser window
    And I navigate to the "urlAdmin" page
    When I enter "admin" to "username.input" field
    And I enter "admin" to "password.input" field

    # Save the admin username before login navigates away
    And I get value of "username.input" then save to "adminAccount"

    And I click "login.button"
    Then the URL should contain "route=common/dashboard"
    And I should see "dashboard.element"

    # Step 2 — Open Messages app on Android emulator
    Given "AndroidUser" opens a mobile device "android-emulator"

    # Skip the welcome / sign-in screen
    When I wait until mobile element "message.skipSignIn" is visible
    And I tap "message.skipSignIn"

    # Search in Messages using the saved web account
    And I wait until mobile element "message.searchIcon" is visible
    And I tap "message.searchIcon"
    And I wait until mobile element "message.searchInput" is visible

    # Use the saved web account (admin) to search in Messages
    And I enter saved variable "adminAccount" to mobile field "message.searchInput"
    Then mobile element "message.searchInput" should be visible
