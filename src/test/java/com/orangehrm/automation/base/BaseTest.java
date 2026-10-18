package com.orangehrm.automation.base;

import com.orangehrm.automation.driver.DriverFactory;
import com.orangehrm.automation.listeners.TestListener;
import com.orangehrm.automation.reporting.TestSteps;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;

public abstract class BaseTest {
    protected WebDriver driver;
    public WebDriver getDriver() { return driver; }
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        TestSteps.clear();
        TestSteps.add("Start a Chrome browser session");
        driver = DriverFactory.createDriver();
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        TestListener.recordFromTestTeardown(result, driver);
        if (driver != null) driver.quit();
        TestSteps.clear();
    }
}
