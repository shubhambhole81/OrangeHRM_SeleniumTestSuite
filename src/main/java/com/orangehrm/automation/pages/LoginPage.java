package com.orangehrm.automation.pages;

import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ALERT = By.cssSelector(".oxd-alert-content-text");
    private static final By FORGOT_PASSWORD = By.cssSelector(".orangehrm-login-forgot-header");
    private static final By FIELD_ERRORS = By.cssSelector(".oxd-input-field-error-message");

    public LoginPage(WebDriver driver) { super(driver); }
    public LoginPage open(String baseUrl) {
        TestSteps.add("Open the OrangeHRM login page");
        driver.get(baseUrl + "/web/index.php/auth/login");
        visible(USERNAME);
        TestSteps.add("Wait for the login form to be ready");
        return this;
    }
    public void login(String username, String password) {
        TestSteps.add("Enter the username and password");
        type(USERNAME, username);
        type(PASSWORD, password);
        TestSteps.add("Submit the login form");
        click(LOGIN_BUTTON);
    }
    public String errorMessage() { return visible(ALERT).getText(); }
    public int fieldErrorCount() {
        wait.until(d -> d.findElements(FIELD_ERRORS).size() == 2);
        return driver.findElements(FIELD_ERRORS).size();
    }
    public void submitEmptyForm() {
        TestSteps.add("Submit the login form without entering credentials");
        click(LOGIN_BUTTON);
    }
    public boolean isLoaded() { return isVisible(USERNAME) && isVisible(LOGIN_BUTTON); }
    public ResetPasswordPage openForgotPassword() {
        TestSteps.add("Open the Forgot Password screen");
        click(FORGOT_PASSWORD);
        return new ResetPasswordPage(driver);
    }
}
