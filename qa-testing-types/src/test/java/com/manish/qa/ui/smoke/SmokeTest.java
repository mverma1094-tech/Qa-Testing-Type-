package com.manish.qa.ui.smoke;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * SMOKE TESTING ("build verification")
 * Run FIRST on every new build. A few shallow checks of the most critical paths:
 * does the site load, can someone log in, are products there?
 * If smoke fails, the build is rejected and nothing else is tested. Target: under 2 minutes.
 */
@Test(groups = "smoke")
public class SmokeTest extends BaseUiTest {

    public void siteLoadsAndShowsLoginForm() {
        LoginPage login = openLoginPage();
        assertTrue(login.isLoginButtonVisible(), "Login button should be visible");
    }

    public void validUserCanLogIn() {
        InventoryPage inventory = loginAsStandardUser();
        assertEquals(inventory.title(), "Products");
    }

    public void productCatalogIsNotEmpty() {
        InventoryPage inventory = loginAsStandardUser();
        assertEquals(inventory.productCount(), 6, "Sauce Demo sells 6 products");
    }
}
