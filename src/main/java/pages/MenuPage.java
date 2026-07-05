package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * The hamburger drawer menu, present on the catalog screen. Provides
 * navigation into Login/Logout, the Webview demo screen, and the Geolocation
 * screen.
 */
public class MenuPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/menuIU")
    private WebElement menuButton;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='LOG IN']")
    private WebElement loginMenuItem;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='LOG OUT']")
    private WebElement logoutMenuItem;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='WEBVIEW']")
    private WebElement webviewMenuItem;

    @AndroidFindBy(xpath = "//android.widget.TextView[@text='GEO LOCATION']")
    private WebElement geoLocationMenuItem;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/user")
    private WebElement loggedInUserLabel;

    public MenuPage(AndroidDriver driver) {
        super(driver);
    }

    public MenuPage open() {
        tap(menuButton);
        return this;
    }

    public LoginPage goToLogin() {
        tap(loginMenuItem);
        return new LoginPage(driver);
    }

    public CatalogPage logout() {
        tap(logoutMenuItem);
        return new CatalogPage(driver);
    }

    public WebviewPage goToWebview() {
        tap(webviewMenuItem);
        return new WebviewPage(driver);
    }

    public void goToGeoLocation() {
        tap(geoLocationMenuItem);
    }

    public boolean isLoginOptionVisible() {
        return isDisplayedSafely(loginMenuItem);
    }

    public boolean isLogoutOptionVisible() {
        return isDisplayedSafely(logoutMenuItem);
    }

    public boolean isUserLoggedInLabelVisible() {
        return isDisplayedSafely(loggedInUserLabel);
    }
}
