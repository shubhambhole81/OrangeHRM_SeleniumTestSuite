package com.orangehrm.automation.tests;

import com.orangehrm.automation.base.BaseTest;
import com.orangehrm.automation.config.Config;
import com.orangehrm.automation.pages.DashboardPage;
import com.orangehrm.automation.pages.LoginPage;
import com.orangehrm.automation.pages.ResetPasswordPage;
import com.orangehrm.automation.reporting.TestSteps;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(com.orangehrm.automation.listeners.TestListener.class)
public class AuthenticationTests extends BaseTest {
    @Test(description = "Valid user can sign in and sign out")
    public void validLoginAndLogout() {
        LoginPage login = new LoginPage(driver).open(Config.baseUrl());
        login.login(Config.username(), Config.password());
        DashboardPage dashboard = new DashboardPage(driver);
        TestSteps.add("Verify that the authenticated dashboard is displayed");
        Assert.assertTrue(dashboard.isLoaded(), "Dashboard should be displayed after valid login");
        dashboard.logout();
        TestSteps.add("Verify that logout returns to the login page");
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Login form should be displayed after logout");
    }

    @Test(description = "Invalid password is rejected with a visible message")
    public void invalidLoginShowsError() {
        LoginPage login = new LoginPage(driver).open(Config.baseUrl());
        login.login(Config.username(), "invalid-password");
        TestSteps.add("Verify the invalid-credentials message is displayed");
        Assert.assertEquals(login.errorMessage(), "Invalid credentials");
    }

    @Test(description = "Unknown username is rejected")
    public void unknownUsernameShowsError() {
        LoginPage login = new LoginPage(driver).open(Config.baseUrl());
        login.login("unknown-user", Config.password());
        TestSteps.add("Verify the unknown username is rejected");
        Assert.assertEquals(login.errorMessage(), "Invalid credentials");
    }

    @Test(description = "Required field validation is shown for an empty login form")
    public void emptyLoginShowsRequiredErrors() {
        LoginPage login = new LoginPage(driver).open(Config.baseUrl());
        login.submitEmptyForm();
        TestSteps.add("Verify required-field messages appear for both credentials");
        Assert.assertEquals(login.fieldErrorCount(), 2, "Both username and password should be required");
    }

    @Test(description = "Forgot password opens the reset form")
    public void forgotPasswordOpensResetForm() {
        LoginPage login = new LoginPage(driver).open(Config.baseUrl());
        ResetPasswordPage reset = login.openForgotPassword();
        TestSteps.add("Verify the reset screen and username field are displayed");
        Assert.assertTrue(reset.isLoaded(), "Reset Password screen should be displayed");
        Assert.assertTrue(reset.hasUsernameField(), "Reset screen should contain an input field");
        reset.cancel();
        TestSteps.add("Verify Cancel returns to the login page");
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Cancel should return to the login page");
    }
}
