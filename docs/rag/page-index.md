# Page Index

Structured index of all pages, screens, and endpoints in the project.
AI agents and humans use this to quickly look up what exists, what elements are available,
what tests cover each page, and how pages connect to each other.

**Properties base path:** `module-demo-all-platforms/src/test/resources/properties/`
**Features base path:** `module-demo-all-platforms/src/test/resources/features/`

---

## Quick Reference

| Page/Screen                                     | Platform | Properties File                 | Feature File(s)                                          | Elements    |
|-------------------------------------------------|----------|---------------------------------|----------------------------------------------------------|-------------|
| [Login (OpenCart Admin)](#login-opencart-admin) | web      | `login/login.properties`        | `login/login.feature`, `login/login-multi-actor.feature` | 5           |
| [API - Posts](#api---posts)                     | api      | `api/api.properties`            | `api/api-demo.feature`                                   | 4 endpoints |
| [Messages App (Android)](#messages-app-android) | mobile   | `mobile/message-app.properties` | `mobile/message-app.feature`                             | 6           |
| [Database (info_schema)](#database)             | db       | (none - inline SQL)             | `db/db-demo.feature`                                     | N/A         |
| [Cross-Platform Flow](#cross-platform-flow)     | cross    | (shared from login + mobile)    | `cross-platform/web-then-mobile.feature`                 | (shared)    |

---

## Login (OpenCart Admin)

- **Properties file:** `login/login.properties`
- **Environment keys:** `urlAdmin` (page URL), `adminUsername`, `adminPassword`
- **Platform:** web
- **Tag:** `@web`

### Elements

| Key                 | Locator Type | Element Type | Description                                       |
|---------------------|--------------|--------------|---------------------------------------------------|
| `username.input`    | id           | text input   | Username field                                    |
| `password.input`    | id           | text input   | Password field                                    |
| `login.button`      | css          | button       | Submit login form                                 |
| `dashboard.element` | id           | container    | Dashboard header (visible after successful login) |
| `error.message`     | css          | text         | Error alert shown on failed login                 |

### Test Coverage

| Feature File                             | Scenario                                                           | Tag             |
|------------------------------------------|--------------------------------------------------------------------|-----------------|
| `login/login.feature`                    | Login successfully with valid credentials                          | @web            |
| `login/login.feature`                    | Login fails with invalid credentials                               | @web            |
| `login/login-multi-actor.feature`        | Two actors login with different browsers                           | @web            |
| `login/login-multi-actor.feature`        | Two actors login with two chrome browsers                          | @web            |
| `cross-platform/web-then-mobile.feature` | Login web then search Messages app on Android with the web account | @cross-platform |

### Navigation Flow

- **Entry point:** Navigate to `urlAdmin`
- **On success:** URL contains `route=common/dashboard`, `dashboard.element` becomes visible
- **On failure:** `error.message` becomes visible
- **Variable output:** `username.input` value can be saved to variable (used in cross-platform flow)

---

## API - Posts

- **Properties file:** `api/api.properties`
- **Environment keys:** `apiBaseUrl` (base URL for all API requests)
- **Platform:** api
- **Tag:** `@api`

### Endpoints

| Key                     | Path       | Methods Tested | Description             |
|-------------------------|------------|----------------|-------------------------|
| `endpoint.posts.single` | `/posts/1` | GET            | Retrieve a single post  |
| `endpoint.posts.create` | `/posts`   | POST           | Create a new post       |
| `endpoint.posts.update` | `/posts/1` | PUT            | Update an existing post |
| `endpoint.posts.delete` | `/posts/1` | DELETE         | Delete a post           |

### Test Coverage

| Feature File           | Scenario                                      | Tag  |
|------------------------|-----------------------------------------------|------|
| `api/api-demo.feature` | GET request to retrieve a post                | @api |
| `api/api-demo.feature` | POST request to create a resource             | @api |
| `api/api-demo.feature` | PUT request to update a resource              | @api |
| `api/api-demo.feature` | Variable resolution across request components | @api |
| `api/api-demo.feature` | DELETE request to remove a resource           | @api |

### Data Flow

- GET `/posts/1` returns `id` and `userId` — saveable to variables
- POST `/posts` returns `id` of newly created resource (saved as `newPostId`)
- Variables `${postId}` and `${userId}` are used in subsequent PUT requests via `${varName}` resolution

---

## Messages App (Android)

- **Properties file:** `mobile/message-app.properties`
- **Device profile:** `mobile/android-emulator.properties`
- **App:** `com.google.android.apps.messaging`
- **Activity:** `com.google.android.apps.messaging.ui.ConversationListActivity`
- **Platform:** mobile
- **Tag:** `@mobile`

### Elements

| Key                        | Locator Type    | Element Type | Description                   |
|----------------------------|-----------------|--------------|-------------------------------|
| `message.skipSignIn`       | id              | button       | Skip welcome / sign-in screen |
| `message.searchIcon`       | accessibilityId | button       | Opens search                  |
| `message.searchInput`      | id              | text input   | Search text field             |
| `message.conversationList` | accessibilityId | list         | Main conversation list        |
| `message.emptyHint`        | id              | text         | Empty state message           |
| `message.startChat`        | accessibilityId | button       | Start new conversation        |

### Device Profile (`android-emulator`)

| Key               | Value                   |
|-------------------|-------------------------|
| `platformName`    | android                 |
| `appiumUrl`       | `http://127.0.0.1:4723` |
| `udid`            | emulator-5554           |
| `platformVersion` | 16                      |

### Test Coverage

| Feature File                             | Scenario                                                           | Tag             |
|------------------------------------------|--------------------------------------------------------------------|-----------------|
| `mobile/message-app.feature`             | Open Messages app and search for a text                            | @mobile         |
| `mobile/message-app.feature`             | Open Messages app and start a new conversation                     | @mobile         |
| `cross-platform/web-then-mobile.feature` | Login web then search Messages app on Android with the web account | @cross-platform |

### Navigation Flow

- **Entry point:** App launch lands on welcome/sign-in screen
- **First action:** Tap `message.skipSignIn` to bypass welcome
- **Then:** Conversation list with `message.searchIcon` and `message.startChat` available
- **Search flow:** Tap `message.searchIcon` → wait for `message.searchInput` → enter text

---

## Database

- **Properties file:** none (queries are inline in feature steps)
- **Environment keys:** `dbJdbcUrl`, `dbUsername`, `dbPassword`
- **Platform:** db
- **Tag:** `@db`
- **Target:** `information_schema.tables` virtual table (MySQL)

### Test Coverage

| Feature File         | Scenario                                       | Tag |
|----------------------|------------------------------------------------|-----|
| `db/db-demo.feature` | SELECT all rows from information_schema.tables | @db |
| `db/db-demo.feature` | SELECT specific columns and verify cell value  | @db |
| `db/db-demo.feature` | SELECT with WHERE clause and verify result     | @db |
| `db/db-demo.feature` | SELECT with COUNT aggregate                    | @db |
| `db/db-demo.feature` | SELECT with no matching rows                   | @db |
| `db/db-demo.feature` | Variable resolution across queries             | @db |
| `db/db-demo.feature` | SELECT using DocString for complex query       | @db |

### Data Flow

- Query results can be saved to variables: row count, column names, cell values, column values
- Variables `${schemaName}` used in subsequent queries via `${varName}` resolution

---

## Cross-Platform Flow

Scenarios that span multiple platforms, sharing data via global variables.

### Web → Mobile: Login then search

- **Feature file:** `cross-platform/web-then-mobile.feature`
- **Tag:** `@cross-platform`
- **Pages used:** [Login](#login-opencart-admin) + [Messages App](#messages-app-android)
- **Flow:**
    1. `WebAdmin` opens Chrome, logs in via Login page
    2. Saves `username.input` value to variable `adminAccount`
    3. `AndroidUser` opens Messages app on `android-emulator`
    4. Skips sign-in, searches for `${adminAccount}` in Messages
- **Variable bridge:** `adminAccount` (saved from web, used in mobile)

---

## Environment Configuration

Environment-specific values are in `properties/{env}/environment.properties` (default: SIT).

| Key             | Purpose                   | Used By     |
|-----------------|---------------------------|-------------|
| `urlAdmin`      | OpenCart admin panel URL  | Login page  |
| `adminUsername` | Admin username            | Login page  |
| `adminPassword` | Admin password            | Login page  |
| `apiBaseUrl`    | JSONPlaceholder base URL  | API - Posts |
| `dbJdbcUrl`     | MySQL JDBC connection URL | Database    |
| `dbUsername`    | Database username         | Database    |
| `dbPassword`    | Database password         | Database    |

Available environments: **SIT** (default), **UAT**

---

## Maintenance

### When adding a new page

1. Add the page to the [Quick Reference](#quick-reference) table
2. Add a detailed entry following the template structure above
3. Update [Cross-Platform Flow](#cross-platform-flow) if the new page connects to existing pages
4. Update [Environment Configuration](#environment-configuration) if new environment keys are added

### When modifying locators

- Update the Elements table for the affected page

### When adding new feature files or scenarios

- Update the Test Coverage section for the affected page

### When adding a new device profile

- Add a Device Profile subsection under the relevant mobile page entry