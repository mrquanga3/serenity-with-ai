@ios
Feature: iOS Contacts App

  Scenario: Open Contacts app and search for a contact
    Given "IosUser" opens a mobile device "ios-simulator"
    When I wait until mobile element "ios.contacts.contactList" is visible
    And I tap "ios.contacts.searchField"
    And I wait until mobile element "ios.contacts.searchField" is visible
    And I enter "John" to mobile field "ios.contacts.searchField"
    Then mobile element "ios.contacts.cancelButton" should be visible

  Scenario: Open Contacts app and start adding a new contact
    Given "IosUser" opens a mobile device "ios-simulator"
    When I wait until mobile element "ios.contacts.contactList" is visible
    And I tap "ios.contacts.addButton"
    And I wait until mobile element "ios.contacts.firstNameField" is visible
    And I enter "Jane" to mobile field "ios.contacts.firstNameField"
    And I enter "Doe" to mobile field "ios.contacts.lastNameField"
    Then mobile element "ios.contacts.doneButton" should be visible
