package com.orangehrm.automation.pages;

import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ResetPasswordPage extends BasePage {
    private static final By RESET_HEADING = By.xpath("//h6[normalize-space()='Reset Password']");
    private static final By CANCEL = By.xpath("//button[normalize-space()='Cancel']");
    public ResetPasswordPage(WebDriver driver) { super(driver); }
    public boolean isLoaded() { return isVisible(RESET_HEADING) && isVisible(CANCEL); }
    public boolean hasUsernameField() { return isVisible(By.name("username")); }
    public void cancel() {
        TestSteps.add("Cancel password reset and return to login");
        click(CANCEL);
    }
}
