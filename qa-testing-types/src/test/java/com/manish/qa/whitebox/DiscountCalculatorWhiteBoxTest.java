package com.manish.qa.whitebox;

import com.manish.shop.DiscountCalculator;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.*;

/**
 * WHITE-BOX TESTING
 * Written by READING THE SOURCE CODE of DiscountCalculator. Each test targets a specific
 * branch (B1-B7, marked in the code comments) so that every path is executed at least once.
 * JaCoCo then proves it: open target/site/jacoco/index.html after `mvn test`.
 *
 * White-box finding: in FLAT5 (B4), FLAT5_AMOUNT.min(subtotal) only matters when subtotal < $5,
 * but in that range the 30% cap (B7) always lowers the discount further. So the min() is
 * effectively dead logic. Black-box tests could never notice this; reading the code does.
 */
@Test(groups = "whitebox")
public class DiscountCalculatorWhiteBoxTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    private BigDecimal d(String subtotal, String coupon, boolean member) {
        return calc.calculateDiscount(new BigDecimal(subtotal), coupon, member);
    }

    // B1: guard clause, both conditions of the OR
    public void b1_nullSubtotalThrows() {
        assertThrows(IllegalArgumentException.class, () -> calc.calculateDiscount(null, null, false));
    }

    public void b1_negativeSubtotalThrows() {
        assertThrows(IllegalArgumentException.class, () -> d("-0.01", null, false));
    }

    // B2: empty-string case is reached from null, "" and whitespace-only input
    public void b2_nullBlankAndWhitespaceCouponAllMeanNoCoupon() {
        assertEquals(d("40.00", null, false), new BigDecimal("0.00"));
        assertEquals(d("40.00", "", false), new BigDecimal("0.00"));
        assertEquals(d("40.00", "   ", false), new BigDecimal("0.00"));
    }

    // B3: both outcomes of the minimum-spend if-statement
    public void b3_save10TrueBranch() {
        assertEquals(d("50.00", "SAVE10", false), new BigDecimal("5.00"));
    }

    public void b3_save10FalseBranch() {
        assertEquals(d("49.99", "SAVE10", false), new BigDecimal("0.00"));
    }

    // B4: FLAT5 under the cap
    public void b4_flat5UnderCap() {
        assertEquals(d("20.00", "FLAT5", false), new BigDecimal("5.00"));
    }

    // B4 + B7: FLAT5 at a small subtotal is cut down by the 30% cap
    public void b4_b7_flat5CappedAtThirtyPercent() {
        assertEquals(d("10.00", "FLAT5", false), new BigDecimal("3.00"));
        assertEquals(d("3.00", "FLAT5", false), new BigDecimal("0.90"));
    }

    // B5: default branch, and the message must not echo the input
    public void b5_unknownCouponThrowsGenericMessage() {
        IllegalArgumentException ex = expectThrows(IllegalArgumentException.class,
                () -> d("20.00", "BOGUS", false));
        assertEquals(ex.getMessage(), "Invalid coupon code");
    }

    // B6: member true and false
    public void b6_memberTrueAndFalse() {
        assertEquals(d("100.00", null, true), new BigDecimal("5.00"));
        assertEquals(d("100.00", null, false), new BigDecimal("0.00"));
    }

    // B7 true: FLAT5 + member on a small order exceeds 30%
    public void b7_capAppliesWhenStackedDiscountsTooHigh() {
        assertEquals(d("5.00", "FLAT5", true), new BigDecimal("1.50"));
    }

    // B7 false: largest legal stack (10% + 5%) stays under the cap
    public void b7_capNotAppliedForNormalStack() {
        assertEquals(d("200.00", "SAVE10", true), new BigDecimal("30.00"));
    }

    // Edge path: zero subtotal goes through every branch without dividing or going negative
    public void zeroSubtotalAlwaysZeroDiscount() {
        assertEquals(d("0.00", "FLAT5", true), new BigDecimal("0.00"));
    }

    // Rounding path: HALF_UP on the final setScale
    public void roundingUsesHalfUp() {
        assertEquals(d("69.97", "SAVE10", false), new BigDecimal("7.00")); // 6.997
        assertEquals(d("50.05", "SAVE10", false), new BigDecimal("5.01")); // 5.005
    }
}
