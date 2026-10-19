package com.manish.qa.unit;

import com.manish.shop.DiscountCalculator;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;

/**
 * UNIT TESTING
 * Checks each discount rule on its own, using simple happy-path values.
 */
@Test(groups = "unit")
public class DiscountCalculatorTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    private BigDecimal discount(String subtotal, String coupon, boolean member) {
        return calculator.calculateDiscount(new BigDecimal(subtotal), coupon, member);
    }

    public void noCouponNoMemberGivesNoDiscount() {
        assertEquals(discount("100.00", null, false), new BigDecimal("0.00"));
    }

    public void save10GivesTenPercentOffAtOrAboveFifty() {
        assertEquals(discount("100.00", "SAVE10", false), new BigDecimal("10.00"));
    }

    public void flat5GivesFiveDollarsOff() {
        assertEquals(discount("20.00", "FLAT5", false), new BigDecimal("5.00"));
    }

    public void memberGetsFivePercentOff() {
        assertEquals(discount("100.00", null, true), new BigDecimal("5.00"));
    }

    public void couponAndMemberDiscountsStack() {
        assertEquals(discount("100.00", "SAVE10", true), new BigDecimal("15.00"));
    }

    public void couponCodeIsCaseInsensitiveAndTrimmed() {
        assertEquals(discount("100.00", "  save10 ", false), new BigDecimal("10.00"));
    }
}
