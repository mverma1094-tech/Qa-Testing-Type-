package com.manish.qa.unit;

import com.manish.shop.Cart;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.manish.qa.support.TestData.*;
import static org.testng.Assert.*;

/**
 * UNIT TESTING
 * Tests one class (Cart) in isolation: no database, no network, no browser.
 * Fast (milliseconds) and run on every code change. Usually written by developers or SDETs.
 */
@Test(groups = "unit")
public class CartTest {

    private Cart cart;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        cart = new Cart();
    }

    public void newCartIsEmpty() {
        assertTrue(cart.isEmpty());
        assertEquals(cart.getItemCount(), 0);
        assertEquals(cart.getSubtotal(), new BigDecimal("0.00"));
    }

    public void addingItemUpdatesQuantityAndCount() {
        cart.addItem(BACKPACK, 2);
        assertEquals(cart.getQuantity(BACKPACK), 2);
        assertEquals(cart.getItemCount(), 2);
    }

    public void addingSameProductTwiceMergesQuantity() {
        cart.addItem(BACKPACK, 1);
        cart.addItem(BACKPACK, 3);
        assertEquals(cart.getQuantity(BACKPACK), 4);
        assertEquals(cart.getItems().size(), 1, "Same product should be one cart line");
    }

    public void subtotalSumsPriceTimesQuantity() {
        cart.addItem(BACKPACK, 2);    // 59.98
        cart.addItem(BIKE_LIGHT, 1);  //  9.99
        assertEquals(cart.getSubtotal(), new BigDecimal("69.97"));
    }

    public void removeItemDeletesTheLine() {
        cart.addItem(BACKPACK, 1);
        cart.addItem(ONESIE, 1);
        cart.removeItem(BACKPACK);
        assertEquals(cart.getQuantity(BACKPACK), 0);
        assertEquals(cart.getItemCount(), 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void zeroQuantityIsRejected() {
        cart.addItem(BACKPACK, 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void nullProductIsRejected() {
        cart.addItem(null, 1);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void itemsViewCannotBeModifiedFromOutside() {
        cart.addItem(BACKPACK, 1);
        cart.getItems().clear();
    }
}
