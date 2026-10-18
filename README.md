# OrangeHRM Selenium Test Suite

Chrome-only end-to-end UI automation for the [OrangeHRM public demo](https://opensource-demo.orangehrmlive.com/), built with Java, Selenium 4, TestNG, and Maven. The project uses the Page Object Model, explicit waits, configurable execution, and screenshots on failure.

## Test coverage

Authentication checks cover successful login and logout, invalid password, unknown username, required fields, and navigation to and cancellation from the password reset screen. Navigation checks sign in and open the Admin, PIM, Leave, Time, Recruitment, My Info, Performance, and Directory modules. Read-only list workflows submit and reset the default filters in Admin, PIM, Leave, Recruitment, Performance, and Directory.

The tests are read-only with respect to HR records. The public demo is shared, so availability, account access, and seeded data can change or be rate limited.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- Google Chrome

Selenium Manager resolves a compatible ChromeDriver automatically. No separate driver download or configuration is needed.

## Run

```bash
mvn clean test
```

Run with a visible Chrome window:

```bash
mvn clean test -Dheadless=false
```

Override the target and login details through Maven properties or environment variables:

```bash
mvn clean test -DbaseUrl=https://opensource-demo.orangehrmlive.com -Dusername=Admin -Dpassword=admin123
```

Supported environment variables: `ORANGEHRM_BASE_URL`, `ORANGEHRM_USERNAME`, `ORANGEHRM_PASSWORD`, `HEADLESS`, and `SELENIUM_TIMEOUT`.

## Run directly from VS Code

1. Open this project folder in VS Code and allow the Java project to finish importing.
2. If prompted, install the recommended **Extension Pack for Java**. It includes Java test support; the project is configured for TestNG.
3. Open the **Testing** view (beaker icon) and choose **Run Test** beside a test, a test class, or the whole suite. You can also use the green **Run Test** link above a test method in the editor.
4. Chrome launches visibly for these VS Code runs. Use the Maven commands above for headless runs.

The first run may take longer while Maven dependencies and ChromeDriver are resolved. VS Code test results appear in the Testing view; failure screenshots go to `target/screenshots/`.

The interactive HTML pass/fail report is generated at `target/test-reports/OrangeHRM-Test-Report.html`. Each test case is a link that expands its recorded execution steps. The report is refreshed as each test finishes, including when tests are launched from VS Code's Testing panel. It also includes status totals, durations, failure details, a test filter, and links to failure screenshots. TestNG and Surefire reports are written under `target/surefire-reports/`, and failure screenshots are saved to `target/screenshots/`.

## Structure

```text
src/main/java/com/orangehrm/automation/
  config/       Runtime configuration
  driver/       Chrome WebDriver setup
  pages/        Page Object Model classes
src/test/java/com/orangehrm/automation/
  base/         Shared test lifecycle
  listeners/    Failure screenshot listener
  tests/        Authentication, module navigation, and list search scenarios
src/test/resources/testng.xml
.github/workflows/ci.yml
```

## Continuous integration

GitHub Actions runs the suite on pushes and pull requests with Java 17 and headless Chrome. CI requires network access so Selenium Manager can resolve ChromeDriver and Maven can download dependencies.

The public demo credentials are defaults only. They are configurable and should be replaced when targeting another OrangeHRM instance.
