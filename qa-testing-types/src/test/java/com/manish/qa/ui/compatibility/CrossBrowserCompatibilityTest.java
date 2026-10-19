package com.manish.qa.ui.compatibility;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.InventoryPage;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * COMPATIBILITY TESTING
 * Same tests, different environments: browsers (Chrome, Firefox, Edge) and screen sizes
 * (desktop, tablet, phone). suites/compatibility.xml runs this class once per combination.
 * Run with: mvn test -Pcompatibility
 * (Teams scale this up with Selenium Grid, BrowserStack or Sauce Labs for real devices and OSes.)
 */
@Test(groups = "compatibility")
public class CrossBrowserCompatibilityTest extends BaseUiTest {

    public void coreShoppingFlowWorks() {
        printEnvironment();
        InventoryPage inventory = loginAsStandardUser();
        assertEquals(inventory.title(), "Products");
        inventory.addToCart("Sauce Labs Backpack");
        assertEquals(inventory.cartCount(), 1);
        assertEquals(inventory.openCart().itemNames().get(0), "Sauce Labs Backpack");
    }

    public void layoutHasNoHorizontalOverflow() {
        InventoryPage inventory = loginAsStandardUser();
        Boolean fits = (Boolean) inventory.runJs(
                "return document.documentElement.scrollWidth <= window.innerWidth;");
        assertTrue(fits, "Content overflows the screen width in this browser/size");
        assertEquals(inventory.productCount(), 6);
    }

    public void sortingWorksInThisBrowser() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.sortBy("Price (high to low)");
        assertEquals(inventory.productPrices().get(0), 49.99, 0.001);
    }

    private void printEnvironment() {
        Capabilities caps = ((HasCapabilities) driver).getCapabilities();
        System.out.printf("[compatibility] %s %s on %s, window %s%n",
                caps.getBrowserName(), caps.getBrowserVersion(), caps.getPlatformName(),
                driver.manage().window().getSize());
    }
}
