package com.manish.qa.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By error = By.cssSelector("[data-test='error']");
    private final By errorCloseButton = By.cssSelector("[data-test='error-button']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(BASE_URL);
        visible(loginButton);
        return this;
    }

    public void loginAs(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(loginButton);
    }

    /** Keyboard-only login: type, then press Enter instead of clicking. */
    public void loginWithEnterKey(String user, String pass) {
        type(username, user);
        type(password, pass);
        visible(password).sendKeys(Keys.ENTER);
    }

    public String errorText() {
        return visible(error).getText();
    }

    public boolean isErrorShown() {
        return isPresent(error);
    }

    public void dismissError() {
        click(errorCloseButton);
    }

    public boolean isLoginButtonVisible() {
        return isPresent(loginButton);
    }

    public String usernamePlaceholder() {
        return visible(username).getAttribute("placeholder");
    }

    public String passwordPlaceholder() {
        return visible(password).getAttribute("placeholder");
    }

    public String passwordFieldType() {
        return visible(password).getAttribute("type");
    }
}
