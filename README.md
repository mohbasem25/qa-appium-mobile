# qa-appium-mobile

Mobile UI test automation framework built with **Appium + Java + TestNG**,
targeting the [Sauce Labs "My Demo App"](https://github.com/saucelabs/my-demo-app-android)
Android application - an open-source shopping app widely used as an Appium
reference target.

This is a portfolio project by **Mohamed Elaadly** demonstrating native mobile
UI automation skills: capability configuration, the Page Object Model applied
to native (not web) screens, explicit-wait driven synchronization, and CI
integration for mobile test execution.

## Purpose

Most QA automation portfolios stop at web UI or API testing. This project
exists to show the additional layer of skill required for **native mobile**
automation specifically:

- Configuring and reasoning about Appium driver capabilities (`UiAutomator2Options`)
- Locating and interacting with native Android views (resource-id, accessibility id, UiAutomator selectors) instead of CSS/XPath on a DOM
- Handling native gestures, scroll-into-view, and hardware key events (back button)
- Structuring a maintainable Page Object Model for a multi-screen native app
- Wiring mobile tests into CI, which is materially harder than web CI

## Tech Stack

| Layer                | Choice                                     |
|----------------------|---------------------------------------------|
| Language             | Java 17                                     |
| Build tool           | Maven                                       |
| Mobile driver client | Appium Java Client 9.x (`UiAutomator2Options`) |
| Underlying protocol  | Selenium 4 / W3C WebDriver                  |
| Test runner          | TestNG                                      |
| Design pattern       | Page Object Model (POM)                     |
| App under test       | Sauce Labs "My Demo App" (Android, native)  |
| CI                   | GitHub Actions + `reactivecircus/android-emulator-runner` |

## Architecture

```
src/main/java/
├── pages/
│   ├── BasePage.java           # shared WebDriverWait helpers, tap/type/scroll utilities
│   ├── LoginPage.java          # username/password login screen
│   ├── CatalogPage.java        # product listing, sort, cart badge, drawer entry point
│   ├── ProductDetailPage.java  # product detail, quantity stepper, add-to-cart
│   ├── CartPage.java           # cart line items, removal, checkout entry point
│   ├── CheckoutPage.java       # checkout step-one screen
│   ├── MenuPage.java           # drawer menu (login/logout/webview/geolocation)
│   └── WebviewPage.java        # embedded webview screen
└── utils/
    ├── DriverFactory.java      # builds AndroidDriver from UiAutomator2Options
    └── ConfigReader.java       # typed accessor over config.properties (+ -D overrides)

src/test/java/tests/
├── BaseTest.java               # per-test driver lifecycle (ThreadLocal AndroidDriver)
├── TestData.java                # shared fixture constants (credentials, product names)
├── LoginTests.java              # 7 tests - valid/invalid login, empty fields, logout
├── CatalogTests.java            # 10 tests - listing, sort, navigation, add-to-cart
├── CartTests.java               # 7 tests - badge count, removal, checkout, empty state
└── DrawerMenuTests.java         # 6 tests - menu navigation, webview, geolocation
```

Design principles:

- **No hardcoded environment/device values.** Everything device- and
  path-specific lives in `src/main/resources/config.properties` and can be
  overridden per run with `-D` system properties (used by CI to inject the
  downloaded APK's absolute path).
- **Explicit waits only.** Every wait goes through `WebDriverWait` +
  `ExpectedConditions` in `BasePage`. `Thread.sleep` is not used anywhere in
  this codebase - native app state changes are always awaited on an
  observable condition (element visibility/clickability), not a fixed delay.
- **Fresh app session per test.** `BaseTest` creates a new `AndroidDriver`
  session before each `@Test` method and tears it down after, so tests are
  independent and safely re-runnable/parallelizable across devices.

## Prerequisites

To run this suite locally you need:

1. **Java 17** and **Maven 3.9+**
2. **Node.js 18+** (Appium itself is an NPM package)
3. **Appium server 2.x** with the UiAutomator2 driver:
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
4. **Android SDK** with at least one of:
   - An Android Virtual Device (AVD) created via Android Studio / `avdmanager`, or
   - A real Android device with USB debugging enabled
5. The **My Demo App** APK, downloaded from the
   [official releases page](https://github.com/saucelabs/my-demo-app-android/releases)
   (the binary is intentionally **not** committed to this repo - see `.gitignore`).

## Setup

```bash
git clone <this-repo-url>
cd qa-appium-mobile

# 1. Download the APK and place it where config.properties expects it
mkdir -p apps
curl -L -o apps/Android-MyDemoAppRN.apk \
  https://github.com/saucelabs/my-demo-app-android/releases/latest/download/Android-MyDemoAppRN.apk

# 2. Start an emulator (example using a pre-created AVD named Pixel_6_API_33)
emulator -avd Pixel_6_API_33

# 3. Start the Appium server in a separate terminal
appium
```

Adjust `src/main/resources/config.properties` if your emulator/device name,
platform version, or APK location differ from the defaults.

## Running the tests

```bash
mvn test
```

To override configuration without editing the properties file:

```bash
mvn test \
  -Dappium.deviceName="Pixel_6_API_33" \
  -Dappium.platformVersion="13.0" \
  -Dappium.app.path="/absolute/path/to/Android-MyDemoAppRN.apk"
```

Run a single test class via the TestNG suite mechanism:

```bash
mvn test -Dtest=LoginTests
```

Surefire/TestNG HTML and XML reports are written to `target/surefire-reports/`
and `test-output/`.

## Test coverage summary

| Test class          | Test count | Scenarios covered                                                                 |
|----------------------|:---------:|-------------------------------------------------------------------------------------|
| `LoginTests`         | 7         | Valid login, invalid username, invalid password, empty fields, empty password, password visibility toggle, logout |
| `CatalogTests`       | 10        | Listing loads, name/price rendering, sort A-Z / Z-A / price asc / price desc, product detail navigation, description rendering, add-to-cart from listing, add-to-cart from detail, quantity stepper |
| `CartTests`          | 7         | Multi-item badge count, cart contents, remove-last-item empty state, remove-one-of-several, fresh-session empty cart, proceed to checkout, add-from-detail then remove |
| `DrawerMenuTests`    | 6         | Menu items visible, logged-in user label, webview screen loads, back-from-webview, geolocation navigation, menu reopens |
| **Total**            | **30**    | |

## CI: Appium on GitHub Actions

`.github/workflows/appium-android.yml` boots a real Android emulator on a
GitHub-hosted macOS runner (for hardware-accelerated virtualization),
installs and starts an Appium server, downloads the demo APK, and runs
`mvn test` against it - with the AVD image cached between runs to keep boot
time down.

**Honest caveat:** mobile CI is inherently slower and flakier than web CI -
emulator cold boot, virtualization overhead, and occasional ANR/timeout noise
are expected even in a healthy pipeline. This workflow is included to
demonstrate that this capability has been built and works end-to-end, not
as a claim that CI is the primary or fastest way to iterate on this suite.
Day-to-day local development against a persistent emulator or a real device
is materially faster and is the intended primary workflow; CI serves as a
regression safety net and a portfolio signal that mobile CI integration -
something most QA portfolios omit entirely - has actually been implemented.

## Why this project

This project is meant to demonstrate:

- **Cross-platform capability design** - capabilities are built through
  `UiAutomator2Options` and externalized to config, the same shape of design
  that extends cleanly to an `XCUITestOptions` iOS counterpart without
  touching test or page-object code.
- **POM applied to native mobile**, not just web - locating native Android
  views by resource-id/accessibility-id/UiAutomator selector, and structuring
  page objects around app *screens* rather than URLs.
- **Proper synchronization** - explicit `WebDriverWait`/`ExpectedConditions`
  throughout, plus `UiScrollable`-based scroll-into-view for off-screen native
  elements, with zero arbitrary `Thread.sleep` calls.
- **CI for mobile** - a working emulator-based GitHub Actions pipeline, which
  is a differentiator most QA automation portfolios lack because mobile CI is
  genuinely harder to stand up than web CI.

## Project structure

```
qa-appium-mobile/
├── .github/workflows/appium-android.yml
├── src/
│   ├── main/java/pages/...
│   ├── main/java/utils/...
│   ├── main/resources/config.properties
│   └── test/java/tests/...
│   └── test/resources/testng.xml
├── pom.xml
├── .gitignore
├── LICENSE
└── README.md
```

## License

MIT - see [LICENSE](LICENSE).
