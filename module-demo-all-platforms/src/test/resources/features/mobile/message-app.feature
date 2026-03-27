Feature: Android Messages App

  Scenario: Open Messages app and search for a text
    Given "MobileUser" opens a mobile device "android-emulator"
    # Skip the welcome / sign-in screen
    When I wait until mobile element "message.skipSignIn" is visible
    And I tap "message.skipSignIn"
    And I wait until mobile element "message.searchIcon" is visible
    And I tap "message.searchIcon"
    And I wait until mobile element "message.searchInput" is visible
    And I enter "Hello" to mobile field "message.searchInput"
    Then mobile element "message.searchInput" should be visible

  Scenario: Open Messages app and start a new conversation
    Given "MobileUser" opens a mobile device "android-emulator"
    # Skip the welcome / sign-in screen
    When I wait until mobile element "message.skipSignIn" is visible
    And I tap "message.skipSignIn"
    And I wait until mobile element "message.startChat" is visible
    And I tap "message.startChat"
