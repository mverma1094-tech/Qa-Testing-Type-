# Ad-hoc Testing

**What it is:** Informal, unplanned testing with no documentation, charter, or time box. The tester uses experience and intuition to try to break the app ("error guessing").

**Exploratory vs ad-hoc:** Both are unscripted. Exploratory testing is *structured* (charter, time box, notes, debrief). Ad-hoc testing is *unstructured*: quick, random, done whenever there's a spare moment.

**Variations:**
- **Buddy testing:** a developer and a tester try things together right after a feature is built
- **Pair testing:** two testers share one screen, one drives and one suggests ideas
- **Monkey testing:** random input with no knowledge of the app (automated in `RandomInputAdhocTest.java`)

---

## Error-guessing ideas to try

- Double-click every button quickly (duplicate orders?)
- Press browser **Back** after finishing checkout, then **Forward**
- Paste a 5,000-character string into a form field
- Type only spaces into the first name, last name, and ZIP fields
- Emoji, accented letters, and Chinese characters in names: `José 😀 张伟`
- Change the URL by hand: `/checkout-step-two.html` with an empty cart
- Log in, open a second tab, log out in one tab, keep shopping in the other
- Zoom the browser to 300% and 25%
- Turn off Wi-Fi mid-checkout, then turn it back on

---

## Ad-hoc log

Ad-hoc testing isn't formally documented, but jot down anything you find so it isn't lost.

| Date | What I tried | What happened | Bug? |
|------|--------------|---------------|------|
| | | | |
| | | | |
| | | | |

---

## Running the automated monkey test

```bash
mvn test -Dgroups=adhoc
```

The output prints a seed. If a run fails, repeat it exactly with:

```bash
mvn test -Dgroups=adhoc -Dadhoc.seed=<seed from the log>
```
