package com.orangehrm.automation.tests;

import com.orangehrm.automation.base.BaseTest;
import com.orangehrm.automation.config.Config;
import com.orangehrm.automation.pages.DashboardPage;
import com.orangehrm.automation.pages.LoginPage;
import com.orangehrm.automation.pages.NavigationMenuPage;
import com.orangehrm.automation.reporting.TestSteps;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(com.orangehrm.automation.listeners.TestListener.class)
public class NavigationTests extends BaseTest {
    @BeforeMethod(alwaysRun = true)
    public void signIn() {
        new LoginPage(driver).open(Config.baseUrl()).login(Config.username(), Config.password());
        TestSteps.add("Verify login reaches the dashboard before module navigation");
        Assert.assertTrue(new DashboardPage(driver).isLoaded(), "Login should reach the dashboard");
    }

    @DataProvider(name = "mainMenuModules")
    public Object[][] mainMenuModules() {
        return new Object[][] {
            {"Admin", "admin"}, {"PIM", "pim"}, {"Leave", "leave"}, {"Time", "time"},
            {"Recruitment", "recruitment"}, {"My Info", "pim"}, {"Performance", "performance"},
            {"Directory", "directory"}
        };
    }

    @Test(dataProvider = "mainMenuModules", description = "A main menu module opens successfully")
    public void mainMenuModuleOpens(String module, String urlSegment) {
        new NavigationMenuPage(driver).openModule(module, urlSegment);
        TestSteps.add("Verify the browser is on the " + module + " module route");
        Assert.assertTrue(driver.getCurrentUrl().contains("/" + urlSegment + "/"),
                module + " should open its expected module route");
    }
}
