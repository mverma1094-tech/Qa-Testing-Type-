# User Acceptance Testing (UAT) Plan and Sign-off

**What UAT is:** The last testing phase before release. Business users or the client confirm the software meets their real-world needs. QA prepares the environment and test cases; the **business** executes and decides.

**Types:** Alpha (internal users, at the company), Beta (real customers, in the real world), Contract acceptance, Regulatory acceptance, Operational acceptance.

---

## User story

> **As a** shopper,
> **I want to** buy products online,
> **so that** I don't have to visit a store.

## Acceptance criteria

Automated versions live in `src/test/java/com/manish/qa/ui/uat/UserAcceptanceTest.java`.

| ID | Given / When / Then | Automated test |
|----|---------------------|----------------|
| AC-1 | **Given** I'm logged in with an item in my cart, **when** I enter my details and finish checkout, **then** I see "Thank you for your order!" | `ac1_shopperCompletesPurchase` |
| AC-2 | **Given** I added two products, **when** I open my cart, **then** I see exactly those two products | `ac2_cartShowsChosenItemsOnly` |
| AC-3 | **Given** I'm checking out, **when** I leave the ZIP code blank, **then** I'm told the postal code is required | `ac3_postalCodeIsRequired` |
| AC-4 | **Given** I added a product, **when** I click Remove, **then** my cart is empty | `ac4_shopperCanRemoveItem` |

## Entry criteria (before UAT starts)

- [ ] System and regression testing complete
- [ ] No open Critical or High severity bugs
- [ ] UAT environment matches production
- [ ] Test data and user accounts are ready
- [ ] Business testers trained and available

## UAT execution log

| ID | Tester (business user) | Date | Result (Pass/Fail) | Comments |
|----|------------------------|------|--------------------|----------|
| AC-1 | | | | |
| AC-2 | | | | |
| AC-3 | | | | |
| AC-4 | | | | |

## Exit criteria

- [ ] All acceptance criteria pass
- [ ] Any failures are fixed and re-tested, or formally accepted as known issues
- [ ] Sign-off obtained from the product owner

## Sign-off

| Role | Name | Decision | Signature | Date |
|------|------|----------|-----------|------|
| Product Owner | | Accept / Reject | | |
| Business Lead | | Accept / Reject | | |
| QA Lead | | Accept / Reject | | |
