package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CatalogPage;
import pages.LoginPage;
import pages.MenuPage;

/**
 * Coverage for the authentication flow: valid login, invalid credential
 * combinations, empty-field validation, and logout.
 */
public class LoginTests extends BaseTest {

    private LoginPage openLoginScreen() {
        MenuPage menu = catalogPage().openMenu();
        Assert.assertTrue(menu.isLoginOptionVisible(),
                "LOG IN menu item should be visible for a logged-out session");
        return menu.goToLogin();
    }

    @Test(description = "A user can log in with valid demo credentials and lands back on the catalog")
    public void validLoginNavigatesToCatalog() {
        LoginPage loginPage = openLoginScreen();
        CatalogPage catalog = loginPage.loginAs(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        Assert.assertTrue(catalog.isDisplayed(),
                "Catalog screen should be displayed immediately after a successful login");
    }

    @Test(description = "Logging in with an unregistered username shows an inline error and stays on the login screen")
    public void invalidUsernameShowsError() {
        LoginPage loginPage = openLoginScreen();
        loginPage.enterUsername(TestData.INVALID_USERNAME)
                .enterPassword(TestData.VALID_PASSWORD)
                .submitInvalidLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error banner should appear for an unrecognized username");
        Assert.assertTrue(loginPage.isDisplayed(),
                "User should remain on the login screen after a failed attempt");
    }

    @Test(description = "Logging in with a valid username but wrong password shows an inline error")
    public void invalidPasswordShowsError() {
        LoginPage loginPage = openLoginScreen();
        loginPage.enterUsername(TestData.VALID_USERNAME)
                .enterPassword(TestData.INVALID_PASSWORD)
                .submitInvalidLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error banner should appear for an incorrect password");
    }

    @Test(description = "Submitting the login form with both fields empty surfaces validation and blocks navigation")
    public void emptyCredentialsAreRejected() {
        LoginPage loginPage = openLoginScreen();
        loginPage.submitInvalidLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error banner should appear when both username and password are empty");
        Assert.assertTrue(loginPage.isDisplayed(),
                "User should remain on the login screen when required fields are empty");
    }

    @Test(description = "Submitting the login form with only the username filled in is rejected")
    public void emptyPasswordIsRejected() {
        LoginPage loginPage = openLoginScreen();
        loginPage.enterUsername(TestData.VALID_USERNAME)
                .submitInvalidLogin();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "An error banner should appear when the password field is left empty");
    }

    @Test(description = "The password field masks input by default and can be revealed via the visibility toggle")
    public void passwordVisibilityToggleWorks() {
        LoginPage loginPage = openLoginScreen();
        loginPage.enterPassword(TestData.VALID_PASSWORD);

        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Password field should be masked by default");

        loginPage.togglePasswordVisibility();
        Assert.assertFalse(loginPage.isPasswordMasked(),
                "Password should become visible after tapping the visibility toggle");
    }

    @Test(description = "A logged-in user can log out via the drawer menu and returns to a logged-out catalog state")
    public void logoutReturnsToLoggedOutState() {
        LoginPage loginPage = openLoginScreen();
        CatalogPage catalog = loginPage.loginAs(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        MenuPage menu = catalog.openMenu();
        Assert.assertTrue(menu.isLogoutOptionVisible(),
                "LOG OUT menu item should be visible for a logged-in session");

        CatalogPage catalogAfterLogout = menu.logout();
        Assert.assertTrue(catalogAfterLogout.isDisplayed(),
                "Catalog should still be reachable/displayed after logging out");

        MenuPage menuAfterLogout = catalogAfterLogout.openMenu();
        Assert.assertTrue(menuAfterLogout.isLoginOptionVisible(),
                "LOG IN menu item should reappear once the session is logged out");
    }
}
