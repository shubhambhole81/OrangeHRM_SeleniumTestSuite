package com.orangehrm.automation.pages;

import com.orangehrm.automation.config.Config;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Config.timeoutSeconds()));
    }

    protected WebElement visible(By locator) { return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)); }
    protected WebElement clickable(By locator) { return wait.until(ExpectedConditions.elementToBeClickable(locator)); }
    protected void click(By locator) { clickable(locator).click(); }
    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }
    protected boolean isVisible(By locator) {
        try { return visible(locator).isDisplayed(); }
        catch (org.openqa.selenium.TimeoutException exception) { return false; }
    }
}
