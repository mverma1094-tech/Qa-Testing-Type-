package com.manish.qa.ui.usability;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.openqa.selenium.Dimension;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * USABILITY TESTING (automatable part)
 * Is the app easy and pleasant to use? True usability testing watches real users try tasks
 * (see docs/usability-checklist.md). Some heuristics CAN be automated, including basic
 * accessibility: clear labels, helpful errors, keyboard support, alt text, mobile layout.
 */
@Test(groups = "usability")
public class UsabilityTest extends BaseUiTest {

    public void loginFieldsTellUsersWhatToType() {
        LoginPage login = openLoginPage();
        assertEquals(login.usernamePlaceholder(), "Username");
        assertEquals(login.passwordPlaceholder(), "Password");
    }

    public void errorMessageIsClearAndCanBeDismissed() {
        LoginPage login = openLoginPage();
        login.loginAs("", "");
        String error = login.errorText();
        assertTrue(error.contains("Username is required"), "Error should say what is wrong: " + error);

        login.dismissError();
        assertFalse(login.isErrorShown(), "User should be able to close the error");
    }

    public void userCanLogInWithKeyboardOnly() {
        LoginPage login = openLoginPage();
        login.loginWithEnterKey(STANDARD_USER, PASSWORD);
        InventoryPage inventory = new InventoryPage(driver);
        inventory.waitUntilLoaded();
        assertEquals(inventory.title(), "Products");
    }

    public void everyProductImageHasAltText() {
        InventoryPage inventory = loginAsStandardUser();
        for (String alt : inventory.imageAltTexts()) {
            assertNotNull(alt, "Screen-reader users need alt text");
            assertFalse(alt.isBlank(), "Alt text should not be empty");
        }
    }

    public void pageFitsOnAPhoneWithoutSideScrolling() {
        driver.manage().window().setSize(new Dimension(375, 812)); // iPhone-sized
        InventoryPage inventory = loginAsStandardUser();
        Boolean fits = (Boolean) inventory.runJs(
                "return document.documentElement.scrollWidth <= window.innerWidth;");
        assertTrue(fits, "Page should not need horizontal scrolling on mobile");
        assertTrue(inventory.productCount() > 0);
    }
}
