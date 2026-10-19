package com.manish.qa.ui.uat;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.CartPage;
import com.manish.qa.ui.pages.CheckoutPage;
import com.manish.qa.ui.pages.InventoryPage;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * USER ACCEPTANCE TESTING (UAT)
 * The final check: does the software meet the BUSINESS need, from the customer's point of view?
 * Normally done by business users or the client, not QA, against acceptance criteria in user stories.
 * These automate the "AC" items from docs/uat-signoff.md; the business still signs off manually.
 *
 * User story: As a shopper, I want to buy products online so that I don't have to visit a store.
 */
@Test(groups = "uat")
public class UserAcceptanceTest extends BaseUiTest {

    @Test(groups = "uat", description = "AC-1: A shopper can complete a purchase and sees a confirmation")
    public void ac1_shopperCompletesPurchase() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Fleece Jacket");
        CheckoutPage checkout = inventory.openCart().checkout()
                .enterInformation("Priya", "Customer", "22030")
                .finish();
        assertEquals(checkout.confirmationHeader(), "Thank you for your order!");
    }

    @Test(groups = "uat", description = "AC-2: The order summary shows exactly what the shopper picked")
    public void ac2_cartShowsChosenItemsOnly() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack").addToCart("Sauce Labs Bolt T-Shirt");
        CartPage cart = inventory.openCart();
        assertEquals(cart.itemNames().size(), 2);
        assertTrue(cart.itemNames().contains("Sauce Labs Backpack"));
        assertTrue(cart.itemNames().contains("Sauce Labs Bolt T-Shirt"));
    }

    @Test(groups = "uat", description = "AC-3: Shipping info is required before payment")
    public void ac3_postalCodeIsRequired() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Onesie");
        CheckoutPage checkout = inventory.openCart().checkout().enterInformation("Priya", "Customer", "");
        assertEquals(checkout.errorText(), "Error: Postal Code is required");
    }

    @Test(groups = "uat", description = "AC-4: The shopper can change their mind and remove an item")
    public void ac4_shopperCanRemoveItem() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack").removeFromCart("Sauce Labs Backpack");
        assertEquals(inventory.cartCount(), 0);
    }
}
