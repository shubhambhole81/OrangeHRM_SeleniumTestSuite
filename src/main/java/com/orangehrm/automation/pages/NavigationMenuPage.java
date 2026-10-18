package com.orangehrm.automation.pages;

import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NavigationMenuPage extends BasePage {
    public NavigationMenuPage(WebDriver driver) { super(driver); }
    public void openModule(String module, String expectedUrlSegment) {
        TestSteps.add("Open the " + module + " module from the main menu");
        click(By.xpath("//span[contains(@class,'oxd-main-menu-item--name') and normalize-space()='" + module + "']"));
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/" + expectedUrlSegment + "/"));
        TestSteps.add("Wait for the " + module + " module page to load");
    }
    public boolean headingIs(String heading) { return isVisible(By.xpath("//h6[normalize-space()='" + heading + "']")); }
}
