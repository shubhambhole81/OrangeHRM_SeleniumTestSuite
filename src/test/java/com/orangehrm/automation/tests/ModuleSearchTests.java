package com.orangehrm.automation.tests;

import com.orangehrm.automation.base.BaseTest;
import com.orangehrm.automation.config.Config;
import com.orangehrm.automation.pages.LoginPage;
import com.orangehrm.automation.pages.ModuleSearchPage;
import com.orangehrm.automation.pages.NavigationMenuPage;
import com.orangehrm.automation.reporting.TestSteps;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** Read-only list filtering scenarios; these tests never create or edit HR records. */
@Listeners(com.orangehrm.automation.listeners.TestListener.class)
public class ModuleSearchTests extends BaseTest {
    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        new LoginPage(driver).open(Config.baseUrl()).login(Config.username(), Config.password());
    }

    @DataProvider(name = "searchableModules")
    public Object[][] searchableModules() {
        return new Object[][] {
            {"Admin", "admin"}, {"PIM", "pim"}, {"Leave", "leave"},
            {"Recruitment", "recruitment"}, {"Performance", "performance"}, {"Directory", "directory"}
        };
    }

    @Test(dataProvider = "searchableModules", description = "Module list search and reset controls work")
    public void searchAndResetListFilters(String module, String route) {
        new NavigationMenuPage(driver).openModule(module, route);
        ModuleSearchPage searchPage = new ModuleSearchPage(driver);

        TestSteps.add("Verify Search and Reset controls are available");
        Assert.assertTrue(searchPage.hasSearchAndResetControls(),
                module + " should provide Search and Reset controls");
        searchPage.searchWithCurrentFilters();
        TestSteps.add("Verify submitting filters keeps the user on the module page");
        Assert.assertTrue(driver.getCurrentUrl().contains("/" + route + "/"),
                module + " search should keep the user in the module");
        searchPage.resetFilters();
        TestSteps.add("Verify Search and Reset remain available after resetting filters");
        Assert.assertTrue(searchPage.hasSearchAndResetControls(),
                module + " filter controls should remain available after reset");
    }
}
