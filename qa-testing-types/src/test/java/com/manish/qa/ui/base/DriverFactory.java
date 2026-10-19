package com.manish.qa.ui.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

/** Builds a browser. Selenium Manager downloads the right driver automatically. */
public final class DriverFactory {

    private DriverFactory() { }

    public static WebDriver create(String browser, boolean headless) {
        return switch (browser.toLowerCase()) {
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                yield new FirefoxDriver(o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                yield new EdgeDriver(o);
            }
            case "chrome" -> {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                // Stop Chrome's password-manager popups from blocking clicks on the demo site
                o.setExperimentalOption("prefs", Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false));
                o.addArguments("--disable-features=PasswordLeakDetection");
                yield new ChromeDriver(o);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}
