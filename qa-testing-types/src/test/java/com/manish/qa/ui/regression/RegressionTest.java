package com.manish.qa.ui.regression;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.CartPage;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.*;

/**
 * REGRESSION TESTING
 * Re-runs tests for features that ALREADY worked, to catch anything a new change broke.
 * Broad and usually fully automated: runs nightly or before every release.
 * Every bug that gets fixed should earn a regression test so it can never quietly return.
 */
@Test(groups = "regression")
public class RegressionTest extends BaseUiTest {

    public void lockedOutUserIsStillBlocked() {
        LoginPage login = openLoginPage();
        login.loginAs(LOCKED_USER, PASSWORD);
        assertTrue(login.errorText().contains("locked out"));
    }

    public void wrongPasswordShowsError() {
        LoginPage login = openLoginPage();
        login.loginAs(STANDARD_USER, "wrong_password");
        assertTrue(login.errorText().contains("Username and password do not match"));
    }

    public void sortByPriceLowToHigh() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.sortBy("Price (low to high)");
        List<Double> prices = inventory.productPrices();
        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Comparator.naturalOrder());
        assertEquals(prices, expected);
    }

    public void sortByNameZToA() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.sortBy("Name (Z to A)");
        List<String> names = inventory.productNames();
        List<String> expected = new ArrayList<>(names);
        expected.sort(Comparator.reverseOrder());
        assertEquals(names, expected);
    }

    public void cartSurvivesNavigatingAway() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Backpack");
        CartPage cart = inventory.openCart();
        InventoryPage back = cart.continueShopping();
        assertEquals(back.cartCount(), 1, "Cart should keep its item after leaving and coming back");
    }

    public void logoutReturnsToLoginPage() {
        InventoryPage inventory = loginAsStandardUser();
        LoginPage login = inventory.logout();
        assertTrue(login.isLoginButtonVisible());
        assertFalse(login.currentUrl().contains("inventory"));
    }
}
