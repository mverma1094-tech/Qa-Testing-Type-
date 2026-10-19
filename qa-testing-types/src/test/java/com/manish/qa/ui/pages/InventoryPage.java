package com.manish.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class InventoryPage extends BasePage {

    private final By title = By.cssSelector("[data-test='title']");
    private final By itemNames = By.cssSelector("[data-test='inventory-item-name']");
    private final By itemPrices = By.cssSelector("[data-test='inventory-item-price']");
    private final By itemImages = By.cssSelector(".inventory_item_img img");
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");
    private final By cartLink = By.cssSelector("[data-test='shopping-cart-link']");
    private final By sortDropdown = By.cssSelector("[data-test='product-sort-container']");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains("inventory"));
        visible(itemNames);
    }

    public String title() {
        return visible(title).getText();
    }

    public int productCount() {
        visible(itemNames);
        return driver.findElements(itemNames).size();
    }

    /** Sauce Demo button ids are "add-to-cart-" + product name in kebab-case. */
    public InventoryPage addToCart(String productName) {
        click(By.id("add-to-cart-" + kebab(productName)));
        return this;
    }

    public InventoryPage removeFromCart(String productName) {
        click(By.id("remove-" + kebab(productName)));
        return this;
    }

    public int cartCount() {
        return isPresent(cartBadge) ? Integer.parseInt(visible(cartBadge).getText()) : 0;
    }

    public CartPage openCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    public void sortBy(String visibleText) {
        new Select(visible(sortDropdown)).selectByVisibleText(visibleText);
    }

    public List<String> productNames() {
        visible(itemNames);
        return driver.findElements(itemNames).stream().map(WebElement::getText).toList();
    }

    public List<Double> productPrices() {
        visible(itemPrices);
        return driver.findElements(itemPrices).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .toList();
    }

    public List<String> imageAltTexts() {
        visible(itemImages);
        return driver.findElements(itemImages).stream().map(e -> e.getAttribute("alt")).toList();
    }

    public LoginPage logout() {
        click(menuButton);
        click(logoutLink);
        return new LoginPage(driver);
    }

    private static String kebab(String name) {
        return name.toLowerCase().trim().replace(" ", "-");
    }
}
