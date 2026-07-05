package pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * The "My Demo App" webview screen - a hybrid screen that loads the Sauce
 * Labs marketing page inside a native WebView component. Useful for
 * demonstrating context-switching between {@code NATIVE_APP} and
 * {@code WEBVIEW_*} contexts, though this page object intentionally stays in
 * the native context and only asserts on the container being present, since
 * flipping context requires Chromedriver to be available on the host running
 * the emulator.
 */
public class WebviewPage extends BasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/action_bar_root")
    private WebElement webviewContainer;

    @AndroidFindBy(className = "android.webkit.WebView")
    private WebElement webViewComponent;

    public WebviewPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayedSafely(webviewContainer) || isDisplayedSafely(webViewComponent);
    }

    public java.util.Set<String> availableContexts() {
        return driver.getContextHandles();
    }

    public CatalogPage goBackToCatalog() {
        pressBack();
        return new CatalogPage(driver);
    }
}
