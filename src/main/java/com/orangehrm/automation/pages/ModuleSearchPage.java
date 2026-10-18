package com.orangehrm.automation.pages;

import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Common read-only list search controls used by several OrangeHRM modules. */
public class ModuleSearchPage extends BasePage {
    private static final By SEARCH = By.xpath("//button[normalize-space()='Search']");
    private static final By RESET = By.xpath("//button[normalize-space()='Reset']");

    public ModuleSearchPage(WebDriver driver) { super(driver); }

    public boolean hasSearchAndResetControls() {
        return isVisible(SEARCH) && isVisible(RESET);
    }

    public void searchWithCurrentFilters() {
        TestSteps.add("Submit the current list filters");
        click(SEARCH);
    }

    public void resetFilters() {
        TestSteps.add("Reset the list filters");
        click(RESET);
    }
}
