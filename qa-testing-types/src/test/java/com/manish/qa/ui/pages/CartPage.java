package com.manish.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CartPage extends BasePage {

    private final By itemNames = By.cssSelector("[data-test='inventory-item-name']");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.urlContains("cart"));
    }

    public List<String> itemNames() {
        return driver.findElements(itemNames).stream().map(WebElement::getText).toList();
    }

    public CheckoutPage checkout() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        InventoryPage page = new InventoryPage(driver);
        page.waitUntilLoaded();
        return page;
    }
}
