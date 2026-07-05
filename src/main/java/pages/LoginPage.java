package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * "My Demo App" login screen, reached via the drawer menu > "Log In".
 * <p>
 * The app ships a biometric-style login flow: a username field, a password
 * field, a visible/hidden password toggle, and a submit button, plus an
 * inline error banner for bad credentials.
 */
public class LoginPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/nameField")
    private WebElement usernameField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordField")
    private WebElement passwordField;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginBtn")
    private WebElement loginButton;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/passwordVisibleIcon")
    private WebElement passwordVisibilityToggle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/errorMessageField")
    private WebElement errorMessageBanner;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/loginText")
    private WebElement loginScreenTitle;

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return isDisplayedSafely(loginScreenTitle);
    }

    public LoginPage enterUsername(String username) {
        typeText(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeText(passwordField, password);
        return this;
    }

    public void togglePasswordVisibility() {
        tap(passwordVisibilityToggle);
    }

    public boolean isPasswordMasked() {
        // The demo app renders the raw text differently under the hood, but
        // exposes it as a normal attribute we can assert against.
        String type = passwordField.getAttribute("password");
        return Boolean.parseBoolean(type);
    }

    public CatalogPage submitValidLogin() {
        tap(loginButton);
        return new CatalogPage(driver);
    }

    public LoginPage submitInvalidLogin() {
        tap(loginButton);
        return this;
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayedSafely(errorMessageBanner);
    }

    public String getErrorMessageText() {
        return waitVisible(errorMessageBanner).getText();
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }

    /** Convenience helper combining the common happy-path login flow. */
    public CatalogPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return submitValidLogin();
    }
}
