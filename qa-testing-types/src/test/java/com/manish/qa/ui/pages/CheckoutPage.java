package com.manish.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Covers checkout step one (your info), step two (overview) and the complete page. */
public class CheckoutPage extends BasePage {

    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By finishButton = By.id("finish");
    private final By itemTotal = By.cssSelector("[data-test='subtotal-label']");
    private final By completeHeader = By.cssSelector("[data-test='complete-header']");
    private final By error = By.cssSelector("[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage enterInformation(String first, String last, String zip) {
        type(firstName, first);
        type(lastName, last);
        type(postalCode, zip);
        click(continueButton);
        return this;
    }

    /** "Item total: $39.98" -> 39.98 */
    public double itemTotal() {
        String text = visible(itemTotal).getText();
        return Double.parseDouble(text.substring(text.indexOf('$') + 1));
    }

    public CheckoutPage finish() {
        click(finishButton);
        return this;
    }

    public String confirmationHeader() {
        return visible(completeHeader).getText();
    }

    public String errorText() {
        return visible(error).getText();
    }

    public String firstNameValue() {
        return visible(firstName).getAttribute("value");
    }
}
