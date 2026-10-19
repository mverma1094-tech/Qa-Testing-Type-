# Usability Testing Checklist

**What it is:** Checking how easy, efficient, and satisfying the product is for real users. The best usability tests watch 5 real users attempt tasks while thinking out loud. Five users typically uncover most of the major usability problems.

Automated checks (labels, errors, keyboard use, alt text, mobile layout) are in `UsabilityTest.java`. The rest needs people.

---

## Moderated session script (about 20 minutes per user)

**Tasks to give the user (don't explain how):**
1. "Log in using the details on this card."
2. "Find the cheapest product and add it to your cart."
3. "Buy a backpack and a bike light, and get to the order confirmation."
4. "You changed your mind about the bike light. Remove it."

**For each task, record:**

| Task | Completed? (Y / with help / N) | Time | Errors / wrong clicks | User's comments |
|------|--------------------------------|------|----------------------|-----------------|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |
| 4 | | | | |

**After the session:** ask the user to rate "This site was easy to use" from 1 (strongly disagree) to 5 (strongly agree).

---

## Heuristic review (Nielsen's 10 usability heuristics)

| # | Heuristic | Pass / Issue | Notes |
|---|-----------|--------------|-------|
| 1 | Visibility of system status (does the cart badge update right away?) | | |
| 2 | Match with the real world (familiar words like "Cart", "Checkout") | | |
| 3 | User control and freedom (can I cancel, go back, remove items?) | | |
| 4 | Consistency and standards (buttons look and behave the same everywhere) | | |
| 5 | Error prevention (are required fields marked before I submit?) | | |
| 6 | Recognition rather than recall (is my cart visible from every page?) | | |
| 7 | Flexibility and efficiency (keyboard shortcuts, Enter to submit) | | |
| 8 | Aesthetic and minimalist design (no clutter) | | |
| 9 | Help users recognize and recover from errors (plain-language messages) | | |
| 10 | Help and documentation (is help available when needed?) | | |

## Accessibility quick checks (WCAG)

- [ ] Every image has meaningful alt text (automated)
- [ ] Whole flow works with keyboard only (Tab, Enter, Space)
- [ ] Visible focus outline on the selected element
- [ ] Text contrast is high enough (use the browser's Lighthouse or the axe DevTools extension)
- [ ] Page still works at 200% zoom
