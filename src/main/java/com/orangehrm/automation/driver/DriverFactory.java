package com.orangehrm.automation.driver;

import com.orangehrm.automation.config.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {
    private DriverFactory() { }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) options.addArguments("--headless=new");
        options.addArguments("--window-size=1440,1000", "--disable-dev-shm-usage", "--no-sandbox");
        return new ChromeDriver(options);
    }
}
