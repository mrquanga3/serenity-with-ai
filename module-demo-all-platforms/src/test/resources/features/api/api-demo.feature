@api
Feature: API Testing Demo
  Demonstrate generic API testing keywords with JSONPlaceholder.

  Scenario: GET request to retrieve a post
    Given I set API base URL from "apiBaseUrl"
    When I send a GET request to the "endpoint.posts.single" endpoint
    Then the API response status code should be 200
    And the API response should be JSON
    And the API response body should contain "userId"
    And the API JSON path "id" should be "1"
    And I get API response body then save to "responseBody"
    And the API response should match JSON schema "schemas/post-schema.json"
    And the API response should match schema
      | field  | type    |
      | id     | #number |
      | title  | #string |
      | body   | #string |
      | userId | #number |

  Scenario: POST request to create a resource
    Given I set API base URL from "apiBaseUrl"
    When I set API request body to
      """
      {
        "title": "foo",
        "body": "bar",
        "userId": 1
      }
      """
    And I send a POST request to the "endpoint.posts.create" endpoint
    Then the API response status code should be 201
    And the API response should be JSON
    And the API JSON path "title" should be "foo"
    And I get API JSON path "id" then save to "newPostId"
    And the API response should match JSON schema "schemas/created-post-schema.json"
    And the API response should match schema
      | field  | type     |
      | id     | #number  |
      | title  | #string  |
      | body   | ##string |
      | userId | ##number |

  Scenario: PUT request to update a resource
    Given I set API base URL from "apiBaseUrl"
    When I set API request body to
      """
      {
        "id": 1,
        "title": "updated title",
        "body": "updated body",
        "userId": 1
      }
      """
    And I send a PUT request to the "endpoint.posts.update" endpoint
    Then the API response status code should be 200
    And the API JSON path "title" should be "updated title"

  Scenario: Variable resolution across request components
    Given I set API base URL from "apiBaseUrl"
    When I send a GET request to the "endpoint.posts.single" endpoint
    Then the API response status code should be 200
    And I get API JSON path "id" then save to "postId"
    And I get API JSON path "userId" then save to "userId"
    When I set API header "X-User" to "${userId}"
    And I set API request body to
      """
      {
        "title": "Post ${postId} by user ${userId}",
        "body": "Updated content",
        "userId": ${userId}
      }
      """
    And I send a PUT request to "/posts/${postId}"
    Then the API response status code should be 200
    And the API JSON path "title" should be "Post ${postId} by user ${userId}"

  Scenario: DELETE request to remove a resource
    Given I set API base URL from "apiBaseUrl"
    When I send a DELETE request to the "endpoint.posts.delete" endpoint
    Then the API response status code should be 200

  Scenario: GET list of posts and validate array item schema
    Given I set API base URL from "apiBaseUrl"
    When I send a GET request to the "endpoint.posts.list" endpoint
    Then the API response status code should be 200
    And the API response should be JSON
    And the API response should match JSON schema "schemas/post-list-schema.json"
    And each item in the API response array should match schema
      | field  | type    |
      | id     | #number |
      | title  | #string |
      | body   | #string |
      | userId | #number |
