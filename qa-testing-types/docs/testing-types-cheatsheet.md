# Testing Types Cheat Sheet (interview prep)

Each entry: what it is, who does it and when, and the example in this project.

## By level (bottom to top of the test pyramid)

**Unit testing.** Tests one small piece of code (a method or class) in isolation. Done by developers or SDETs on every code change. Fast and numerous.
→ `unit/CartTest`, `unit/DiscountCalculatorTest`

**Integration testing.** Tests that components work together: data passed correctly, failures handled across modules, APIs and databases connected. Done after unit testing.
→ `integration/OrderServiceIntegrationTest` (stock rollback when payment is declined)

**System testing.** Tests the complete, deployed application end to end against the requirements. Done by QA in a test environment.
→ `ui/system/EndToEndCheckoutSystemTest`

**User Acceptance Testing (UAT).** Business users confirm the product solves their real problem. The final phase before release; the business signs off.
→ `ui/uat/UserAcceptanceTest` + `docs/uat-signoff.md`

## By purpose / timing

**Smoke testing.** A shallow, wide check that a new build is stable enough to test at all. "Does it turn on?" Run first on every build.
→ `ui/smoke/SmokeTest`

**Sanity testing.** A narrow, deep check of one specific fix or change. "Does this fix make sense?" Run before full regression.
→ `ui/sanity/SanityTest`

| | Smoke | Sanity |
|-|-------|--------|
| Scope | Whole app, shallow | One area, deeper |
| When | Every new build | After a specific fix |
| Usually | Scripted, automated | Often unscripted |

**Regression testing.** Re-runs tests for features that already worked, to catch side effects of new changes. The best candidate for automation.
→ `ui/regression/RegressionTest`

## Non-functional

**Performance testing.** How fast is it under normal conditions? Measures response time against a budget.
→ `performance/CheckoutPerformanceTest`, `ui/performance/PageLoadPerformanceUiTest`

**Load testing.** Does it still work at the *expected* peak number of users?
→ `load/CheckoutLoadTest` (25 concurrent users, 500 orders)

**Stress testing.** What happens *beyond* capacity? It should fail gracefully and recover, with no data corruption.
→ `stress/CheckoutStressTest` (500 buyers fight over 50 jackets)

| | Performance | Load | Stress |
|-|-------------|------|--------|
| Question | How fast? | OK at expected peak? | What breaks past the limit? |
| Users | Normal | Expected maximum | Beyond maximum |
| Tools | JMeter, k6, Lighthouse | JMeter, Gatling, k6 | JMeter, Gatling, k6 |

**Security testing.** Finds weaknesses attackers could use: injection, XSS, broken access control, exposed data. Guided by the OWASP Top 10.
→ `security/InputSecurityTest`, `ui/security/LoginSecurityUiTest`

**Usability testing.** Is it easy and pleasant to use? Real users attempt tasks while being observed.
→ `ui/usability/UsabilityTest` + `docs/usability-checklist.md`

**Compatibility testing.** Does it work across browsers, devices, screen sizes, and operating systems?
→ `ui/compatibility/CrossBrowserCompatibilityTest` + `suites/compatibility.xml`

## By knowledge of the code

**Black-box testing.** Tests from the requirements only, with no view of the code. Techniques: equivalence partitioning, boundary value analysis, decision tables, state transitions.
→ `blackbox/BlackBoxTechniquesTest`

**White-box testing.** Tests designed by reading the code, so that every statement, branch, and path runs. Measured with code coverage (JaCoCo).
→ `whitebox/DiscountCalculatorWhiteBoxTest` + `target/site/jacoco/index.html`

**Gray-box testing.** Tests through the UI or API with *partial* knowledge of internals (database schema, storage, cookies, architecture).
→ `ui/graybox/CartStorageGrayBoxTest` (reads and writes the cart in localStorage)

## Unscripted

**Exploratory testing.** Structured but unscripted: a charter, a time box, notes, a debrief. Learn and test at the same time.
→ `docs/exploratory-testing.md`

**Ad-hoc testing.** Fully informal, with no plan or documentation. Error guessing from experience.
→ `docs/adhoc-testing.md` + `adhoc/RandomInputAdhocTest` (automated monkey test)

---

## Common interview questions

- **Smoke vs sanity?** Smoke is wide and shallow on a new build. Sanity is narrow and deep after a specific fix.
- **Load vs stress?** Load tests the expected peak. Stress goes past it to find the breaking point and check recovery.
- **Retesting vs regression?** Retesting confirms a specific bug is fixed. Regression confirms the fix didn't break anything else.
- **What's BVA?** Testing at the edges of a valid range: just below, on, and just above each limit. For 1–10: test 0, 1, 2, 9, 10, 11.
- **Why automate regression but not exploratory?** Regression repeats known checks, which machines do best. Exploratory relies on human judgment to find the unknown.
- **What does 100% code coverage prove?** That every line ran, not that it's correct. Tests still need good assertions.
