package com.manish.qa.ui.security;

import com.manish.qa.ui.base.BaseUiTest;
import com.manish.qa.ui.pages.BasePage;
import com.manish.qa.ui.pages.CheckoutPage;
import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.openqa.selenium.NoAlertPresentException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * SECURITY TESTING (browser level)
 * Basic checks a QA tester can automate: injection payloads, access control,
 * masked passwords, XSS. Deeper testing uses tools like OWASP ZAP or Burp Suite.
 * Only run security tests against sites you own or are allowed to test (Sauce Demo is a public practice site).
 */
public class LoginSecurityUiTest extends BaseUiTest {

    @DataProvider
    public Object[][] sqlInjectionPayloads() {
        return new Object[][]{
                {"' OR '1'='1"},
                {"admin'--"},
                {"\" OR \"\"=\""}
        };
    }

    @Test(groups = "security-ui", dataProvider = "sqlInjectionPayloads")
    public void sqlInjectionDoesNotBypassLogin(String payload) {
        LoginPage login = openLoginPage();
        login.loginAs(payload, payload);
        assertTrue(login.isErrorShown(), "Injection payload must not log in");
        assertFalse(login.currentUrl().contains("inventory"));
    }

    @Test(groups = "security-ui")
    public void protectedPageRequiresLogin() {
        driver.get(BasePage.BASE_URL + "inventory.html");
        LoginPage login = new LoginPage(driver);
        assertTrue(login.errorText().contains("You can only access"),
                "Direct URL access without login must be blocked");
    }

    @Test(groups = "security-ui")
    public void passwordIsMasked() {
        assertEquals(openLoginPage().passwordFieldType(), "password");
    }

    @Test(groups = "security-ui")
    public void xssPayloadInCheckoutFormDoesNotExecute() {
        InventoryPage inventory = loginAsStandardUser();
        inventory.addToCart("Sauce Labs Onesie");
        CheckoutPage checkout = inventory.openCart().checkout();
        checkout.enterInformation("<script>alert('xss')</script>", "<img src=x onerror=alert(1)>", "20151");

        try {
            driver.switchTo().alert();
            fail("XSS payload executed: a JavaScript alert appeared");
        } catch (NoAlertPresentException expected) {
            // good: the script was treated as text
        }
    }

    @Test(groups = "security-ui")
    public void protectedPageBlockedAfterLogout() {
        loginAsStandardUser().logout();
        driver.get(BasePage.BASE_URL + "inventory.html");
        assertTrue(new LoginPage(driver).errorText().contains("You can only access"),
                "Session must end on logout");
    }
}
