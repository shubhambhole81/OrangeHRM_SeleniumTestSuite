package com.orangehrm.automation.pages;

import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {
    private static final By DASHBOARD_HEADING = By.xpath("//h6[normalize-space()='Dashboard']");
    private static final By USER_MENU = By.cssSelector(".oxd-userdropdown-tab");
    private static final By LOGOUT = By.xpath("//a[normalize-space()='Logout']");
    public DashboardPage(WebDriver driver) { super(driver); }
    public boolean isLoaded() { return isVisible(DASHBOARD_HEADING); }
    public void logout() {
        TestSteps.add("Open the user menu and select Logout");
        click(USER_MENU);
        click(LOGOUT);
    }
}
