# QA Testing Types Framework

One Java project that demonstrates **18 software testing types** with working code and documentation. It is built with Maven, TestNG, Selenium 4, and JaCoCo.

There are two systems under test:
1. **A small shop library inside this repo** (`src/main/java`): cart, discount rules, inventory, checkout, payment. This is real code to unit test, integration test, white-box test, and load/stress test.
2. **[Sauce Demo](https://www.saucedemo.com)**, a public practice e-commerce site, for browser-based testing.

## The 18 testing types

| # | Type | Where | Run it |
|---|------|-------|--------|
| 1 | Unit | `unit/` | `mvn test -Dgroups=unit` |
| 2 | Integration | `integration/` | `mvn test -Dgroups=integration` |
| 3 | System | `ui/system/` | `mvn test -Dgroups=system` |
| 4 | Sanity | `ui/sanity/` | `mvn test -Dgroups=sanity` |
| 5 | Smoke | `ui/smoke/` | `mvn test -Dgroups=smoke` |
| 6 | Regression | `ui/regression/` | `mvn test -Dgroups=regression` |
| 7 | User Acceptance (UAT) | `ui/uat/` + `docs/uat-signoff.md` | `mvn test -Dgroups=uat` |
| 8 | Performance | `performance/`, `ui/performance/` | `mvn test -Dgroups=performance,performance-ui` |
| 9 | Load | `load/` | `mvn test -Dgroups=load` |
| 10 | Stress | `stress/` | `mvn test -Dgroups=stress` |
| 11 | Security | `security/`, `ui/security/` | `mvn test -Dgroups=security,security-ui` |
| 12 | Usability | `ui/usability/` + `docs/usability-checklist.md` | `mvn test -Dgroups=usability` |
| 13 | Compatibility | `ui/compatibility/` + `suites/compatibility.xml` | `mvn test -Pcompatibility` |
| 14 | Black-box | `blackbox/` | `mvn test -Dgroups=blackbox` |
| 15 | White-box | `whitebox/` + JaCoCo | `mvn test -Dgroups=whitebox` |
| 16 | Gray-box | `ui/graybox/` | `mvn test -Dgroups=graybox` |
| 17 | Exploratory | `docs/exploratory-testing.md` | Manual (charters provided) |
| 18 | Ad-hoc | `docs/adhoc-testing.md` + `adhoc/` | `mvn test -Dgroups=adhoc` |

Test folders are under `src/test/java/com/manish/qa/`. Every test class starts with a comment explaining what that testing type is and how this example shows it.

New to the terms? Start with **[docs/testing-types-cheatsheet.md](docs/testing-types-cheatsheet.md)**.

## Requirements

- Java 17+
- Maven 3.8+
- Google Chrome for browser tests (Firefox and Edge are optional, used for compatibility testing). Selenium Manager downloads drivers automatically.

## Quick start

```bash
# Default: all code-level tests (no browser needed, about 10 seconds)
mvn test

# Code-level tests + fail the build if coverage < 80%
mvn verify

# All browser tests
mvn test -Dgroups=smoke,sanity,system,regression,uat,usability,graybox,security-ui,performance-ui

# Any group without opening a browser window
mvn test -Dgroups=smoke -Dheadless=true

# A different browser
mvn test -Dgroups=smoke -Dbrowser=firefox
```

## Reports

| Report | Location |
|--------|----------|
| TestNG results | `target/surefire-reports/index.html` |
| Code coverage (white-box) | `target/site/jacoco/index.html` |

## Project structure

```
qa-testing-types/
├── pom.xml
├── suites/compatibility.xml            browsers x screen sizes
├── docs/                               manual testing artifacts
│   ├── testing-types-cheatsheet.md
│   ├── exploratory-testing.md          charters + session sheet
│   ├── adhoc-testing.md                error-guessing ideas + log
│   ├── uat-signoff.md                  acceptance criteria + sign-off
│   └── usability-checklist.md          user-session script + heuristics
├── src/main/java/com/manish/shop/      application under test
│   ├── Cart, Product, DiscountCalculator, InventoryRepository
│   ├── order/      OrderService, Order, OutOfStockException
│   ├── payment/    PaymentGateway, FakePaymentGateway
│   └── security/   InputValidator
└── src/test/java/com/manish/qa/
    ├── unit/  integration/  whitebox/  blackbox/
    ├── security/  performance/  load/  stress/  adhoc/
    ├── support/TestData.java
    └── ui/
        ├── base/   BaseUiTest, DriverFactory
        ├── pages/  Page Object Model: Login, Inventory, Cart, Checkout
        └── smoke/ sanity/ system/ regression/ uat/ usability/
            compatibility/ graybox/ security/ performance/
```

## Design decisions

- **TestNG groups** map one-to-one to testing types, so any type can be run on its own.
- **Page Object Model** keeps locators in `ui/pages`. Tests read like user actions.
- **`BigDecimal` for money**: floating-point errors are a classic bug, and the tests check exact cents.
- **Fake payment gateway** keeps integration tests fast and repeatable without a real card processor.
- **Thread-safe inventory**: the stress test proves 500 simultaneous buyers can't oversell 50 items.
- **CI** (`.github/workflows/tests.yml`) runs code-level tests with a coverage gate, then headless browser smoke tests, on every push.

## Note on the security tests

The browser security tests only send harmless, well-known payloads to Sauce Demo, a site built for practice. Only run security tests against systems you own or have permission to test.
