package com.manish.qa.blackbox;

import com.manish.shop.Cart;
import com.manish.shop.DiscountCalculator;
import com.manish.shop.security.InputValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.manish.qa.support.TestData.BACKPACK;
import static org.testng.Assert.*;

/**
 * BLACK-BOX TESTING
 * Written ONLY from the requirements, without looking at the code:
 *   "1-10 units per product", "SAVE10 needs a $50 minimum", "ZIP is 12345 or 12345-6789".
 * Uses the two classic techniques:
 *   - Boundary Value Analysis (BVA): test just below, on, and just above each limit
 *   - Equivalence Partitioning (EP): one representative value per group of inputs that behave the same
 */
public class BlackBoxTechniquesTest {

    // ---------- BVA: cart quantity, valid range 1..10 ----------
    @DataProvider
    public Object[][] quantityBoundaries() {
        return new Object[][]{
                {-1, false}, {0, false},   // below lower boundary
                {1, true}, {2, true},      // on / just above lower boundary
                {9, true}, {10, true},     // just below / on upper boundary
                {11, false}                // above upper boundary
        };
    }

    @Test(groups = "blackbox", dataProvider = "quantityBoundaries")
    public void cartQuantityBoundaries(int qty, boolean shouldBeAccepted) {
        Cart cart = new Cart();
        boolean accepted;
        try {
            cart.addItem(BACKPACK, qty);
            accepted = true;
        } catch (IllegalArgumentException e) {
            accepted = false;
        }
        assertEquals(accepted, shouldBeAccepted, "Quantity " + qty);
    }

    @Test(groups = "blackbox")
    public void cumulativeQuantityCannotPassTen() {
        Cart cart = new Cart();
        cart.addItem(BACKPACK, 10);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(BACKPACK, 1));
    }

    // ---------- BVA: SAVE10 minimum spend of $50.00 ----------
    @DataProvider
    public Object[][] save10Boundaries() {
        return new Object[][]{
                {"49.99", "0.00"},
                {"50.00", "5.00"},
                {"50.01", "5.00"}
        };
    }

    @Test(groups = "blackbox", dataProvider = "save10Boundaries")
    public void save10MinimumSpendBoundary(String subtotal, String expectedDiscount) {
        BigDecimal actual = new DiscountCalculator().calculateDiscount(new BigDecimal(subtotal), "SAVE10", false);
        assertEquals(actual, new BigDecimal(expectedDiscount), "Subtotal " + subtotal);
    }

    // ---------- EP: coupon codes ----------
    @DataProvider
    public Object[][] couponPartitions() {
        return new Object[][]{
                {"SAVE10", true},   // valid code
                {"save10", true},   // valid code, different case
                {"FLAT5", true},    // second valid code
                {null, true},       // no code
                {"SAVE20", false},  // invalid: unknown code
                {"SAVE 10", false}, // invalid: malformed
                {"%%%", false}      // invalid: symbols
        };
    }

    @Test(groups = "blackbox", dataProvider = "couponPartitions")
    public void couponEquivalenceClasses(String coupon, boolean shouldBeAccepted) {
        boolean accepted;
        try {
            new DiscountCalculator().calculateDiscount(new BigDecimal("60.00"), coupon, false);
            accepted = true;
        } catch (IllegalArgumentException e) {
            accepted = false;
        }
        assertEquals(accepted, shouldBeAccepted, "Coupon " + coupon);
    }

    // ---------- EP + BVA: ZIP code ----------
    @DataProvider
    public Object[][] zipPartitions() {
        return new Object[][]{
                {"20151", true},        // valid 5-digit
                {"20151-1234", true},   // valid ZIP+4
                {"2015", false},        // 4 digits (boundary)
                {"201511", false},      // 6 digits (boundary)
                {"20151-123", false},   // short +4 part
                {"ABCDE", false},       // letters
                {"", false}             // empty
        };
    }

    @Test(groups = "blackbox", dataProvider = "zipPartitions")
    public void zipCodePartitions(String zip, boolean expectedValid) {
        assertEquals(InputValidator.isValidZip(zip), expectedValid, "ZIP '" + zip + "'");
    }

    // ---------- BVA: username length 3..20 ----------
    @DataProvider
    public Object[][] usernameLengths() {
        return new Object[][]{
                {2, false}, {3, true}, {20, true}, {21, false}
        };
    }

    @Test(groups = "blackbox", dataProvider = "usernameLengths")
    public void usernameLengthBoundaries(int length, boolean expectedValid) {
        assertEquals(InputValidator.isValidUsername("a".repeat(length)), expectedValid, "Length " + length);
    }
}
