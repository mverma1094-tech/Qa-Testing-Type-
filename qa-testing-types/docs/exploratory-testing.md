# Exploratory Testing

**What it is:** Simultaneous learning, test design, and test execution. There is no script. The tester explores the app with a goal (a *charter*) inside a fixed time box, and takes notes as they go.

**Why it can't be fully automated:** Its value comes from human curiosity: noticing something odd and following it. Automation only checks what you already thought of.

**Method used here:** Session-Based Test Management (SBTM). Each session gets one charter, a 45–60 minute time box, and a debrief.

---

## Charters for Sauce Demo (https://www.saucedemo.com)

Sauce Demo ships with special users that have deliberately planted problems. Explore them like you don't know what's wrong.

| # | Charter | Login |
|---|---------|-------|
| C1 | Explore the **product catalog** to discover whether images, names, and prices match each other | `problem_user` |
| C2 | Explore **checkout** with unusual input (long names, emoji, spaces only) to discover validation gaps | `problem_user` |
| C3 | Explore **sorting and filters** to discover inconsistencies between the selected option and the result | `problem_user` |
| C4 | Explore **page layout** to discover visual defects (misaligned buttons, wrong icons, odd styling) | `visual_user` |
| C5 | Explore **adding/removing items and checkout** to discover actions that fail or behave inconsistently | `error_user` |
| C6 | Explore **response times** across pages to discover where the app feels slow | `performance_glitch_user` |

Password for all users: `secret_sauce`

---

## Session sheet (copy one per session)

```
CHARTER:      C1 – product catalog with problem_user
TESTER:       Manish
DATE / TIME:  2026-10-__  10:00–10:45
ENVIRONMENT:  Chrome ___ on Windows 11, 1920x1080

TASK BREAKDOWN
  Session setup:          __%
  Test design & execution: __%
  Bug investigation:       __%

NOTES (what I did, what I saw, what I thought)
  - 10:02 Logged in. Scanned all 6 products ...
  - 10:07 Noticed ...  -> followed up by ...
  -

BUGS FOUND
  BUG-__: <short title>
     Steps: 1. ... 2. ... 3. ...
     Expected: ...
     Actual:   ...
     Severity: High / Medium / Low   Screenshot: ...

QUESTIONS / ISSUES (things that blocked me or need a product decision)
  -

IDEAS FOR NEW AUTOMATED TESTS
  -
```

---

## Heuristics to drive exploration

- **SFDPOT** (San Francisco Depot): Structure, Function, Data, Platform, Operations, Time
- **Goldilocks data:** too small, too big, just right (empty, 1 char, 10,000 chars)
- **Interrupt it:** refresh mid-checkout, press Back after placing an order, open two tabs
- **Follow the money:** does every total, tax, and price add up?
- **Compare users:** do the same steps with `standard_user` vs a problem user and spot the differences

## Turning findings into automation

Every confirmed bug should become a test. Put it in `RegressionTest` once fixed, so it can never quietly come back.
