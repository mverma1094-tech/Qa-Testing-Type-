package com.manish.qa.adhoc;

import com.manish.shop.DiscountCalculator;
import com.manish.shop.security.InputValidator;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * AD-HOC TESTING (automated helper)
 * Real ad-hoc testing is informal and unplanned: "let me just try weird stuff and see what breaks".
 * See docs/adhoc-testing.md for the manual log. This class automates the same spirit by
 * throwing random, unplanned inputs (a "monkey test") at the code and checking it never crashes
 * in an unexpected way. The seed is printed so any failure can be reproduced exactly.
 */
@Test(groups = "adhoc")
public class RandomInputAdhocTest {

    private static final long SEED = Long.getLong("adhoc.seed", System.currentTimeMillis());
    private static final String CHARS = "aZ09_ -'\"<>&;%$#@!/\\\t\né中😀";

    private final Random random = new Random(SEED);

    private String randomString(int maxLen) {
        int len = random.nextInt(maxLen + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        return sb.toString();
    }

    public void validatorsNeverCrashOnRandomInput() {
        System.out.println("[adhoc] seed=" + SEED + "  (rerun with -Dadhoc.seed=" + SEED + ")");
        for (int i = 0; i < 5_000; i++) {
            String input = randomString(40);
            InputValidator.isValidUsername(input);
            InputValidator.isValidEmail(input);
            InputValidator.isValidZip(input);
            String safe = InputValidator.escapeHtml(input);
            assertFalse(safe.contains("<") || safe.contains(">"), "Unescaped output for: " + input);
        }
    }

    public void discountIsAlwaysBetweenZeroAndThirtyPercent() {
        String[] coupons = {null, "", "SAVE10", "flat5", " FLAT5 ", "junk", "SAVE10SAVE10", "\u0000"};
        DiscountCalculator calc = new DiscountCalculator();

        for (int i = 0; i < 5_000; i++) {
            BigDecimal subtotal = BigDecimal.valueOf(random.nextInt(100_000), 2); // $0.00 - $999.99
            String coupon = coupons[random.nextInt(coupons.length)];
            boolean member = random.nextBoolean();
            try {
                BigDecimal discount = calc.calculateDiscount(subtotal, coupon, member);
                BigDecimal maxAllowed = subtotal.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
                assertTrue(discount.signum() >= 0, "Negative discount for " + subtotal + "/" + coupon);
                assertTrue(discount.compareTo(maxAllowed) <= 0,
                        "Discount " + discount + " above 30% of " + subtotal + " (seed " + SEED + ")");
            } catch (IllegalArgumentException expectedForBadCoupon) {
                // invalid coupons are allowed to be rejected
            }
        }
    }
}
