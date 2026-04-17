@cross-platform
Feature: Cross-platform Web then iOS

  Scenario: Login web then search Contacts app on iOS with the web account
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

    # Step 2 — Open Contacts app on iOS Simulator
    Given "IosUser" opens a mobile device "ios-simulator"
    When I wait until mobile element "ios.contacts.contactList" is visible

    # Search in Contacts using the saved web account
    And I tap "ios.contacts.searchField"
    And I wait until mobile element "ios.contacts.searchField" is visible

    # Use the saved web account (admin) to search in Contacts
    And I enter saved variable "adminAccount" to mobile field "ios.contacts.searchField"
    Then mobile element "ios.contacts.cancelButton" should be visible
