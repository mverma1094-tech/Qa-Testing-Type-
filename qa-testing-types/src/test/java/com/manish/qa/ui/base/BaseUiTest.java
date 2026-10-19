package com.manish.qa.ui.base;

import com.manish.qa.ui.pages.InventoryPage;
import com.manish.qa.ui.pages.LoginPage;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Parent class for every browser test: opens a fresh browser before each test and closes it after.
 * alwaysRun = true makes setup run even when tests are filtered by group (-Dgroups=smoke).
 */
public abstract class BaseUiTest {

    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_USER = "locked_out_user";
    public static final String PASSWORD = "secret_sauce";

    protected WebDriver driver;

    @Parameters({"browser", "width", "height"})
    @BeforeMethod(alwaysRun = true)
    public void startBrowser(@Optional("") String browser,
                             @Optional("1920") String width,
                             @Optional("1080") String height) {
        String chosen = browser.isBlank() ? System.getProperty("browser", "chrome") : browser;
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        driver = DriverFactory.create(chosen, headless);
        driver.manage().window().setSize(new Dimension(Integer.parseInt(width), Integer.parseInt(height)));
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected LoginPage openLoginPage() {
        return new LoginPage(driver).open();
    }

    protected InventoryPage loginAsStandardUser() {
        openLoginPage().loginAs(STANDARD_USER, PASSWORD);
        InventoryPage inventory = new InventoryPage(driver);
        inventory.waitUntilLoaded();
        return inventory;
    }
}
