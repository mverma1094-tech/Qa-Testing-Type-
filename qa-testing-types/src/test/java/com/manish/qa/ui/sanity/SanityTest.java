package com.manish.qa.ui.sanity;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.CartPage;
import com.manish.qa.ui.pages.InventoryPage;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * SANITY TESTING
 * Narrow and deep, right after a SPECIFIC fix or small change.
 * Scenario: developers just shipped a fix for "cart badge shows the wrong count".
 * Sanity checks only that area works before spending time on a full regression run.
 * Smoke = "is the whole build alive?"  Sanity = "does this one fix make sense?"
 */
@Test(groups = "sanity")
public class SanityTest extends BaseUiTest {

    public void cartBadgeCountsUpAndDownCorrectly() {
        InventoryPage inventory = loginAsStandardUser();

        inventory.addToCart("Sauce Labs Backpack");
        assertEquals(inventory.cartCount(), 1);

        inventory.addToCart("Sauce Labs Bike Light");
        assertEquals(inventory.cartCount(), 2);

        inventory.removeFromCart("Sauce Labs Backpack");
        assertEquals(inventory.cartCount(), 1);

        inventory.removeFromCart("Sauce Labs Bike Light");
        assertEquals(inventory.cartCount(), 0, "Badge should disappear when cart is empty");
    }

    public void cartPageMatchesTheBadge() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Onesie").addToCart("Sauce Labs Fleece Jacket");

        CartPage cart = inventory.openCart();
        assertEquals(cart.itemNames().size(), 2);
        assertTrue(cart.itemNames().contains("Sauce Labs Onesie"));
        assertTrue(cart.itemNames().contains("Sauce Labs Fleece Jacket"));
    }
}
