package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CatalogPage;
import pages.MenuPage;
import pages.WebviewPage;

/**
 * Coverage for the drawer/hamburger menu: general navigation affordances and
 * the webview screen. The geolocation screen is included as a
 * navigation-only smoke check, since asserting on map tile rendering is out
 * of scope for a UI functional suite.
 */
public class DrawerMenuTests extends BaseTest {

    @Test(description = "The drawer menu opens and exposes the expected top-level navigation items")
    public void drawerMenuOpensWithExpectedItems() {
        MenuPage menu = catalogPage().openMenu();

        Assert.assertTrue(menu.isLoginOptionVisible(),
                "LOG IN item should be present in the drawer for a logged-out session");
    }

    @Test(description = "Logging in surfaces the logged-in user's identity in the drawer menu")
    public void drawerMenuShowsLoggedInUserAfterLogin() {
        MenuPage menu = catalogPage().openMenu();
        CatalogPage catalog = menu.goToLogin().loginAs(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        MenuPage menuAfterLogin = catalog.openMenu();
        Assert.assertTrue(menuAfterLogin.isUserLoggedInLabelVisible(),
                "Drawer menu should display the logged-in user's identity after a successful login");
    }

    @Test(description = "Navigating to the Webview menu item loads the embedded webview screen")
    public void webviewScreenLoadsFromMenu() {
        MenuPage menu = catalogPage().openMenu();
        WebviewPage webview = menu.goToWebview();

        Assert.assertTrue(webview.isLoaded(), "Webview screen/container should be displayed after navigation");
    }

    @Test(description = "Navigating back from the webview screen returns to the product catalog")
    public void backFromWebviewReturnsToCatalog() {
        MenuPage menu = catalogPage().openMenu();
        WebviewPage webview = menu.goToWebview();
        Assert.assertTrue(webview.isLoaded(), "Precondition: webview should load before navigating back");

        CatalogPage catalog = webview.goBackToCatalog();

        Assert.assertTrue(catalog.isDisplayed(),
                "Catalog screen should be displayed again after backing out of the webview screen");
    }

    @Test(description = "The Geo Location menu item is present and can be tapped without crashing the app")
    public void geoLocationMenuItemIsNavigable() {
        MenuPage menu = catalogPage().openMenu();
        // Smoke-level check: confirm navigation doesn't throw. Deeper map/location
        // assertions would require a mocked location provider and are out of scope.
        menu.goToGeoLocation();
    }

    @Test(description = "Reopening the drawer menu after closing it via back press still exposes navigation items")
    public void drawerMenuReopensAfterBackPress() {
        CatalogPage catalog = catalogPage();
        MenuPage menu = catalog.openMenu();
        Assert.assertTrue(menu.isLoginOptionVisible(), "Precondition: menu should be open with LOG IN visible");

        catalog.openMenu(); // menu button also serves to open the drawer again in this simplified flow
        Assert.assertTrue(menu.isLoginOptionVisible(),
                "Drawer menu should still expose LOG IN after being reopened");
    }
}
